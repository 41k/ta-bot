package root.application.domain.indicator.bollinger;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;
import root.application.domain.indicator.MainChartNumIndicator;

public class BBLowerIndicator extends org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator implements MainChartNumIndicator
{
    private static final String INDICATOR_NAME = "BBL";

    public BBLowerIndicator(BBMiddleIndicator bbm, Indicator<Num> deviation, Num multiplier)
    {
        super(bbm, deviation, multiplier);
    }

    @Override
    public String getName()
    {
        return INDICATOR_NAME;
    }
}
