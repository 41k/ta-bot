package root.application.domain.indicator.bar;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.candles.LowerShadowIndicator;
import org.ta4j.core.indicators.candles.RealBodyIndicator;
import org.ta4j.core.indicators.candles.UpperShadowIndicator;
import org.ta4j.core.num.Num;

public class BarIndicator extends CachedIndicator<Boolean>
{
    private final BarType barType;
    private final Num bodyMinSize;
    private final Num bodyMaxSize;
    private final Num lowerShadowMinSize;
    private final Num lowerShadowMaxSize;
    private final Num upperShadowMinSize;
    private final Num upperShadowMaxSize;

    private final RealBodyIndicator realBodyIndicator;
    private final LowerShadowIndicator lowerShadowIndicator;
    private final UpperShadowIndicator upperShadowIndicator;

    public BarIndicator(BarType barType,
                        double bodyMinSize, double bodyMaxSize,
                        double lowerShadowMinSize, double lowerShadowMaxSize,
                        double upperShadowMinSize, double upperShadowMaxSize,
                        BarSeries series)
    {
        super(series);
        this.barType = barType;
        this.bodyMinSize = series.numOf(bodyMinSize);
        this.bodyMaxSize = series.numOf(bodyMaxSize);
        this.lowerShadowMinSize = series.numOf(lowerShadowMinSize);
        this.lowerShadowMaxSize = series.numOf(lowerShadowMaxSize);
        this.upperShadowMinSize = series.numOf(upperShadowMinSize);
        this.upperShadowMaxSize = series.numOf(upperShadowMaxSize);
        this.realBodyIndicator = new RealBodyIndicator(series);
        this.upperShadowIndicator = new UpperShadowIndicator(series);
        this.lowerShadowIndicator = new LowerShadowIndicator(series);
    }

    @Override
    protected Boolean calculate(int index)
    {
        Bar bar = getBarSeries().getBar(index);
        Num bodySize = realBodyIndicator.getValue(index).abs();
        Num lowerShadowSize = lowerShadowIndicator.getValue(index);
        Num upperShadowSize = upperShadowIndicator.getValue(index);
        return barType.conforms(bar) &&
            bodySize.isGreaterThanOrEqual(bodyMinSize) && bodySize.isLessThanOrEqual(bodyMaxSize) &&
            lowerShadowSize.isGreaterThanOrEqual(lowerShadowMinSize) && lowerShadowSize.isLessThanOrEqual(lowerShadowMaxSize) &&
            upperShadowSize.isGreaterThanOrEqual(upperShadowMinSize) && upperShadowSize.isLessThanOrEqual(upperShadowMaxSize);
    }
}
