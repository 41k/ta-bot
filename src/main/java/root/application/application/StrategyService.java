package root.application.application;

import lombok.RequiredArgsConstructor;
import root.application.domain.trading.StrategiesExecutor;
import root.application.domain.trading.StrategyExecution;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class StrategyService
{
    private final Map<String, StrategiesExecutor> strategyExecutorsStore;

    public List<StrategyExecution.State> getStrategyExecutions(String exchangeGatewayId)
    {
        return getStrategiesExecutor(exchangeGatewayId).getStrategyExecutions();
    }

    public List<String> getInactiveStrategyIds(String exchangeGatewayId)
    {
        return getStrategiesExecutor(exchangeGatewayId).getInactiveStrategyIds();
    }

    public void activateStrategy(String exchangeGatewayId, String strategyId, double amount)
    {
        getStrategiesExecutor(exchangeGatewayId).activateStrategy(strategyId, amount);
    }

    public void deactivateStrategy(String exchangeGatewayId, String strategyId)
    {
        getStrategiesExecutor(exchangeGatewayId).deactivateStrategy(strategyId);
    }

    private StrategiesExecutor getStrategiesExecutor(String exchangeGatewayId)
    {
        return ofNullable(strategyExecutorsStore.get(exchangeGatewayId)).orElseThrow(() -> new NoSuchElementException(
            format("Strategies Executor is not found for exchange gateway [%s].", exchangeGatewayId)));
    }
}
