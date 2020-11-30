package root.application.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import root.application.domain.report.TradeHistoryItem;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class HistoryService
{
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10000);

    private final ApplicationLevelTradeHistoryItemRepository tradeHistoryItemRepository;

    public List<String> searchForExchanges(HistoryFilter filter)
    {
        return tradeHistoryItemRepository.findTrades(filter, DEFAULT_PAGEABLE)
            .stream()
            .map(TradeHistoryItem::getExchangeGatewayId)
            .distinct()
            .collect(toList());
    }

    public List<String> searchForStrategies(HistoryFilter filter)
    {
        return tradeHistoryItemRepository.findTrades(filter, DEFAULT_PAGEABLE)
            .stream()
            .map(TradeHistoryItem::getStrategyId)
            .distinct()
            .collect(toList());
    }

    public List<TradeHistoryItem> searchForTrades(HistoryFilter filter, Pageable pageable)
    {
        return tradeHistoryItemRepository.findTrades(filter, pageable);
    }

    public List<TradeHistoryItem> getAllTrades()
    {
        return tradeHistoryItemRepository.findAllTrades();
    }
}
