package root.application.domain.history;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.ta4j.core.BarSeries;
import root.application.domain.strategy.Strategy;
import root.application.domain.strategy.StrategyFactory;
import root.application.domain.trading.ExchangeGateway;
import root.application.domain.trading.StrategyExecutionContext;

@Value
@Builder
public class TradeContext
{
    @NonNull
    String strategyExecutionId;
    @NonNull
    StrategyExecutionContext strategyExecutionContext;
    @NonNull
    BarSeries series;
    @NonNull
    Strategy strategy;
}
