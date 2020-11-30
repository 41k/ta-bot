package root.application.infrastructure.persistence.trade_history_item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import root.application.application.ApplicationLevelTradeHistoryItemRepository;
import root.application.application.HistoryFilter;
import root.application.domain.report.TradeHistoryItem;
import root.application.domain.report.TradeHistoryItemRepository;

import java.util.List;

@RequiredArgsConstructor
public class TradeHistoryItemRepositoryImpl implements TradeHistoryItemRepository, ApplicationLevelTradeHistoryItemRepository
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

    @Override
    public List<TradeHistoryItem> findAllTrades()
    {
        return r2dbcRepository.findAll()
            .map(mapper::toDomainObject)
            .collectList()
            .block();
    }

    @Override
    public List<TradeHistoryItem> findTrades(HistoryFilter filter, Pageable pageable)
    {
        return r2dbcRepository.findAllByFilter(filter, pageable)
            .map(mapper::toDomainObject)
            .collectList()
            .block();
    }
}
