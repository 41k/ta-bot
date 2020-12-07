package root.application.domain.history;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import root.application.domain.strategy.StrategyFactory;
import root.application.domain.trading.ExchangeGateway;
import root.application.domain.trading.StrategyExecutionContext;

@Value
@Builder
public class TradeContext
{
    @NonNull
    ExchangeGateway exchangeGateway;
    @NonNull
    String strategyExecutionId;
    @NonNull
    StrategyExecutionContext strategyExecutionContext;
    @NonNull
    StrategyFactory strategyFactory;
}
