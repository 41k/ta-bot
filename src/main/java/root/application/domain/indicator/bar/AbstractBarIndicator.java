package root.application.domain.indicator.bar;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;

public abstract class AbstractBarIndicator extends CachedIndicator<Boolean>
{
    public AbstractBarIndicator(BarSeries series)
    {
        super(series);
    }

    protected boolean isNotAcceptableIndex(int index, int nPreviousBars)
    {
        try
        {
            getBar(index);
            return (index - nPreviousBars) < getBarSeries().getBeginIndex();
        }
        catch (IndexOutOfBoundsException e)
        {
            return TRUE;
        }
    }

    protected Bar getBar(int index)
    {
        return getBarSeries().getBar(index);
    }

    protected Num calculateBarSize(Bar bar)
    {
        return bar.getClosePrice().minus(bar.getOpenPrice()).abs();
    }

    protected Stream<Bar> getSubSeries(int startIndex, int endIndex)
    {
        return IntStream.range(startIndex, endIndex + 1).mapToObj(this::getBar);
    }

    protected boolean doesSubSeriesHasAllowedBarTypes(Set<BarType> types, int startIndex, int endIndex)
    {
        return getSubSeries(startIndex, endIndex).allMatch(hasAllowedType(types));
    }

    protected Num calculateTotalSizeOfNPreviousBars(int currentBarIndex, int nPreviousBars)
    {
        return getNPreviousBars(currentBarIndex, nPreviousBars)
                .map(this::calculateBarSize)
                .reduce(PrecisionNum.valueOf(0), Num::plus);
    }

    protected boolean doesPreviousBarsHaveAllowedTypes(Set<BarType> types, int currentBarIndex, int nPreviousBars)
    {
        return getNPreviousBars(currentBarIndex, nPreviousBars).allMatch(hasAllowedType(types));
    }

    private Stream<Bar> getNPreviousBars(int currentBarIndex, int nPreviousBars)
    {
        int startIndex = currentBarIndex - nPreviousBars;
        int endIndex = currentBarIndex - 1;
        return getSubSeries(startIndex, endIndex);
    }

    private Predicate<Bar> hasAllowedType(Set<BarType> types)
    {
        return bar -> types.stream().anyMatch(barType -> barType.conforms(bar));
    }
}
