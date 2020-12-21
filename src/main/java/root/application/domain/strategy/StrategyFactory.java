package root.application.domain.strategy;

import org.ta4j.core.BarSeries;

public interface StrategyFactory
{
    Strategy create(BarSeries series);

    String getStrategyId();

    String getStrategyName();
}
