package root.application.infrastructure.persistence.trade_history_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.Column;
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
    private String strategyId;
    @NotNull
    private String exchangeGatewayId;
    @NotNull
    private Long entryTimestamp;
    @NotNull
    private Long exitTimestamp;
    @NotNull
    private Double profit;
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String ticks;
}
