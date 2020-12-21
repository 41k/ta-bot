package root.application.application.repository;

import org.springframework.data.domain.Pageable;
import root.application.application.model.HistoryFilter;
import root.application.domain.history.TradeHistoryItem;

import java.util.Collection;

public interface ApplicationLevelTradeHistoryItemRepository
{
    Collection<TradeHistoryItem> findTrades(HistoryFilter filter, Pageable pageable);

    Collection<TradeHistoryItem> findTradesByStrategyExecutionId(String strategyExecutionId);
}
