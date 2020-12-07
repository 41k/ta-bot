package root.application.domain.trading;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import root.application.domain.history.TradeHistoryItemRepository;
import root.application.domain.strategy.StrategyFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Slf4j
public class StrategyExecutionsManager
{
    private final Map<String, StrategyFactory> strategyIdToFactoryMap;
    private final Map<String, StrategyExecution> strategyExecutionsStore;
    private final ExchangeGateway exchangeGateway;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;

    public StrategyExecutionsManager(Map<String, StrategyFactory> strategyIdToFactoryMap,
                                     ExchangeGateway exchangeGateway,
                                     TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        this.strategyIdToFactoryMap = strategyIdToFactoryMap;
        this.strategyExecutionsStore = new HashMap<>();
        this.exchangeGateway = exchangeGateway;
        this.tradeHistoryItemRepository = tradeHistoryItemRepository;
    }

    public void runStrategyExecution(StrategyExecutionContext strategyExecutionContext)
    {
        var strategyId = strategyExecutionContext.getStrategyId();
        var strategyFactory = ofNullable(strategyIdToFactoryMap.get(strategyId)).orElseThrow(() ->
            new NoSuchElementException(format("Strategy factory for strategyId [%s] is not found.", strategyId)));
        var strategyExecution = new StrategyExecution(
            strategyFactory, exchangeGateway, strategyExecutionContext, tradeHistoryItemRepository);
        strategyExecutionsStore.put(strategyExecution.getId(), strategyExecution);
    }

    public void stopStrategyExecution(String strategyExecutionId)
    {
        ofNullable(strategyExecutionsStore.get(strategyExecutionId)).ifPresentOrElse(
            StrategyExecution::stop,
            () -> log.warn("Strategy execution with id [{}] is not found.", strategyExecutionId)
        );
    }

    public List<StrategyExecution.State> getStrategyExecutions()
    {
        return strategyExecutionsStore.values()
            .stream()
            .filter(StrategyExecution::isActive)
            .map(StrategyExecution::getState)
            .collect(toList());
    }

    @Value
    private static class StrategyExecutionStoreKey
    {
        String strategyId;
        Interval interval;
    }
}
