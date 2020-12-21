package root.application.infrastructure.persistence.trade_history_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

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
    private String userId;
    @NotNull
    private String exchangeGateway;
    @NotNull
    private String strategyExecutionId;
    @NotNull
    private String strategyName;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Symbol symbol;
    @NotNull
    private Double amount;
    @NotNull
    @Column("time_interval")
    @Enumerated(EnumType.STRING)
    private Interval interval;
    @NotNull
    private Double totalProfit;
    @NotNull
    private Double absoluteProfit;
    @NotNull
    private Long entryTimestamp;
    @NotNull
    private Long exitTimestamp;
    @NotNull
    @javax.persistence.Column(columnDefinition = "MEDIUMTEXT")
    private String ticks;
}
