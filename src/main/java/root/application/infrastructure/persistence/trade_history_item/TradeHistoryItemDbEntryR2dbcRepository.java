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
            "exchange_gateway_id = :exchangeGatewayId")
    Flux<TradeHistoryItemDbEntry> findAllInRangeByExchangeGatewayId(
        Long fromTimestamp, Long toTimestamp, String exchangeGatewayId);

    @Query("SELECT * FROM trade_history_item WHERE " +
            "entry_timestamp >= :fromTimestamp AND " +
            "exit_timestamp <= :toTimestamp AND " +
            "exchange_gateway_id = :exchangeGatewayId AND " +
            "strategy_id = :strategyId")
    Flux<TradeHistoryItemDbEntry> findAllInRangeByExchangeGatewayIdAndStrategyId(
        Long fromTimestamp, Long toTimestamp, String exchangeGatewayId, String strategyId);
}
