package root.application.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryFilter
{
    private Long fromTimestamp;
    private Long toTimestamp;
    private String exchangeGateway;
    private String strategyExecutionId;
}
