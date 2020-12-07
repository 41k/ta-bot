package root.application.application.repository;

import org.springframework.data.domain.Pageable;
import root.application.application.model.HistoryFilter;
import root.application.domain.history.TradeHistoryItem;

import java.util.List;

public interface ApplicationLevelTradeHistoryItemRepository {

    List<TradeHistoryItem> findAllTrades();

    List<TradeHistoryItem> findTrades(HistoryFilter filter, Pageable pageable);

    List<TradeHistoryItem> findTradesByStrategyExecutionId(String strategyExecutionId);
}
