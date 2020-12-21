package root.application.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import root.application.application.model.HistoryFilter;
import root.application.application.model.StrategyExecutionInfo;
import root.application.application.model.StrategyExecutionStatistics;
import root.application.application.repository.ApplicationLevelTradeHistoryItemRepository;
import root.application.domain.history.TradeHistoryItem;
import root.framework.service.UserService;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class HistoryService
{
    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10000);

    private final ApplicationLevelTradeHistoryItemRepository tradeHistoryItemRepository;
    private final UserService userService;

    public Collection<String> searchForExchangeGateways(HistoryFilter filter)
    {
        var enrichedFilter = enrichFilterWithUserId(filter);
        return tradeHistoryItemRepository.findTrades(enrichedFilter, DEFAULT_PAGEABLE)
            .stream()
            .map(TradeHistoryItem::getExchangeGateway)
            .collect(toSet());
    }

    public Collection<StrategyExecutionInfo> searchForStrategyExecutions(HistoryFilter filter)
    {
        var enrichedFilter = enrichFilterWithUserId(filter);
        return tradeHistoryItemRepository.findTrades(enrichedFilter, DEFAULT_PAGEABLE)
            .stream()
            .map(this::buildStrategyExecutionInfo)
            .collect(toSet());
    }

    public Collection<TradeHistoryItem> searchForTrades(HistoryFilter filter, Pageable pageable)
    {
        var enrichedFilter = enrichFilterWithUserId(filter);
        return tradeHistoryItemRepository.findTrades(enrichedFilter, pageable);
    }

    public Collection<TradeHistoryItem> getAllTrades()
    {
        var userId = userService.getCurrentUserId();
        var filter = HistoryFilter.builder().userId(userId.toString()).build();
        return tradeHistoryItemRepository.findTrades(filter, DEFAULT_PAGEABLE);
    }

    public StrategyExecutionStatistics getStrategyExecutionStatistics(String strategyExecutionId)
    {
        var trades = tradeHistoryItemRepository.findTradesByStrategyExecutionId(strategyExecutionId);
        var nProfitableTrades = trades.stream().mapToDouble(TradeHistoryItem::getTotalProfit).filter(profit -> profit > 0).count();
        var nUnprofitableTrades = trades.stream().mapToDouble(TradeHistoryItem::getTotalProfit).filter(profit -> profit <= 0).count();
        var totalProfit = trades.stream().mapToDouble(TradeHistoryItem::getTotalProfit).sum();
        return StrategyExecutionStatistics.builder()
            .nProfitableTrades(nProfitableTrades)
            .nUnprofitableTrades(nUnprofitableTrades)
            .totalProfit(totalProfit)
            .build();
    }

    private HistoryFilter enrichFilterWithUserId(HistoryFilter filter)
    {
        var userId = userService.getCurrentUserId();
        return filter.toBuilder().userId(userId.toString()).build();
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
