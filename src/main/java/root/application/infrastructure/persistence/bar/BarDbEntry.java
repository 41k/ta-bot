package root.application.infrastructure.persistence.bar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;

@Table("bar")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarDbEntry implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static final double AMOUNT = 0d;
    private static final int TRADES = 0;
    private static final Function<Number, Num> NUM_FUNCTION = PrecisionNum::valueOf;

    @Id
    private Long id;
    @NotNull
    private String exchangeGatewayId;
    @NotNull
    private Long duration;
    @NotNull
    private Long timestamp;
    @NotNull
    private Double open;
    @NotNull
    private Double high;
    @NotNull
    private Double low;
    @NotNull
    private Double close;
    @NotNull
    private Double volume;

    public Bar toDomainObject()
    {
        var barDuration = Duration.ofMillis(duration);
        var barTime = Instant.ofEpochMilli(timestamp);
        var zonedBarTime = ZonedDateTime.ofInstant(barTime, ZoneId.systemDefault());
        return new BaseBar(barDuration, zonedBarTime, open, high, low, close, volume, AMOUNT, TRADES, NUM_FUNCTION);
    }

    public static BarDbEntry fromDomainObject(Bar bar, String exchangeGatewayId)
    {
        return BarDbEntry.builder()
            .exchangeGatewayId(exchangeGatewayId)
            .duration(bar.getTimePeriod().toMillis())
            .timestamp(bar.getEndTime().toInstant().toEpochMilli())
            .open(bar.getOpenPrice().doubleValue())
            .high(bar.getHighPrice().doubleValue())
            .low(bar.getLowPrice().doubleValue())
            .close(bar.getClosePrice().doubleValue())
            .volume(bar.getVolume().doubleValue())
            .build();
    }
}
