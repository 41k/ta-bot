package root.application.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Strategy;

public abstract class AbstractStrategyFactory implements StrategyFactory
{
    private static final int BAR_SERIES_SIZE_DEFAULT_THRESHOLD = 1000;

    protected final String strategyId;
    protected final String exchangeId;
    protected final BarSeries series;

    public AbstractStrategyFactory(String strategyId, String exchangeId)
    {
        checkInput(strategyId, exchangeId);
        this.strategyId = strategyId;
        this.exchangeId = exchangeId;
        this.series = initBarSeries();
    }

    @Override
    public abstract Strategy create();

    @Override
    public String getStrategyId()
    {
        return strategyId;
    }

    @Override
    public String getExchangeId()
    {
        return exchangeId;
    }

    @Override
    public BarSeries getBarSeries()
    {
        return series;
    }

    private void checkInput(String strategyId, String exchangeId)
    {
        if (strategyId == null || strategyId.isBlank() ||
            exchangeId == null || exchangeId.isBlank())
        {
            throw new IllegalArgumentException();
        }
    }

    private BarSeries initBarSeries()
    {
        BarSeries series = new BaseBarSeries();
        series.setMaximumBarCount(BAR_SERIES_SIZE_DEFAULT_THRESHOLD);
        return series;
    }
}
