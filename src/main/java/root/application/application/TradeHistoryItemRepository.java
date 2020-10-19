package root.application.application;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import root.application.domain.report.TradeHistoryItem;

public interface TradeHistoryItemRepository
{
    Mono<TradeHistoryItem> save(TradeHistoryItem tradeHistoryItem);

    Flux<TradeHistoryItem> findAll();
}
