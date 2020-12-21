package root.application.infrastructure.persistence.trade_history_item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.query.Criteria;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import root.application.application.model.HistoryFilter;

import static java.util.Objects.nonNull;

public interface TradeHistoryItemDbEntryR2dbcRepository extends R2dbcRepository<TradeHistoryItemDbEntry, Long>, TradeHistoryItemDbEntryR2dbcRepositoryInternal
{
    @Query("SELECT * FROM trade_history_item WHERE strategy_execution_id = :strategyExecutionId")
    Flux<TradeHistoryItemDbEntry> findAllByStrategyExecutionId(String strategyExecutionId);
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
        var criteria = Criteria.where("user_id").is(filter.getUserId());
        var fromTimestamp = filter.getFromTimestamp();
        if (nonNull(fromTimestamp))
        {
            criteria = criteria.and("entry_timestamp").greaterThanOrEquals(fromTimestamp);
        }
        var toTimestamp = filter.getToTimestamp();
        if (nonNull(toTimestamp))
        {
            criteria = criteria.and("exit_timestamp").lessThanOrEquals(toTimestamp);
        }
        var exchangeGateway = filter.getExchangeGateway();
        if (nonNull(exchangeGateway))
        {
            criteria = criteria.and("exchange_gateway").is(exchangeGateway);
        }
        var strategyExecutionId = filter.getStrategyExecutionId();
        if (nonNull(strategyExecutionId))
        {
            criteria = criteria.and("strategy_execution_id").is(strategyExecutionId);
        }
        return databaseClient.select()
            .from(TradeHistoryItemDbEntry.class)
            .matching(criteria)
            .page(pageable)
            .as(TradeHistoryItemDbEntry.class)
            .all();
    }
}
