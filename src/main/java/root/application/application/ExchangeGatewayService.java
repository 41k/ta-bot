package root.application.application;

import lombok.RequiredArgsConstructor;
import root.application.domain.ExchangeGateway;

import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class ExchangeGatewayService
{
    private final Map<String, ExchangeGateway> exchangeGatewaysStore;

    public Set<String> getIds()
    {
        return exchangeGatewaysStore.keySet();
    }
}
