package root.application.presentation.dto;

import lombok.Data;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

@Data
public class RunStrategyExecutionRequest
{
    private String strategyId;
    private Symbol symbol;
    private Double amount;
    private Interval interval;
}
