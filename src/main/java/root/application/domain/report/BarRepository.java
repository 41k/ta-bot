package root.application.domain.report;

import org.ta4j.core.Bar;

public interface BarRepository
{
    Bar save(Bar bar, String exchangeGatewayId);
}
