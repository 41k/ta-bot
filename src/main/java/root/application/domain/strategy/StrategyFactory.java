package root.application.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.num.Num;
import root.application.domain.indicator.Indicator;
import root.application.domain.level.StopLossLevelProvider;

import java.util.List;
import java.util.Optional;

public interface StrategyFactory
{
    Strategy create();

    String getStrategyId();

    String getExchangeId();

    BarSeries getBarSeries();

    default Optional<Integer> getUnstablePeriodLength()
    {
        return Optional.empty();
    }

    default List<Indicator<Num>> getNumIndicators()
    {
        return List.of();
    }

    default Optional<StopLossLevelProvider> getStopLossLevelProvider()
    {
        return Optional.empty();
    }
}
