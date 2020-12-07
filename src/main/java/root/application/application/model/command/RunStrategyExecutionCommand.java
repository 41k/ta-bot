package root.application.application.model.command;

import lombok.Builder;
import lombok.Value;
import root.application.domain.trading.Interval;

@Value
@Builder
public class RunStrategyExecutionCommand
{
    String exchangeGatewayId;
    String strategyId;
    String symbol;
    double amount;
    Interval interval;
}
