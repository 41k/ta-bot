package root.application.domain.trading;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.ta4j.core.Bar;

import java.util.Map;
import java.util.function.Consumer;

@Value
@Builder
@ToString(exclude = {"exchangeGatewayAccountConfiguration", "barStreamSubscriber"})
public class BarStreamSubscriptionContext
{
    @NonNull
    Symbol symbol;
    @NonNull
    Interval interval;
    @NonNull
    String barStreamSubscriberId;
    @NonNull
    Consumer<Bar> barStreamSubscriber;
    @NonNull
    Map<ExchangeGatewayAccountConfigurationProperty, String> exchangeGatewayAccountConfiguration;
}
