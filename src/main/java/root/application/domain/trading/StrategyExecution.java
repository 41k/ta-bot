package root.application.domain.trading;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.*;
import org.ta4j.core.num.Num;
import root.application.domain.ExchangeGateway;
import root.application.domain.report.TradeHistoryItem;
import root.application.domain.report.TradeHistoryItemBuilder;
import root.application.domain.strategy.StrategyFactory;

import java.util.Optional;

import static root.application.domain.trading.StrategyExecutionStatus.*;

@Slf4j
public class StrategyExecution
{
    private final StrategyFactory strategyFactory;
    private final Strategy strategy;
    private final String strategyId;
    private final BarSeries series;
    private final ExchangeGateway exchangeGateway;
    private final TradingRecord tradingRecord;
    private final TradeHistoryItemBuilder tradeHistoryItemBuilder;
    private final Num amount;
    @Getter
    private volatile StrategyExecutionStatus status;

    public StrategyExecution(StrategyFactory strategyFactory, ExchangeGateway exchangeGateway, double amount)
    {
        this.strategyFactory = strategyFactory;
        this.strategyId = strategyFactory.getStrategyId();
        this.strategy = strategyFactory.create();
        this.series = strategyFactory.getBarSeries();
        this.exchangeGateway = exchangeGateway;
        this.tradingRecord = new BaseTradingRecord();
        this.tradeHistoryItemBuilder = new TradeHistoryItemBuilder();
        this.amount = strategyFactory.getBarSeries().numOf(amount);
        this.status = WAITING_FOR_ENTRY;
    }

    public synchronized Optional<TradeHistoryItem> processBar(Bar bar)
    {
        series.addBar(bar);
        var currentBarIndex = series.getEndIndex();
        var price = bar.getClosePrice();
        if (shouldBuy(currentBarIndex))
        {
            buy(currentBarIndex, price);
            return Optional.empty();
        }
        if (shouldSell(currentBarIndex))
        {
            sell(currentBarIndex, price);
            return Optional.of(getLastTrade());
        }
        return Optional.empty();
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
            log.info("Execution for strategy [{}] is stopping...", strategyId);
        }
    }

    public boolean isActive()
    {
        return status.equals(WAITING_FOR_ENTRY) ||
            status.equals(WAITING_FOR_EXIT) ||
            status.equals(STOPPING);
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

    private void buy(int currentBarIndex, Num price)
    {
        exchangeGateway.buy(amount.doubleValue());
        tradingRecord.enter(currentBarIndex, price, amount);
        changeStatus();
    }

    private void sell(int currentBarIndex, Num price)
    {
        exchangeGateway.sell(amount.doubleValue());
        tradingRecord.exit(currentBarIndex, price, amount);
        changeStatus();
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

    private TradeHistoryItem getLastTrade()
    {
        var lastTrade = tradingRecord.getLastTrade();
        var exchangeId = exchangeGateway.getExchangeId();
        return tradeHistoryItemBuilder.build(lastTrade, strategyFactory, exchangeId);
    }

    private void logExecutionStoppage()
    {
        log.info("Execution for strategy [{}] has been stopped successfully.", strategyId);
    }
}
