package root.application.domain.report;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.ta4j.core.Order;
import root.application.domain.level.Level;

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
    List<Level> levels = List.of();
    @Builder.Default
    Map<String, Double> mainChartNumIndicators = Map.of();
    @Builder.Default
    Map<String, Double> additionalChartNumIndicators = Map.of();
}
