package root.application.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;
import root.application.domain.report.TradeHistoryItem;
import root.application.infrastructure.persistence.bar.BarDbEntry;
import root.application.infrastructure.persistence.bar.BarDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemMapper;

@RequiredArgsConstructor
public class HistoryDataProvider
{
    private final TradeHistoryItemMapper mapper;
    private final TradeHistoryItemDbEntryR2dbcRepository tradeR2dbcRepository;
    private final BarDbEntryR2dbcRepository barR2dbcRepository;

    public Flux<TradeHistoryItem> findAllTrades()
    {
        return tradeR2dbcRepository.findAll().map(mapper::toDomainObject);
    }

    public Flux<TradeHistoryItem> findAllTradesInRange(Long fromTimestamp, Long toTimestamp)
    {
        return tradeR2dbcRepository.findAllInRange(fromTimestamp, toTimestamp).map(mapper::toDomainObject);
    }

    public Flux<Bar> findAllBars()
    {
        return barR2dbcRepository.findAll().map(BarDbEntry::toDomainObject);
    }
}
