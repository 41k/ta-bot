package root.application.domain.trading;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import root.application.domain.strategy.StrategyFactory;

import java.util.Map;

@Value
@Builder
@ToString(exclude = {"exchangeGateway", "exchangeGatewayAccountConfiguration", "strategyFactory"})
public class StrategyExecutionContext
{
    @NonNull
    String userId;
    @NonNull
    ExchangeGateway exchangeGateway;
    @NonNull
    Map<ExchangeGatewayAccountConfigurationProperty, String> exchangeGatewayAccountConfiguration;
    @NonNull
    StrategyFactory strategyFactory;
    @NonNull
    Symbol symbol;
    @NonNull
    Double amount;
    @NonNull
    Interval interval;
}
