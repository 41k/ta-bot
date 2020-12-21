package root.application.domain.history;

import org.ta4j.core.Bar;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

public interface BarRepository
{
    void save(Bar bar, String exchangeGateway, Symbol symbol, Interval interval);
}
