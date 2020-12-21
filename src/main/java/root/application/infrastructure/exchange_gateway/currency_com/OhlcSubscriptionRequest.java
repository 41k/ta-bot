package root.application.infrastructure.exchange_gateway.currency_com;

import lombok.Getter;
import lombok.Value;

import java.util.Collection;

public class OhlcSubscriptionRequest
{
    @Getter
    private final String destination;
    @Getter
    private final Payload payload;

    public OhlcSubscriptionRequest(String destination, Collection<String> symbols, Collection<String> intervals)
    {
        this.destination = destination;
        this.payload = new Payload(symbols, intervals);
    }

    @Value
    public static class Payload
    {
        Collection<String> symbols;
        Collection<String> intervals;
    }
}
