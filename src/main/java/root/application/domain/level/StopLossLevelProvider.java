package root.application.domain.level;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import static java.lang.String.format;

public class StopLossLevelProvider
{
    private static final String NAME_FORMAT = "SL %.2f (-%.0f)";

    private final BarSeries series;
    private final Num lossValue;

    public StopLossLevelProvider(BarSeries series, double lossValue)
    {
        this.series = series;
        this.lossValue = series.numOf(lossValue);
    }

    public Level getLevel(int index)
    {
        Bar bar = series.getBar(index);
        Num closePrice = bar.getClosePrice();
        double levelValue = closePrice.minus(lossValue).doubleValue();
        String levelName = format(NAME_FORMAT, levelValue, lossValue.doubleValue());
        return new Level(levelName, levelValue);
    }
}
