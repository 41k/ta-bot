package root.application.domain.indicator.rsi;

import org.ta4j.core.num.Num;
import root.application.domain.indicator.AdditionalChartNumIndicator;

import static java.lang.String.format;

public class RSIIndicator extends org.ta4j.core.indicators.RSIIndicator implements AdditionalChartNumIndicator
{
    private static final String RSI_INDICATOR_NAME_FORMAT = "RSI(%d)";

    private final String name;

    public RSIIndicator(org.ta4j.core.Indicator<Num> indicator, int length)
    {
        super(indicator, length);
        this.name = format(RSI_INDICATOR_NAME_FORMAT, length);
    }

    public String getName()
    {
        return name;
    }
}
