package root.application.application;

import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;
import root.application.domain.report.TradeHistoryItem;

public interface QueryProcessor
{
    Flux<TradeHistoryItem> findAllTrades();

    Flux<Bar> findAllBars();
}
