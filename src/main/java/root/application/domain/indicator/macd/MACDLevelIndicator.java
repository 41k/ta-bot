package root.application.domain.indicator.macd;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.num.Num;
import root.application.domain.indicator.AdditionalChartNumIndicator;

import static java.lang.String.format;

public class MACDLevelIndicator extends ConstantIndicator<Num> implements AdditionalChartNumIndicator
{
    private static final String MACD_LEVEL_INDICATOR_NAME_FORMAT = "MACD-level(%.0f)";

    private final String name;

    public MACDLevelIndicator(BarSeries series, Num level)
    {
        super(series, level);
        this.name = format(MACD_LEVEL_INDICATOR_NAME_FORMAT, level.doubleValue());
    }

    public String getName()
    {
        return name;
    }
}
