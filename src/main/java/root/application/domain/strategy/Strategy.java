package root.application.domain.strategy;

import lombok.Getter;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.num.Num;
import root.application.domain.indicator.Indicator;
import root.application.domain.level.StopLossLevelProvider;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Strategy extends BaseStrategy
{
    private static final Integer DEFAULT_UNSTABLE_PERIOD_LENGTH = 0;

    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    private final List<Indicator<Num>> numIndicators;
    private final StopLossLevelProvider stopLossLevelProvider;
    private final Integer unstablePeriodLength;

    public Strategy(String id,
                    String name,
                    List<Indicator<Num>> numIndicators,
                    Rule entryRule,
                    Rule exitRule)
    {
        this(id, name, numIndicators, entryRule, exitRule, null, null);
    }

    public Strategy(String id,
                    String name,
                    List<Indicator<Num>> numIndicators,
                    Rule entryRule,
                    Rule exitRule,
                    StopLossLevelProvider stopLossLevelProvider,
                    Integer unstablePeriodLength)
    {
        super(name, entryRule, exitRule, ofNullable(unstablePeriodLength).orElse(DEFAULT_UNSTABLE_PERIOD_LENGTH));
        this.id = id;
        this.name = name;
        this.numIndicators = numIndicators;
        this.stopLossLevelProvider = stopLossLevelProvider;
        this.unstablePeriodLength = unstablePeriodLength;
    }

    public Optional<StopLossLevelProvider> getStopLossLevelProvider()
    {
        return ofNullable(stopLossLevelProvider);
    }

    public Optional<Integer> getUnstablePeriodLength()
    {
        return ofNullable(unstablePeriodLength);
    }
}
