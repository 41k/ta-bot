package root.application.infrastructure.persistence.trade_history_item;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import root.application.domain.report.TradeHistoryItem;
import root.application.domain.report.TradeHistoryItemRepository;

@RequiredArgsConstructor
public class TradeHistoryItemRepositoryImpl implements TradeHistoryItemRepository
{
    private final TradeHistoryItemMapper mapper;
    private final TradeHistoryItemDbEntryR2dbcRepository r2dbcRepository;

    @Override
    public TradeHistoryItem save(TradeHistoryItem tradeHistoryItem)
    {
        Mono.just(tradeHistoryItem)
            .map(mapper::toDbEntry)
            .flatMap(r2dbcRepository::save)
            .subscribe();
        return tradeHistoryItem;
    }
}
