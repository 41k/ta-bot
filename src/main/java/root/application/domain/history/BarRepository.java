package root.application.domain.history;

import org.ta4j.core.Bar;
import root.application.domain.trading.Interval;

public interface BarRepository
{
    Bar save(Bar bar, String exchangeGatewayId, Interval interval);
}
