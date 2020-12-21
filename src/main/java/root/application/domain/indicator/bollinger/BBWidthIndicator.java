package root.application.domain.indicator.bollinger;

import root.application.domain.indicator.MainChartNumIndicator;

public class BBWidthIndicator extends org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator implements MainChartNumIndicator
{
    private static final String INDICATOR_NAME = "BBW";

    public BBWidthIndicator(BBUpperIndicator bbu, BBMiddleIndicator bbm, BBLowerIndicator bbl)
    {
        super(bbu, bbm, bbl);
    }

    @Override
    public String getName()
    {
        return INDICATOR_NAME;
    }
}
