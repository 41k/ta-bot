package root.application.presentation.dto;

import lombok.Data;
import root.application.domain.trading.Interval;

@Data
public class RunStrategyExecutionRequest
{
    private String strategyId;
    private String symbol;
    private double amount;
    private Interval interval;
}
