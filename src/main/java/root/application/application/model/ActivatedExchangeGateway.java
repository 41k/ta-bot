package root.application.application.model;

import lombok.Builder;
import lombok.Value;
import root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty;

import java.util.Map;

@Value
@Builder
public class ActivatedExchangeGateway
{
    String id;
    String name;
    Long accountId;
    Map<ExchangeGatewayAccountConfigurationProperty, String> accountConfiguration;
}
