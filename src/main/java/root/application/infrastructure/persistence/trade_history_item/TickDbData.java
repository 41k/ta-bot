package root.application.infrastructure.persistence.trade_history_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ta4j.core.Order;
import root.application.domain.history.Tick;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TickDbData
{
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private Long timestamp;
    private Order.OrderType signal;
    private List<MainChartLevelDbData> mainChartLevels;
    private Map<String, Double> mainChartNumIndicators;
    private Map<String, Double> additionalChartNumIndicators;

    public Tick toDomainObject()
    {
        // Wrapping with Optional.ofNullable() is necessary for backward compatibility,
        // since "levels" property was renamed to "mainChartLevels"
        var mainChartLevelDomainObjectList = ofNullable(mainChartLevels).orElseGet(List::of)
            .stream()
            .map(MainChartLevelDbData::toDomainObject)
            .collect(toList());
        return Tick.builder()
            .open(open)
            .high(high)
            .low(low)
            .close(close)
            .volume(volume)
            .timestamp(timestamp)
            .signal(signal)
            .mainChartLevels(mainChartLevelDomainObjectList)
            .mainChartNumIndicators(mainChartNumIndicators)
            .additionalChartNumIndicators(additionalChartNumIndicators)
            .build();
    }

    public static TickDbData fromDomainObject(Tick tick)
    {
        var mainChartLevelDbDataList = tick.getMainChartLevels()
            .stream()
            .map(MainChartLevelDbData::fromDomainObject)
            .collect(toList());
        return TickDbData.builder()
            .open(tick.getOpen())
            .high(tick.getHigh())
            .low(tick.getLow())
            .close(tick.getClose())
            .volume(tick.getVolume())
            .timestamp(tick.getTimestamp())
            .signal(tick.getSignal())
            .mainChartLevels(mainChartLevelDbDataList)
            .mainChartNumIndicators(tick.getMainChartNumIndicators())
            .additionalChartNumIndicators(tick.getAdditionalChartNumIndicators())
            .build();
    }
}
