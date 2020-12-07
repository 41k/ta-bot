package root.application.infrastructure.persistence.trade_history_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ta4j.core.Order;
import root.application.domain.history.Tick;

import java.util.List;
import java.util.Map;

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
    private List<LevelDbData> levels;
    private Map<String, Double> mainChartNumIndicators;
    private Map<String, Double> additionalChartNumIndicators;

    public Tick toDomainObject()
    {
        var levelDomainObjectList = levels.stream().map(LevelDbData::toDomainObject).collect(toList());
        return Tick.builder()
            .open(open)
            .high(high)
            .low(low)
            .close(close)
            .volume(volume)
            .timestamp(timestamp)
            .signal(signal)
            .levels(levelDomainObjectList)
            .mainChartNumIndicators(mainChartNumIndicators)
            .additionalChartNumIndicators(additionalChartNumIndicators)
            .build();
    }

    public static TickDbData fromDomainObject(Tick tick)
    {
        var levelDbDataList = tick.getLevels().stream().map(LevelDbData::fromDomainObject).collect(toList());
        return TickDbData.builder()
            .open(tick.getOpen())
            .high(tick.getHigh())
            .low(tick.getLow())
            .close(tick.getClose())
            .volume(tick.getVolume())
            .timestamp(tick.getTimestamp())
            .signal(tick.getSignal())
            .levels(levelDbDataList)
            .mainChartNumIndicators(tick.getMainChartNumIndicators())
            .additionalChartNumIndicators(tick.getAdditionalChartNumIndicators())
            .build();
    }
}
