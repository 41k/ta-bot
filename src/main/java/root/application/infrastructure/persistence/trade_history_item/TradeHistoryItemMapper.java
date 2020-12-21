package root.application.infrastructure.persistence.trade_history_item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import root.application.domain.history.Tick;
import root.application.domain.history.TradeHistoryItem;

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
            .userId(dbEntry.getUserId())
            .exchangeGateway(dbEntry.getExchangeGateway())
            .strategyExecutionId(dbEntry.getStrategyExecutionId())
            .strategyName(dbEntry.getStrategyName())
            .symbol(dbEntry.getSymbol())
            .amount(dbEntry.getAmount())
            .interval(dbEntry.getInterval())
            .totalProfit(dbEntry.getTotalProfit())
            .absoluteProfit(dbEntry.getAbsoluteProfit())
            .entryTimestamp(dbEntry.getEntryTimestamp())
            .exitTimestamp(dbEntry.getExitTimestamp())
            .ticks(toTicksList(dbEntry.getTicks()))
            .build();
    }

    public TradeHistoryItemDbEntry toDbEntry(TradeHistoryItem domainObject)
    {
        return TradeHistoryItemDbEntry.builder()
            .userId(domainObject.getUserId())
            .exchangeGateway(domainObject.getExchangeGateway())
            .strategyExecutionId(domainObject.getStrategyExecutionId())
            .strategyName(domainObject.getStrategyName())
            .symbol(domainObject.getSymbol())
            .amount(domainObject.getAmount())
            .interval(domainObject.getInterval())
            .totalProfit(domainObject.getTotalProfit())
            .absoluteProfit(domainObject.getAbsoluteProfit())
            .entryTimestamp(domainObject.getEntryTimestamp())
            .exitTimestamp(domainObject.getExitTimestamp())
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
