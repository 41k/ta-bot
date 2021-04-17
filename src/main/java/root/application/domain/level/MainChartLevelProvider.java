package root.application.domain.level;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

import static java.lang.String.format;

@RequiredArgsConstructor
public class MainChartLevelProvider
{
    private static final String LEVEL_NAME_FORMAT = "%s %.2f";

    private final String levelNamePrefix;
    private final Function<Integer, Double> levelValueProvider;

    public MainChartLevel getLevel(int entryIndex)
    {
        var name = getLevelName(entryIndex);
        var value = getLevelValue(entryIndex);
        return new MainChartLevel(name, value);
    }

    private String getLevelName(int entryIndex)
    {
        var levelValue = getLevelValue(entryIndex);
        return format(LEVEL_NAME_FORMAT, levelNamePrefix, levelValue);
    }

    private double getLevelValue(int entryIndex)
    {
        return levelValueProvider.apply(entryIndex);
    }
}
