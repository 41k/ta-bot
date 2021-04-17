package root.application.domain.history;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.ta4j.core.Order;
import root.application.domain.level.MainChartLevel;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class Tick
{
    @NonNull
    Double open;
    @NonNull
    Double high;
    @NonNull
    Double low;
    @NonNull
    Double close;
    @NonNull
    Double volume;
    @NonNull
    Long timestamp;
    Order.OrderType signal;
    @Builder.Default
    List<MainChartLevel> mainChartLevels = List.of();
    @Builder.Default
    Map<String, Double> mainChartNumIndicators = Map.of();
    @Builder.Default
    Map<String, Double> additionalChartNumIndicators = Map.of();
}
