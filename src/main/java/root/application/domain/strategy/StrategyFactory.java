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

    String getStrategyName();

    BarSeries getBarSeries();

    List<Indicator<Num>> getNumIndicators();

    Optional<Integer> getUnstablePeriodLength();

    Optional<StopLossLevelProvider> getStopLossLevelProvider();
}
