package root.application.application.model;

import lombok.Builder;
import lombok.Value;
import root.application.domain.trading.Interval;
import root.application.domain.trading.StrategyExecutionStatus;

@Value
@Builder
public class StrategyExecutionInfo
{
    String id;
    StrategyExecutionStatus status;
    String strategyName;
    String symbol;
    double amount;
    Interval interval;
    StrategyExecutionStatistics statistics;
    long startTime;
}
