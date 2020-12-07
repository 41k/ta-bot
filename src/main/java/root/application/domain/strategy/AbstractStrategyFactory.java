package root.application.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.num.Num;
import root.application.domain.indicator.Indicator;
import root.application.domain.level.StopLossLevelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public abstract class AbstractStrategyFactory implements StrategyFactory
{
    private static final int BAR_SERIES_SIZE_DEFAULT_THRESHOLD = 1000;

    protected final String strategyId;
    protected final String strategyName;
    protected final BarSeries series;
    protected final List<Indicator<Num>> numIndicators;
    protected Integer unstablePeriodLength;
    protected StopLossLevelProvider stopLossLevelProvider;

    public AbstractStrategyFactory(String strategyId, String strategyName)
    {
        this.strategyId = strategyId;
        this.strategyName = strategyName;
        this.series = initBarSeries();
        this.numIndicators = new ArrayList<>();
    }

    @Override
    public abstract Strategy create();

    @Override
    public String getStrategyId()
    {
        return strategyId;
    }

    @Override
    public String getStrategyName()
    {
        return strategyName;
    }

    @Override
    public BarSeries getBarSeries()
    {
        return series;
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }

    @Override
    public Optional<Integer> getUnstablePeriodLength()
    {
        return ofNullable(unstablePeriodLength);
    }

    @Override
    public Optional<StopLossLevelProvider> getStopLossLevelProvider()
    {
        return ofNullable(stopLossLevelProvider);
    }

    private BarSeries initBarSeries()
    {
        BarSeries series = new BaseBarSeries();
        series.setMaximumBarCount(BAR_SERIES_SIZE_DEFAULT_THRESHOLD);
        return series;
    }
}
