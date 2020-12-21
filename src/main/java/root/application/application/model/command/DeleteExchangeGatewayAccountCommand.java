package root.application.application.model.command;

import lombok.Value;

@Value
public class DeleteExchangeGatewayAccountCommand
{
    String exchangeGatewayId;
    Long accountId;
}
