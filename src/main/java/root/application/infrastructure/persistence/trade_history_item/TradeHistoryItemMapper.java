package root.application.infrastructure.persistence.trade_history_item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import root.application.domain.report.Tick;
import root.application.domain.report.TradeHistoryItem;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class TradeHistoryItemMapper
{
    private final ObjectMapper objectMapper;

    public TradeHistoryItem toDomainObject(TradeHistoryItemDbEntry dbEntry)
    {
        return TradeHistoryItem.builder()
            .strategyId(dbEntry.getStrategyId())
            .exchangeId(dbEntry.getExchangeId())
            .entryTimestamp(dbEntry.getEntryTimestamp())
            .exitTimestamp(dbEntry.getExitTimestamp())
            .profit(dbEntry.getProfit())
            .ticks(toTicksList(dbEntry.getTicks()))
            .build();
    }

    public TradeHistoryItemDbEntry toDbEntry(TradeHistoryItem domainObject)
    {
        return TradeHistoryItemDbEntry.builder()
            .strategyId(domainObject.getStrategyId())
            .exchangeId(domainObject.getExchangeId())
            .entryTimestamp(domainObject.getEntryTimestamp())
            .exitTimestamp(domainObject.getExitTimestamp())
            .profit(domainObject.getProfit())
            .ticks(toTicksDbData(domainObject.getTicks()))
            .build();
    }

    @SneakyThrows
    private List<Tick> toTicksList(String ticksDbData)
    {
        var ticksDbDataList = Arrays.asList(objectMapper.readValue(ticksDbData, TickDbData[].class));
        return ticksDbDataList.stream().map(TickDbData::toDomainObject).collect(toList());
    }

    @SneakyThrows
    private String toTicksDbData(List<Tick> ticksList)
    {
        var ticksDbDataList = ticksList.stream().map(TickDbData::fromDomainObject).collect(toList());
        return objectMapper.writeValueAsString(ticksDbDataList);
    }
}
