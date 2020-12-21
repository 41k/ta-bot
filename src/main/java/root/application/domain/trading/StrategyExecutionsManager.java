package root.application.domain.trading;

import lombok.extern.slf4j.Slf4j;
import root.application.domain.history.TradeHistoryItemRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Slf4j
public class StrategyExecutionsManager
{
    private final Map<String, StrategyExecution> strategyExecutionsStore;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;

    public StrategyExecutionsManager(TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        this.strategyExecutionsStore = new HashMap<>();
        this.tradeHistoryItemRepository = tradeHistoryItemRepository;
    }

    public void runStrategyExecution(StrategyExecutionContext strategyExecutionContext)
    {
        var strategyExecution = new StrategyExecution(strategyExecutionContext, tradeHistoryItemRepository);
        strategyExecutionsStore.put(strategyExecution.getId(), strategyExecution);
    }

    public void stopStrategyExecution(String strategyExecutionId)
    {
        ofNullable(strategyExecutionsStore.get(strategyExecutionId)).ifPresentOrElse(
            StrategyExecution::stop,
            () -> log.warn("Strategy execution with id [{}] is not found.", strategyExecutionId)
        );
    }

    public Collection<StrategyExecution.State> getStrategyExecutions()
    {
        return strategyExecutionsStore.values()
            .stream()
            .filter(StrategyExecution::isActive)
            .map(StrategyExecution::getState)
            .collect(toList());
    }
}
