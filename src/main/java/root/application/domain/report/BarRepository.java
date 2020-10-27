package root.application.domain.report;

import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import root.application.domain.report.TradeHistoryItem;

public interface BarRepository
{
    Bar save(Bar bar, String exchangeId);
}
