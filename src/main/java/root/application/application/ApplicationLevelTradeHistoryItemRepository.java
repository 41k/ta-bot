package root.application.application;

import root.application.domain.report.TradeHistoryItem;

import java.util.List;

public interface ApplicationLevelTradeHistoryItemRepository {

    List<TradeHistoryItem> findTrades();

    List<TradeHistoryItem> findTrades(Long fromTimestamp, Long toTimestamp);

    List<TradeHistoryItem> findTrades(Long fromTimestamp, Long toTimestamp, String exchangeId);

    List<TradeHistoryItem> findTrades(Long fromTimestamp, Long toTimestamp, String exchangeId, String strategyId);
}
