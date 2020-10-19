package root.application.domain.strategyexecution;

import lombok.Getter;
import org.ta4j.core.*;
import org.ta4j.core.num.Num;
import root.application.domain.ExchangeGateway;
import root.application.domain.strategy.StrategyFactory;

import java.util.Optional;

import static root.application.domain.strategyexecution.StrategyExecutionStatus.*;

public class StrategyExecution
{
    private final Strategy strategy;
    private final BarSeries series;
    private final ExchangeGateway exchangeGateway;
    private final Num amount;
    private final TradingRecord tradingRecord;
    @Getter
    private volatile StrategyExecutionStatus status;

    public StrategyExecution(StrategyFactory strategyFactory, ExchangeGateway exchangeGateway, Num amount)
    {
        this.strategy = strategyFactory.create();
        this.series = strategyFactory.getBarSeries();
        this.exchangeGateway = exchangeGateway;
        this.amount = amount;
        this.tradingRecord = new BaseTradingRecord();
        this.status = WAITING_FOR_ENTRY;
    }

    public synchronized Optional<Trade> processBar(Bar bar)
    {
        series.addBar(bar);
        int currentBarIndex = series.getEndIndex();
        Num price = bar.getClosePrice();
        if (shouldBuy(currentBarIndex))
        {
            buy(currentBarIndex, price, amount);
            return Optional.empty();
        }
        if (shouldSell(currentBarIndex))
        {
            sell(currentBarIndex, price, amount);
            return Optional.of(tradingRecord.getLastTrade());
        }
        return Optional.empty();
    }

    public synchronized void stop()
    {
        if (status.equals(WAITING_FOR_ENTRY))
        {
            this.status = STOPPED;
        }
        else if (status.equals(WAITING_FOR_EXIT))
        {
            this.status = STOPPING;
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

    private void buy(int currentBarIndex, Num price, Num amount)
    {
        exchangeGateway.buy(amount.doubleValue());
        tradingRecord.enter(currentBarIndex, price, amount);
        changeStatus();
    }

    private void sell(int currentBarIndex, Num price, Num amount)
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
        }
    }
}
