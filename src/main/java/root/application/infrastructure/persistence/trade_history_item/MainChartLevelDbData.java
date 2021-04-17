package root.application.infrastructure.persistence.trade_history_item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import root.application.domain.level.MainChartLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainChartLevelDbData
{
    private String name;
    private double value;

    public MainChartLevel toDomainObject()
    {
        return new MainChartLevel(name, value);
    }

    public static MainChartLevelDbData fromDomainObject(MainChartLevel mainChartLevel)
    {
        return new MainChartLevelDbData(mainChartLevel.getName(), mainChartLevel.getValue());
    }
}
