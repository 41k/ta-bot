package root.application.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;
import root.application.application.QueryProcessor;
import root.application.domain.report.TradeHistoryItem;
import root.application.infrastructure.persistence.bar.BarDbEntry;
import root.application.infrastructure.persistence.bar.BarDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemMapper;

@RequiredArgsConstructor
public class QueryProcessorImpl implements QueryProcessor
{
    private final TradeHistoryItemMapper mapper;
    private final TradeHistoryItemDbEntryR2dbcRepository tradeR2dbcRepository;
    private final BarDbEntryR2dbcRepository barR2dbcRepository;

    @Override
    public Flux<TradeHistoryItem> findAllTrades()
    {
        return tradeR2dbcRepository.findAll().map(mapper::toDomainObject);
    }

    @Override
    public Flux<Bar> findAllBars()
    {
        return barR2dbcRepository.findAll().map(BarDbEntry::toDomainObject);
    }
}
