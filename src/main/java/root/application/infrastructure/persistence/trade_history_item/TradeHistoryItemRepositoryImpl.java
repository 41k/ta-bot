package root.application.infrastructure.persistence.trade_history_item;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import root.application.application.TradeHistoryItemRepository;
import root.application.domain.report.TradeHistoryItem;

@RequiredArgsConstructor
public class TradeHistoryItemRepositoryImpl implements TradeHistoryItemRepository
{
    private final TradeHistoryItemMapper mapper;
    private final TradeHistoryItemDbEntryR2dbcRepository r2dbcRepository;


    @Override
    public Mono<TradeHistoryItem> save(TradeHistoryItem tradeHistoryItem)
    {
        return Mono.just(tradeHistoryItem)
            .map(mapper::toDbEntry)
            .flatMap(r2dbcRepository::save)
            .map(mapper::toDomainObject);
    }

    @Override
    public Flux<TradeHistoryItem> findAll()
    {
        return r2dbcRepository.findAll().map(mapper::toDomainObject);
    }
}
