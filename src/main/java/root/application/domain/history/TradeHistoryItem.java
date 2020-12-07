package root.application.domain.history;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import root.application.domain.trading.Interval;

import java.util.List;

@Value
@Builder
public class TradeHistoryItem
{
    @NonNull
    String exchangeGateway;
    @NonNull
    String strategyExecutionId;
    @NonNull
    String strategyName;
    @NonNull
    String symbol;
    @NonNull
    Double amount;
    @NonNull
    Double totalProfit;
    @NonNull
    Double absoluteProfit;
    @NonNull
    Interval interval;
    @NonNull
    Long entryTimestamp;
    @NonNull
    Long exitTimestamp;
    @NonNull
    List<Tick> ticks;
}
