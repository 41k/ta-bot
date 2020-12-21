package root.application.application.model;

import lombok.Builder;
import lombok.Value;
import root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty;

import java.util.Map;

@Value
@Builder
public class ExchangeGatewayAccount
{
    Long id;
    Long userId;
    String exchangeGatewayId;
    Map<ExchangeGatewayAccountConfigurationProperty, String> configuration;
}
