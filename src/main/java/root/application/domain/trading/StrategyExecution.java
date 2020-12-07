package root.application.domain.trading;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.*;
import root.application.domain.history.TradeContext;
import root.application.domain.history.TradeHistoryItemBuilder;
import root.application.domain.history.TradeHistoryItemRepository;
import root.application.domain.strategy.StrategyFactory;

import java.util.UUID;

import static org.ta4j.core.Order.OrderType.BUY;
import static org.ta4j.core.Order.OrderType.SELL;
import static root.application.domain.trading.StrategyExecutionStatus.*;

@Slf4j
public class StrategyExecution
{
    private static final String STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE =
        "Execution of strategy [{}] for exchange gateway [{}] with {}";

    @Getter
    private final String id;
    private final long startTime;
    private final StrategyFactory strategyFactory;
    private final Strategy strategy;
    private final BarSeries series;
    private final ExchangeGateway exchangeGateway;
    private final TradingRecord tradingRecord;
    private final TradeHistoryItemBuilder tradeHistoryItemBuilder;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;
    private final StrategyExecutionContext strategyExecutionContext;
    private volatile StrategyExecutionStatus status;

    public StrategyExecution(StrategyFactory strategyFactory,
                             ExchangeGateway exchangeGateway,
                             StrategyExecutionContext strategyExecutionContext,
                             TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        this.id = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
        this.strategyFactory = strategyFactory;
        this.strategy = strategyFactory.create();
        this.series = strategyFactory.getBarSeries();
        this.exchangeGateway = exchangeGateway;
        this.tradingRecord = new BaseTradingRecord();
        this.tradeHistoryItemBuilder = new TradeHistoryItemBuilder();
        this.tradeHistoryItemRepository = tradeHistoryItemRepository;
        this.strategyExecutionContext = strategyExecutionContext;
        run();
    }

    public synchronized void stop()
    {
        if (status.equals(WAITING_FOR_ENTRY))
        {
            this.status = STOPPED;
            logExecutionStoppage();
        }
        else if (status.equals(WAITING_FOR_EXIT))
        {
            this.status = STOPPING;
            logExecutionStopping();
        }
    }

    public synchronized boolean isActive()
    {
        return status.equals(WAITING_FOR_ENTRY) ||
            status.equals(WAITING_FOR_EXIT) ||
            status.equals(STOPPING);
    }

    public synchronized State getState()
    {
        return State.builder()
            .id(id)
            .startTime(startTime)
            .status(status)
            .strategyName(strategyFactory.getStrategyName())
            .symbol(strategyExecutionContext.getSymbol())
            .amount(strategyExecutionContext.getAmount())
            .interval(strategyExecutionContext.getInterval())
            .build();
    }

    private void run()
    {
        this.status = WAITING_FOR_ENTRY;
        var interval = strategyExecutionContext.getInterval();
        exchangeGateway.subscribeToBarStream(interval, this::processBar);
        log.info(STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE + " has been run successfully.",
            strategyFactory.getStrategyName(), exchangeGateway.getName(), strategyExecutionContext);
    }

    private synchronized void processBar(Bar bar)
    {
        series.addBar(bar);
        var currentBarIndex = series.getEndIndex();
        var tradingOperationContext = buildTradingOperationContext(bar);
        if (shouldBuy(currentBarIndex))
        {
            buy(currentBarIndex, tradingOperationContext);
            return;
        }
        if (shouldSell(currentBarIndex))
        {
            sell(currentBarIndex, tradingOperationContext);
            recordLastTradeToHistory();
        }
    }

    private boolean shouldBuy(int currentBarIndex)
    {
        return status.equals(WAITING_FOR_ENTRY) &&
            strategy.shouldEnter(currentBarIndex) &&
            tradingRecord.getCurrentTrade().isNew();
    }

    private boolean shouldSell(int currentBarIndex)
    {
        return (status.equals(WAITING_FOR_EXIT) || status.equals(STOPPING)) &&
            strategy.shouldExit(currentBarIndex) &&
            tradingRecord.getCurrentTrade().isOpened();
    }

    private void buy(int currentBarIndex, TradingOperationContext tradingOperationContext)
    {
        try
        {
            var tradingOperationResult = exchangeGateway.buy(tradingOperationContext);
            var price = series.numOf(tradingOperationResult.getPrice());
            var amount = series.numOf(tradingOperationResult.getAmount());
            tradingRecord.enter(currentBarIndex, price, amount);
            changeStatus();
        }
        catch (Exception exception)
        {
            logTradingOperationFailure(BUY, exception);
        }
    }

    private void sell(int currentBarIndex, TradingOperationContext tradingOperationContext)
    {
        try
        {
            var tradingOperationResult = exchangeGateway.sell(tradingOperationContext);
            var price = series.numOf(tradingOperationResult.getPrice());
            var amount = series.numOf(tradingOperationResult.getAmount());
            tradingRecord.exit(currentBarIndex, price, amount);
            changeStatus();
        }
        catch (Exception exception)
        {
            logTradingOperationFailure(SELL, exception);
        }
    }

    private void changeStatus()
    {
        if (status.equals(WAITING_FOR_ENTRY))
        {
            status = WAITING_FOR_EXIT;
        }
        else if (status.equals(WAITING_FOR_EXIT))
        {
            status = WAITING_FOR_ENTRY;
        }
        else if (status.equals(STOPPING))
        {
            status = STOPPED;
            logExecutionStoppage();
        }
    }

    private void recordLastTradeToHistory()
    {
        var lastTrade = tradingRecord.getLastTrade();
        var tradeContext = TradeContext.builder()
            .exchangeGateway(exchangeGateway)
            .strategyExecutionId(id)
            .strategyExecutionContext(strategyExecutionContext)
            .strategyFactory(strategyFactory)
            .build();
        var tradeHistoryItem = tradeHistoryItemBuilder.build(lastTrade, tradeContext);
        tradeHistoryItemRepository.save(tradeHistoryItem);
    }

    private void logExecutionStopping()
    {
        log.info(STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE + " is stopping...",
            strategyFactory.getStrategyName(), exchangeGateway.getName(), strategyExecutionContext);
    }

    private void logExecutionStoppage()
    {
        log.info(STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE + " has been stopped successfully.",
            strategyFactory.getStrategyName(), exchangeGateway.getName(), strategyExecutionContext);
    }

    private void logTradingOperationFailure(Order.OrderType operationType, Exception exception)
    {
        log.error("{} operation failed for " + STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE,
            operationType, strategyFactory.getStrategyName(), exchangeGateway.getName(), strategyExecutionContext);
    }

    private TradingOperationContext buildTradingOperationContext(Bar bar)
    {
        return TradingOperationContext.builder()
            .symbol(strategyExecutionContext.getSymbol())
            .amount(strategyExecutionContext.getAmount())
            .price(bar.getClosePrice().doubleValue())
            .build();
    }

    @Value
    @Builder
    public static class State
    {
        String id;
        long startTime;
        StrategyExecutionStatus status;
        String strategyName;
        String symbol;
        double amount;
        Interval interval;
    }
}
