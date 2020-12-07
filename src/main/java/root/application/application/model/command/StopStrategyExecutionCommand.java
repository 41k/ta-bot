package root.application.application.model.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StopStrategyExecutionCommand
{
    String exchangeGatewayId;
    String strategyExecutionId;
}
