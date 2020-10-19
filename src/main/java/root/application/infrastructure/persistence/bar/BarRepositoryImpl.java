package root.application.infrastructure.persistence.bar;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import root.application.application.BarRepository;
import root.application.application.TradeHistoryItemRepository;
import root.application.domain.report.TradeHistoryItem;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemMapper;

@RequiredArgsConstructor
public class BarRepositoryImpl implements BarRepository
{
    private final BarDbEntryR2dbcRepository r2dbcRepository;


    @Override
    public Mono<Bar> save(Bar bar, String exchangeId)
    {
         return r2dbcRepository.save(BarDbEntry.fromDomainObject(bar, exchangeId)).map(BarDbEntry::toDomainObject);
    }

    @Override
    public Flux<Bar> findAll()
    {
        return r2dbcRepository.findAll().map(BarDbEntry::toDomainObject);
    }
}
