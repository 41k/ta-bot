package root.application.domain.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ta4j.core.BarSeries;

@RequiredArgsConstructor
public abstract class AbstractStrategyFactory implements StrategyFactory
{
    @Getter
    private final String strategyId;
    @Getter
    private final String strategyName;

    @Override
    public abstract Strategy create(BarSeries series);
}
