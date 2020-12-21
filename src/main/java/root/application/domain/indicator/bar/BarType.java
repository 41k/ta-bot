package root.application.domain.indicator.bar;

import org.ta4j.core.Bar;

import java.util.function.Predicate;

public enum BarType
{
    BULLISH(Bar::isBullish),
    BEARISH(Bar::isBearish);

    private final Predicate<Bar> barTypePredicate;

    BarType(Predicate<Bar> barTypePredicate)
    {
        this.barTypePredicate = barTypePredicate;
    }

    public boolean conforms(Bar bar)
    {
        return barTypePredicate.test(bar);
    }
}
