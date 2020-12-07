package root.application.application.service;

import lombok.RequiredArgsConstructor;
import root.application.application.model.ExchangeGatewayInfo;
import root.application.domain.trading.ExchangeGateway;
import root.application.domain.trading.Interval;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class ExchangeGatewayService
{
    private final Map<String, ExchangeGateway> exchangeGatewaysStore;

    public Set<ExchangeGatewayInfo> getExchangeGateways()
    {
        return exchangeGatewaysStore.values()
            .stream()
            .map(exchangeGateway ->
                ExchangeGatewayInfo.builder()
                    .id(exchangeGateway.getId())
                    .name(exchangeGateway.getName())
                    .build())
            .collect(toSet());
    }

    public Set<Interval> getSupportedIntervals(String exchangeGatewayId)
    {
        return getExchangeGateway(exchangeGatewayId).getSupportedIntervals();
    }

    public Set<String> getSupportedSymbols(String exchangeGatewayId)
    {
        return getExchangeGateway(exchangeGatewayId).getSupportedSymbols();
    }

    private ExchangeGateway getExchangeGateway(String exchangeGatewayId)
    {
        return ofNullable(exchangeGatewaysStore.get(exchangeGatewayId)).orElseThrow(
            () -> new NoSuchElementException(format("Exchange gateway with id [%s] is not found.", exchangeGatewayId)));
    }
}
