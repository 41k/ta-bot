package root.application.domain.indicator.wri;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.num.Num;
import root.application.domain.indicator.AdditionalChartNumIndicator;

import static java.lang.String.format;

public class WRLevelIndicator extends ConstantIndicator<Num> implements AdditionalChartNumIndicator
{
    private static final String LEVEL_NAME_FORMAT = "WR-level(%.0f)";

    private final String name;

    public WRLevelIndicator(BarSeries series, Num level)
    {
        super(series, level);
        this.name = format(LEVEL_NAME_FORMAT, level.doubleValue());
    }

    public String getName()
    {
        return name;
    }
}
