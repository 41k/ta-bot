package root.application.infrastructure.persistence.trade_history_item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.query.Criteria;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import root.application.application.HistoryFilter;

import static java.util.Objects.nonNull;

public interface TradeHistoryItemDbEntryR2dbcRepository extends R2dbcRepository<TradeHistoryItemDbEntry, Long>, TradeHistoryItemDbEntryR2dbcRepositoryInternal
{
}

interface TradeHistoryItemDbEntryR2dbcRepositoryInternal
{
    Flux<TradeHistoryItemDbEntry> findAllByFilter(HistoryFilter filter, Pageable pageable);
}

@RequiredArgsConstructor
class TradeHistoryItemDbEntryR2dbcRepositoryInternalImpl implements TradeHistoryItemDbEntryR2dbcRepositoryInternal
{
    private final DatabaseClient databaseClient;

    public Flux<TradeHistoryItemDbEntry> findAllByFilter(HistoryFilter filter, Pageable pageable)
    {
        var criteria = Criteria
            .where("entry_timestamp").greaterThanOrEquals(filter.getFromTimestamp())
            .and("exit_timestamp").lessThanOrEquals(filter.getToTimestamp());
        var exchangeGatewayId = filter.getExchangeGatewayId();
        if (nonNull(exchangeGatewayId))
        {
            criteria = criteria.and("exchange_gateway_id").is(exchangeGatewayId);
        }
        var strategyId = filter.getStrategyId();
        if (nonNull(strategyId))
        {
            criteria = criteria.and("strategy_id").is(strategyId);
        }
        return databaseClient.select()
            .from(TradeHistoryItemDbEntry.class)
            .matching(criteria)
            .page(pageable)
            .as(TradeHistoryItemDbEntry.class)
            .all();
    }
}
