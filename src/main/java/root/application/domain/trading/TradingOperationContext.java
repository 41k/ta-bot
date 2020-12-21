package root.application.domain.trading;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import java.util.Map;

@Value
@Builder
@ToString(exclude = {"exchangeGatewayAccountConfiguration"})
public class TradingOperationContext
{
    @NonNull
    Symbol symbol;
    @NonNull
    Double amount;
    @NonNull
    Double price;
    @NonNull
    Map<ExchangeGatewayAccountConfigurationProperty, String> exchangeGatewayAccountConfiguration;
}
