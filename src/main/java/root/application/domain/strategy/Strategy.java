package root.application.domain.strategy;

import lombok.Getter;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import root.application.domain.indicator.NumberIndicator;
import root.application.domain.level.MainChartLevelProvider;

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
    private final List<NumberIndicator> numberIndicators;
    private final List<MainChartLevelProvider> mainChartLevelProviders;
    private final Integer unstablePeriodLength;

    public Strategy(String id,
                    String name,
                    List<NumberIndicator> numberIndicators,
                    Rule entryRule,
                    Rule exitRule)
    {
        this(id, name, numberIndicators, entryRule, exitRule, List.of(), null);
    }

    public Strategy(String id,
                    String name,
                    List<NumberIndicator> numberIndicators,
                    Rule entryRule,
                    Rule exitRule,
                    List<MainChartLevelProvider> mainChartLevelProviders,
                    Integer unstablePeriodLength)
    {
        super(name, entryRule, exitRule, ofNullable(unstablePeriodLength).orElse(DEFAULT_UNSTABLE_PERIOD_LENGTH));
        this.id = id;
        this.name = name;
        this.numberIndicators = numberIndicators;
        this.mainChartLevelProviders = mainChartLevelProviders;
        this.unstablePeriodLength = unstablePeriodLength;
    }

    public List<MainChartLevelProvider> getMainChartLevelProviders()
    {
        return mainChartLevelProviders;
    }

    public Optional<Integer> getUnstablePeriodLength()
    {
        return ofNullable(unstablePeriodLength);
    }
}
