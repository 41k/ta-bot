package root.application.domain.indicator.trend;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class DownTrendIndicator extends AbstractTrendIndicator
{
    public DownTrendIndicator(Indicator<Num> indicator, int trendLineLength, double slopeMinThreshold)
    {
        super(indicator, trendLineLength, slopeMinThreshold);
    }

    @Override
    protected boolean trendDirectionIsSatisfied(Num slope)
    {
        return slope.isNegative();
    }
}
