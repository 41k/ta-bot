package root.application.domain.indicator.bollinger;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;
import root.application.domain.indicator.MainChartNumIndicator;

public class BBUpperIndicator extends org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator implements MainChartNumIndicator
{
    private static final String INDICATOR_NAME = "BBU";

    public BBUpperIndicator(BBMiddleIndicator bbm, Indicator<Num> deviation, Num multiplier)
    {
        super(bbm, deviation, multiplier);
    }

    @Override
    public String getName()
    {
        return INDICATOR_NAME;
    }
}
