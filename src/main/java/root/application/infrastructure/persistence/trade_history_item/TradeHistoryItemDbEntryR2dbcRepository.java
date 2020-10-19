package root.application.infrastructure.persistence.trade_history_item;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface TradeHistoryItemDbEntryR2dbcRepository extends R2dbcRepository<TradeHistoryItemDbEntry, Long>
{
}
