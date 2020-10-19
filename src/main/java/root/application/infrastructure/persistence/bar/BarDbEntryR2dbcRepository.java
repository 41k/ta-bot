package root.application.infrastructure.persistence.bar;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemDbEntry;

public interface BarDbEntryR2dbcRepository extends R2dbcRepository<BarDbEntry, Long>
{
}
