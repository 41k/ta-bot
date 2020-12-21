package root.application.domain.trading;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.*;
import org.ta4j.core.cost.LinearTransactionCostModel;
import org.ta4j.core.cost.ZeroCostModel;
import root.application.domain.history.TradeContext;
import root.application.domain.history.TradeHistoryItemBuilder;
import root.application.domain.history.TradeHistoryItemRepository;
import root.application.domain.strategy.Strategy;

import java.util.UUID;

import static org.ta4j.core.Order.OrderType.BUY;
import static org.ta4j.core.Order.OrderType.SELL;
import static root.application.domain.trading.StrategyExecutionStatus.*;

@Slf4j
public class StrategyExecution
{
    private static final int BAR_SERIES_SIZE_THRESHOLD = 1000;
    private static final String STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE =
        "Execution of strategy [{}] for exchange gateway [{}] with {}";

    @Getter
    private final String id;
    private final long startTime;
    private final BarSeries series;
    private final Strategy strategy;
    private final ExchangeGateway exchangeGateway;
    private final TradingRecord tradingRecord;
    private final TradeHistoryItemBuilder tradeHistoryItemBuilder;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;
    private final StrategyExecutionContext strategyExecutionContext;
    private volatile StrategyExecutionStatus status;

    public StrategyExecution(StrategyExecutionContext strategyExecutionContext,
                             TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        this.id = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
        this.series = initBarSeries();
        this.strategy = strategyExecutionContext.getStrategyFactory().create(series);
        this.exchangeGateway = strategyExecutionContext.getExchangeGateway();
        this.tradingRecord = initTradingRecord(strategyExecutionContext);
        this.tradeHistoryItemBuilder = new TradeHistoryItemBuilder();
        this.tradeHistoryItemRepository = tradeHistoryItemRepository;
        this.strategyExecutionContext = strategyExecutionContext;
        run();
    }

    public synchronized void stop()
    {
        if (status.equals(WAITING_FOR_ENTRY))
        {
            stopExecution();
        }
        else if (status.equals(WAITING_FOR_EXIT))
        {
            this.status = STOPPING;
            log.info(STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE + " is stopping...",
                strategy.getName(), exchangeGateway.getName(), strategyExecutionContext);
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
            .strategyName(strategy.getName())
            .symbol(strategyExecutionContext.getSymbol())
            .amount(strategyExecutionContext.getAmount())
            .interval(strategyExecutionContext.getInterval())
            .build();
    }

    private void run()
    {
        subscribeToBarStream();
        this.status = WAITING_FOR_ENTRY;
        log.info(STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE + " has been run successfully.",
            strategy.getName(), exchangeGateway.getName(), strategyExecutionContext);
    }

    private void subscribeToBarStream()
    {
        var subscriptionContext = buildBarStreamSubscriptionContext();
        exchangeGateway.subscribeToBarStream(subscriptionContext);
    }

    private void unsubscribeFromBarStream()
    {
        var subscriptionContext = buildBarStreamSubscriptionContext();
        exchangeGateway.unsubscribeFromBarStream(subscriptionContext);
    }

    private BarStreamSubscriptionContext buildBarStreamSubscriptionContext()
    {
        return BarStreamSubscriptionContext.builder()
            .symbol(strategyExecutionContext.getSymbol())
            .interval(strategyExecutionContext.getInterval())
            .barStreamSubscriberId(id)
            .barStreamSubscriber(this::acceptBar)
            .exchangeGatewayAccountConfiguration(strategyExecutionContext.getExchangeGatewayAccountConfiguration())
            .build();
    }

    private synchronized void acceptBar(Bar bar)
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
            updateState();
            logTradingOperationResult(BUY, tradingOperationResult);
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
            updateState();
            recordLastTradeToHistory();
            logTradingOperationResult(SELL, tradingOperationResult);
        }
        catch (Exception exception)
        {
            logTradingOperationFailure(SELL, exception);
        }
    }

    private void updateState()
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
            stopExecution();
        }
    }

    private void stopExecution()
    {
        unsubscribeFromBarStream();
        status = STOPPED;
        log.info(STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE + " has been stopped successfully.",
            strategy.getName(), exchangeGateway.getName(), strategyExecutionContext);
    }

    private void recordLastTradeToHistory()
    {
        try
        {
            var lastTrade = tradingRecord.getLastTrade();
            var tradeContext = TradeContext.builder()
                .strategyExecutionId(id)
                .strategyExecutionContext(strategyExecutionContext)
                .series(series)
                .strategy(strategy)
                .build();
            var tradeHistoryItem = tradeHistoryItemBuilder.build(lastTrade, tradeContext);
            tradeHistoryItemRepository.save(tradeHistoryItem);
        }
        catch (Exception e)
        {
            log.error("Error has occurred during recording of trade to history.", e);
        }
    }

    private void logTradingOperationResult(Order.OrderType operationType, TradingOperationResult tradingOperationResult)
    {
        log.info("{} operation for " + STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE + " has been done successfully with {}.",
            operationType, strategy.getName(), exchangeGateway.getName(), strategyExecutionContext, tradingOperationResult);
    }

    private void logTradingOperationFailure(Order.OrderType operationType, Exception exception)
    {
        log.error("{} operation failed for " + STRATEGY_EXECUTION_DESCRIPTION_FOR_LOG_MESSAGE,
            operationType, strategy.getName(), exchangeGateway.getName(), strategyExecutionContext, exception);
    }

    private TradingOperationContext buildTradingOperationContext(Bar bar)
    {
        return TradingOperationContext.builder()
            .exchangeGatewayAccountConfiguration(strategyExecutionContext.getExchangeGatewayAccountConfiguration())
            .symbol(strategyExecutionContext.getSymbol())
            .amount(strategyExecutionContext.getAmount())
            .price(bar.getClosePrice().doubleValue())
            .build();
    }

    private BarSeries initBarSeries()
    {
        var series = new BaseBarSeries();
        series.setMaximumBarCount(BAR_SERIES_SIZE_THRESHOLD);
        return series;
    }

    private TradingRecord initTradingRecord(StrategyExecutionContext strategyExecutionContext)
    {
        var transactionFee = strategyExecutionContext.getExchangeGateway().getTransactionFee();
        return new BaseTradingRecord(BUY, new LinearTransactionCostModel(transactionFee), new ZeroCostModel());
    }

    @Value
    @Builder
    public static class State
    {
        @NonNull
        String id;
        @NonNull
        Long startTime;
        @NonNull
        StrategyExecutionStatus status;
        @NonNull
        String strategyName;
        @NonNull
        Symbol symbol;
        @NonNull
        Double amount;
        @NonNull
        Interval interval;
    }
}
