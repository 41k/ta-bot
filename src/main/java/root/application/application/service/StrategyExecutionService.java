package root.application.application.service;

import lombok.RequiredArgsConstructor;
import root.application.application.model.StrategyExecutionInfo;
import root.application.application.model.StrategyExecutionStatistics;
import root.application.application.model.command.RunStrategyExecutionCommand;
import root.application.application.model.command.StopStrategyExecutionCommand;
import root.application.application.repository.ApplicationLevelTradeHistoryItemRepository;
import root.application.domain.history.TradeHistoryItem;
import root.application.domain.trading.StrategyExecution;
import root.application.domain.trading.StrategyExecutionContext;
import root.application.domain.trading.StrategyExecutionsManager;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class StrategyExecutionService
{
    private final Map<String, StrategyExecutionsManager> strategyExecutionManagersStore;
    private final ApplicationLevelTradeHistoryItemRepository tradeHistoryItemRepository;

    public List<StrategyExecutionInfo> getStrategyExecutions(String exchangeGatewayId)
    {
        return getStrategyExecutionsManager(exchangeGatewayId).getStrategyExecutions()
            .stream()
            .map(this::buildStrategyExecutionInfo)
            .collect(toList());
    }

    public void execute(RunStrategyExecutionCommand command)
    {
        var exchangeGatewayId = command.getExchangeGatewayId();
        var strategyExecutionsManager = getStrategyExecutionsManager(exchangeGatewayId);
        var strategyExecutionContext = StrategyExecutionContext.builder()
            .strategyId(command.getStrategyId())
            .symbol(command.getSymbol())
            .amount(command.getAmount())
            .interval(command.getInterval())
            .build();
        strategyExecutionsManager.runStrategyExecution(strategyExecutionContext);
    }

    public void execute(StopStrategyExecutionCommand command)
    {
        var exchangeGatewayId = command.getExchangeGatewayId();
        var strategyExecutionsManager = getStrategyExecutionsManager(exchangeGatewayId);
        var strategyExecutionId = command.getStrategyExecutionId();
        strategyExecutionsManager.stopStrategyExecution(strategyExecutionId);
    }

    private StrategyExecutionsManager getStrategyExecutionsManager(String exchangeGatewayId)
    {
        return ofNullable(strategyExecutionManagersStore.get(exchangeGatewayId)).orElseThrow(() -> new NoSuchElementException(
            format("Strategy executions manager for exchange gateway [%s] is not found.", exchangeGatewayId)));
    }

    private StrategyExecutionInfo buildStrategyExecutionInfo(StrategyExecution.State strategyExecutionState)
    {
        var strategyExecutionId = strategyExecutionState.getId();
        var strategyExecutionStatistics = calculateStrategyExecutionStatistics(strategyExecutionId);
        return StrategyExecutionInfo.builder()
            .id(strategyExecutionId)
            .startTime(strategyExecutionState.getStartTime())
            .status(strategyExecutionState.getStatus())
            .strategyName(strategyExecutionState.getStrategyName())
            .symbol(strategyExecutionState.getSymbol())
            .amount(strategyExecutionState.getAmount())
            .interval(strategyExecutionState.getInterval())
            .statistics(strategyExecutionStatistics)
            .build();
    }

    private StrategyExecutionStatistics calculateStrategyExecutionStatistics(String strategyExecutionId)
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
}
