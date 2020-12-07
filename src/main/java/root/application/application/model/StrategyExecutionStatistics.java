package root.application.application.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StrategyExecutionStatistics
{
    long nProfitableTrades;
    long nUnprofitableTrades;
    double totalProfit;
}
