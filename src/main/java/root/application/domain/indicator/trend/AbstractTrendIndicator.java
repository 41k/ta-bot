package root.application.domain.indicator.trend;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

import static java.lang.Boolean.FALSE;

public abstract class AbstractTrendIndicator extends CachedIndicator<Boolean>
{
    private final Indicator<Num> indicator;
    private final int trendLineLength;
    private final Num slopeMinThreshold;

    public AbstractTrendIndicator(Indicator<Num> indicator, int trendLineLength, double slopeMinThreshold)
    {
        super(indicator);
        this.indicator = indicator;
        this.trendLineLength = trendLineLength;
        this.slopeMinThreshold = numOf(slopeMinThreshold);
    }

    @Override
    protected Boolean calculate(int index)
    {
        if (trendLineLengthIsNotReached(index))
        {
            return FALSE;
        }
        Num slope = calculateSlope(index);
        return trendDirectionIsSatisfied(slope) &&
                slopeMinThresholdIsSatisfied(slope);
    }

    private boolean trendLineLengthIsNotReached(int index)
    {
        return (index - trendLineLength) < getBarSeries().getBeginIndex();
    }

    private Num calculateSlope(int index)
    {
        Num y2 = indicator.getValue(index);
        Num y1 = indicator.getValue(index - trendLineLength);
        Num rise = y2.minus(y1);
        Num run = numOf(trendLineLength);
        return rise.dividedBy(run);
    }

    protected abstract boolean trendDirectionIsSatisfied(Num slope);

    private boolean slopeMinThresholdIsSatisfied(Num slope)
    {
        return slope.abs().isGreaterThan(slopeMinThreshold);
    }
}
