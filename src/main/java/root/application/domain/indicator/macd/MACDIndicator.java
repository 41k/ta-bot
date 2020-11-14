package root.application.domain.indicator.macd;

import org.ta4j.core.num.Num;
import root.application.domain.indicator.AdditionalChartNumIndicator;

import static java.lang.String.format;

public class MACDIndicator extends org.ta4j.core.indicators.MACDIndicator implements AdditionalChartNumIndicator
{
    private static final String MACD_INDICATOR_NAME_FORMAT = "MACD(%d, %d)";

    private final String name;

    public MACDIndicator(org.ta4j.core.Indicator<Num> indicator, int shortEmaLength, int longEmaLength)
    {
        super(indicator, shortEmaLength, longEmaLength);
        this.name = format(MACD_INDICATOR_NAME_FORMAT, shortEmaLength, longEmaLength);
    }

    public String getName()
    {
        return name;
    }
}
