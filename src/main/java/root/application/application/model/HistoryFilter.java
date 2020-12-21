package root.application.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class HistoryFilter
{
    private String userId;
    private Long fromTimestamp;
    private Long toTimestamp;
    private String exchangeGateway;
    private String strategyExecutionId;
}
