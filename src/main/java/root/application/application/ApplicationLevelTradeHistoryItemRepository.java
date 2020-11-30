package root.application.application;

import org.springframework.data.domain.Pageable;
import root.application.domain.report.TradeHistoryItem;

import java.util.List;

public interface ApplicationLevelTradeHistoryItemRepository {

    List<TradeHistoryItem> findAllTrades();

    List<TradeHistoryItem> findTrades(HistoryFilter filter, Pageable pageable);
}
