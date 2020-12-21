package root.application.application.model.command;

import lombok.Value;
import root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty;

import java.util.Map;

@Value
public class CreateExchangeGatewayAccountCommand
{
    String exchangeGatewayId;
    Map<ExchangeGatewayAccountConfigurationProperty, String> configuration;
}
