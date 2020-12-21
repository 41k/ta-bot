package root.application.domain.history;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

import java.util.List;

@Value
@Builder
public class TradeHistoryItem
{
    @NonNull
    String userId;
    @NonNull
    String exchangeGateway;
    @NonNull
    String strategyExecutionId;
    @NonNull
    String strategyName;
    @NonNull
    Symbol symbol;
    @NonNull
    Double amount;
    @NonNull
    Interval interval;
    @NonNull
    Double totalProfit;
    @NonNull
    Double absoluteProfit;
    @NonNull
    Long entryTimestamp;
    @NonNull
    Long exitTimestamp;
    @NonNull
    List<Tick> ticks;
}
