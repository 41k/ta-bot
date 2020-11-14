package root.application.domain.trading;

import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import root.application.domain.ExchangeGateway;
import root.application.domain.report.TradeHistoryItemRepository;
import root.application.domain.strategy.StrategyFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Slf4j
public class StrategiesExecutor
{
    private final Map<String, StrategyFactory> strategyIdToFactoryMap;
    private final Map<String, StrategyExecution> strategyIdToExecutionMap;
    private final ExchangeGateway exchangeGateway;
    private final String exchangeId;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;

    public StrategiesExecutor(Map<String, StrategyFactory> strategyIdToFactoryMap,
                              ExchangeGateway exchangeGateway,
                              TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        this.strategyIdToFactoryMap = strategyIdToFactoryMap;
        this.strategyIdToExecutionMap = new HashMap<>();
        this.exchangeGateway = exchangeGateway;
        this.exchangeId = exchangeGateway.getExchangeId();
        this.tradeHistoryItemRepository = tradeHistoryItemRepository;
        exchangeGateway.subscribeToBarStream(this::processBar);
    }

    public void activateStrategy(String strategyId, double amount)
    {
        if (isActiveStrategy(strategyId))
        {
            throw new IllegalStateException(format("Strategy [%s] is already active for exchange [%s].", strategyId, exchangeId));
        }
        var strategyFactory = ofNullable(strategyIdToFactoryMap.get(strategyId)).orElseThrow(() ->
            new IllegalStateException(format("Strategy factory for strategyId [%s] is not found.", strategyId)));
        var strategyExecution = new StrategyExecution(strategyFactory, exchangeGateway, amount);
        strategyIdToExecutionMap.put(strategyId, strategyExecution);
        log.info("Strategy [{}] for exchange [{}] has been activated successfully.", strategyId, exchangeId);
    }

    public void deactivateStrategy(String strategyId)
    {
        var strategyExecution = strategyIdToExecutionMap.get(strategyId);
        if (isNull(strategyExecution))
        {
            log.warn("Active strategy with id [{}] is not found.", strategyId);
            return;
        }
        strategyExecution.stop();
        log.info("Strategy [{}] for exchange [{}] has been deactivated successfully.", strategyId, exchangeId);
    }

    private void processBar(Bar bar)
    {
        strategyIdToExecutionMap.values().stream()
            .map(strategyExecution -> strategyExecution.processBar(bar))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(tradeHistoryItemRepository::save);
    }

    private boolean isActiveStrategy(String strategyId)
    {
        return ofNullable(strategyIdToExecutionMap.get(strategyId))
            .map(StrategyExecution::isActive)
            .orElse(FALSE);
    }
}