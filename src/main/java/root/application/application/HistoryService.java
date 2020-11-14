package root.application.application;

import lombok.RequiredArgsConstructor;
import root.application.domain.report.TradeHistoryItem;

import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class HistoryService
{
    private final ApplicationLevelTradeHistoryItemRepository tradeHistoryItemRepository;

    public List<TradeHistoryItem> searchForTrades(Long fromTimestamp, Long toTimestamp, String exchangeId, String strategyId)
    {
        if (nonNull(fromTimestamp) && nonNull(toTimestamp) && nonNull(exchangeId) && nonNull(strategyId))
        {
            return tradeHistoryItemRepository.findTrades(fromTimestamp, toTimestamp, exchangeId, strategyId);
        }
        else if (nonNull(fromTimestamp) && nonNull(toTimestamp) && nonNull(exchangeId))
        {
            return tradeHistoryItemRepository.findTrades(fromTimestamp, toTimestamp, exchangeId);
        }
        else if (nonNull(fromTimestamp) && nonNull(toTimestamp))
        {
            return tradeHistoryItemRepository.findTrades(fromTimestamp, toTimestamp);
        }
        else
        {
            return List.of();
        }
    }

    public List<TradeHistoryItem> getAllTrades()
    {
        return tradeHistoryItemRepository.findTrades();
    }

    public List<String> searchForExchanges(Long fromTimestamp, Long toTimestamp)
    {
        return tradeHistoryItemRepository.findTrades(fromTimestamp, toTimestamp)
            .stream()
            .map(TradeHistoryItem::getExchangeId)
            .distinct()
            .collect(toList());
    }

    public List<String> searchForStrategies(Long fromTimestamp, Long toTimestamp, String exchangeId)
    {
        return tradeHistoryItemRepository.findTrades(fromTimestamp, toTimestamp, exchangeId)
            .stream()
            .map(TradeHistoryItem::getStrategyId)
            .distinct()
            .collect(toList());
    }
}
