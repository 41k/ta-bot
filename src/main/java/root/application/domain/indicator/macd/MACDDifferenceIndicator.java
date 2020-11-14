package root.application.domain.indicator.macd;

import org.ta4j.core.indicators.helpers.DifferenceIndicator;
import root.application.domain.indicator.AdditionalChartNumIndicator;

public class MACDDifferenceIndicator extends DifferenceIndicator implements AdditionalChartNumIndicator
{
    private static final String MACD_DIFFERENCE_INDICATOR_NAME_FORMAT = "MACD-diff";

    public MACDDifferenceIndicator(MACDIndicator macdIndicator, MACDSignalLineIndicator macdSignalLineIndicator)
    {
        super(macdIndicator, macdSignalLineIndicator);
    }

    public String getName()
    {
        return MACD_DIFFERENCE_INDICATOR_NAME_FORMAT;
    }
}
