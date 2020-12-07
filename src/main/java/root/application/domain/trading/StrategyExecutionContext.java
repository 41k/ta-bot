package root.application.domain.trading;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class StrategyExecutionContext
{
    @NonNull
    String strategyId;
    @NonNull
    String symbol;
    @NonNull
    double amount;
    @NonNull
    Interval interval;
}
