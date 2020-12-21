package root.application.application.model.command;

import lombok.Builder;
import lombok.Value;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

@Value
@Builder
public class RunStrategyExecutionCommand
{
    String exchangeGatewayId;
    Long exchangeGatewayAccountId;
    String strategyId;
    Symbol symbol;
    Double amount;
    Interval interval;
}
