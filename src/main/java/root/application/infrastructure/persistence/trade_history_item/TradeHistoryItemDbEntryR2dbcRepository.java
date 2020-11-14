package root.application.infrastructure.persistence.trade_history_item;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface TradeHistoryItemDbEntryR2dbcRepository extends R2dbcRepository<TradeHistoryItemDbEntry, Long>
{
    @Query("SELECT * FROM trade_history_item WHERE " +
            "entry_timestamp >= :fromTimestamp AND " +
            "exit_timestamp <= :toTimestamp")
    Flux<TradeHistoryItemDbEntry> findAllInRange(Long fromTimestamp, Long toTimestamp);

    @Query("SELECT * FROM trade_history_item WHERE " +
            "entry_timestamp >= :fromTimestamp AND " +
            "exit_timestamp <= :toTimestamp AND " +
            "exchange_id = :exchangeId")
    Flux<TradeHistoryItemDbEntry> findAllInRangeByExchangeId(Long fromTimestamp, Long toTimestamp, String exchangeId);

    @Query("SELECT * FROM trade_history_item WHERE " +
            "entry_timestamp >= :fromTimestamp AND " +
            "exit_timestamp <= :toTimestamp AND " +
            "exchange_id = :exchangeId AND strategy_id = :strategyId")
    Flux<TradeHistoryItemDbEntry> findAllInRangeByExchangeIdAndStrategyId(
        Long fromTimestamp, Long toTimestamp, String exchangeId, String strategyId);
}
