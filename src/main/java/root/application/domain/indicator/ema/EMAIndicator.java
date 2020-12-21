package root.application.domain.indicator.ema;

import org.ta4j.core.num.Num;
import root.application.domain.indicator.MainChartNumIndicator;

import static java.lang.String.format;

public class EMAIndicator extends org.ta4j.core.indicators.EMAIndicator implements MainChartNumIndicator
{
    private static final String EMA_INDICATOR_NAME_FORMAT = "EMA(%d)";

    private final String name;

    public EMAIndicator(org.ta4j.core.Indicator<Num> indicator, int length)
    {
        super(indicator, length);
        this.name = format(EMA_INDICATOR_NAME_FORMAT, length);
    }

    public String getName()
    {
        return name;
    }
}
