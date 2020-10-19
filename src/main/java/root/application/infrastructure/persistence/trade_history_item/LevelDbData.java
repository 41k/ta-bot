package root.application.infrastructure.persistence.trade_history_item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import root.application.domain.level.Level;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelDbData
{
    String name;
    double value;

    public Level toDomainObject()
    {
        return new Level(name, value);
    }

    public static LevelDbData fromDomainObject(Level level)
    {
        return new LevelDbData(level.getName(), level.getValue());
    }
}
