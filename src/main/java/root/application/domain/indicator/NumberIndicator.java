package root.application.domain.indicator;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;
import root.application.domain.ChartType;

@Value
@Builder
public class NumberIndicator implements Indicator<Num>
{
    @NonNull
    String name;
    @NonNull
    ChartType chartType;
    @NonNull
    Indicator<Num> indicator;

    @Override
    public Num getValue(int index)
    {
        return indicator.getValue(index);
    }

    @Override
    public BarSeries getBarSeries() {
        return indicator.getBarSeries();
    }

    @Override
    public Num numOf(Number number) {
        return indicator.numOf(number);
    }
}
