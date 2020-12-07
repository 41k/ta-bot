package root.application.infrastructure.persistence.trade_history_item;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import root.application.domain.trading.Interval;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table("trade_history_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeHistoryItemDbEntry implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    @NotNull
    String exchangeGateway;
    @NotNull
    String strategyExecutionId;
    @NotNull
    String strategyName;
    @NotNull
    String symbol;
    @NotNull
    Double amount;
    @NotNull
    private Double totalProfit;
    @NotNull
    private Double absoluteProfit;
    @NotNull
    @Column("time_interval")
    @Enumerated(EnumType.STRING)
    Interval interval;
    @NotNull
    private Long entryTimestamp;
    @NotNull
    private Long exitTimestamp;
    @NotNull
    @javax.persistence.Column(columnDefinition = "TEXT")
    private String ticks;
}
