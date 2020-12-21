package root.application.application;

import root.application.domain.trading.StrategyExecutionsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class StrategyExecutionManagersStore
{
    private final Map<Long, StrategyExecutionsManager> store = new HashMap<>();

    public Optional<StrategyExecutionsManager> get(Long exchangeGatewayAccountId)
    {
        return ofNullable(store.get(exchangeGatewayAccountId));
    }

    public StrategyExecutionsManager getOrThrowException(Long exchangeGatewayAccountId)
    {
        return get(exchangeGatewayAccountId).orElseThrow(() -> new NoSuchElementException(format(
            "Strategy executions manager is not found for exchangeGatewayAccountId [%d].", exchangeGatewayAccountId)));
    }

    public void put(Long exchangeGatewayAccountId, StrategyExecutionsManager strategyExecutionsManager)
    {
        store.put(exchangeGatewayAccountId, strategyExecutionsManager);
    }

    public void deleteByExchangeGatewayAccountId(Long exchangeGatewayAccountId)
    {
        store.remove(exchangeGatewayAccountId);
    }
}
