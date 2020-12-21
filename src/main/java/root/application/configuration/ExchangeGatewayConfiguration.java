package root.application.configuration;

import lombok.Data;
import org.springframework.validation.annotation.Validated;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Map;

@Data
@Validated
public class ExchangeGatewayConfiguration
{
    @NotEmpty
    private String id;
    @NotEmpty
    private String name;
    private double transactionFee;
    @NotEmpty
    private Map<Symbol, String> symbolToRepresentationMap;
    @NotEmpty
    private Map<Interval, String> intervalToRepresentationMap;

    public Collection<Symbol> getSymbols()
    {
        return symbolToRepresentationMap.keySet();
    }

    public Collection<String> getSymbolsRepresentations()
    {
        return symbolToRepresentationMap.values();
    }

    public Symbol getSymbol(String representation)
    {
        return symbolToRepresentationMap.entrySet().stream()
            .filter(entry -> entry.getValue().equals(representation))
            .findFirst()
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    public String getSymbolRepresentation(Symbol symbol)
    {
        return symbolToRepresentationMap.get(symbol);
    }

    public Collection<Interval> getIntervals()
    {
        return intervalToRepresentationMap.keySet();
    }

    public Collection<String> getIntervalsRepresentations()
    {
        return intervalToRepresentationMap.values();
    }

    public Interval getInterval(String representation)
    {
        return intervalToRepresentationMap.entrySet().stream()
            .filter(entry -> entry.getValue().equals(representation))
            .findFirst()
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}
