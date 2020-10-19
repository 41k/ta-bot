package root.application.domain.report;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TradeHistoryItem
{
    @NonNull
    String strategyId;
    @NonNull
    String exchangeId;
    @NonNull
    Long entryTimestamp;
    @NonNull
    Long exitTimestamp;
    @NonNull
    Double profit;
    @NonNull
    List<Tick> ticks;
}
