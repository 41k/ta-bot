package root.application.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import root.application.application.model.HistoryFilter;
import root.application.application.model.StrategyExecutionInfo;
import root.application.application.repository.ApplicationLevelTradeHistoryItemRepository;
import root.application.domain.history.TradeHistoryItem;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class HistoryService
{
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10000);

    private final ApplicationLevelTradeHistoryItemRepository tradeHistoryItemRepository;

    public List<String> searchForExchangeGateways(HistoryFilter filter)
    {
        return tradeHistoryItemRepository.findTrades(filter, DEFAULT_PAGEABLE)
            .stream()
            .map(TradeHistoryItem::getExchangeGateway)
            .distinct()
            .collect(toList());
    }

    public List<StrategyExecutionInfo> searchForStrategyExecutions(HistoryFilter filter)
    {
        return tradeHistoryItemRepository.findTrades(filter, DEFAULT_PAGEABLE)
            .stream()
            .map(this::buildStrategyExecutionInfo)
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

    private StrategyExecutionInfo buildStrategyExecutionInfo(TradeHistoryItem tradeHistoryItem)
    {
        return StrategyExecutionInfo.builder()
            .id(tradeHistoryItem.getStrategyExecutionId())
            .strategyName(tradeHistoryItem.getStrategyName())
            .symbol(tradeHistoryItem.getSymbol())
            .amount(tradeHistoryItem.getAmount())
            .interval(tradeHistoryItem.getInterval())
            .build();
    }
}
