package root.application.domain.report;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Trade;
import org.ta4j.core.num.Num;
import root.application.domain.indicator.AdditionalChartNumIndicator;
import root.application.domain.indicator.Indicator;
import root.application.domain.indicator.MainChartNumIndicator;
import root.application.domain.level.Level;
import root.application.domain.strategy.StrategyFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class TradeHistoryItemBuilder
{
    private static final int DEFAULT_N_TICKS_BEFORE_TRADE = 20;
    private static final int DEFAULT_N_TICKS_AFTER_TRADE = 20;

    public TradeHistoryItem build(Trade trade, StrategyFactory strategyFactory, String exchangeGatewayId)
    {
        var ticksBeforeTrade = getTicksBeforeTrade(trade, strategyFactory);
        var ticksDuringTrade = getTicksDuringTrade(trade, strategyFactory);
        var ticksAfterTrade = getTicksAfterTrade(trade, strategyFactory);
        var ticks = Stream.of(ticksBeforeTrade, ticksDuringTrade, ticksAfterTrade)
                .flatMap(Collection::stream)
                .collect(toList());
        var entryTimestamp = getEntryTimestamp(ticksDuringTrade);
        var exitTimestamp = getExitTimestamp(ticksDuringTrade);
        return TradeHistoryItem.builder()
                .strategyId(strategyFactory.getStrategyId())
                .exchangeGatewayId(exchangeGatewayId)
                .entryTimestamp(entryTimestamp)
                .exitTimestamp(exitTimestamp)
                .profit(trade.getProfit().doubleValue())
                .ticks(ticks)
                .build();
    }

    private List<Tick> getTicksBeforeTrade(Trade trade, StrategyFactory strategyFactory)
    {
        var entryOrderIndex = trade.getEntry().getIndex();
        var series = strategyFactory.getBarSeries();
        var seriesBeginIndex = series.getBeginIndex();
        if (entryOrderIndex == seriesBeginIndex)
        {
            return List.of();
        }
        var ticks = new ArrayList<Tick>();
        var nTicksBeforeTrade = strategyFactory.getUnstablePeriodLength().orElse(DEFAULT_N_TICKS_BEFORE_TRADE);
        var startIndex = Math.max(0, entryOrderIndex - nTicksBeforeTrade - 1);
        for (int i = startIndex; i < entryOrderIndex; i++)
        {
            var tick = getTickBuilder(i, strategyFactory).build();
            ticks.add(tick);
        }
        return ticks;
    }

    private List<Tick> getTicksAfterTrade(Trade trade, StrategyFactory strategyFactory)
    {
        var exitOrderIndex = trade.getExit().getIndex();
        var series = strategyFactory.getBarSeries();
        var seriesEndIndex = series.getEndIndex();
        if (exitOrderIndex == seriesEndIndex)
        {
            return List.of();
        }
        var ticks = new ArrayList<Tick>();
        var startIndex = exitOrderIndex + 1;
        var endIndex = Math.min(exitOrderIndex + DEFAULT_N_TICKS_AFTER_TRADE + 1, seriesEndIndex);
        for (int i = startIndex; i <= endIndex; i++)
        {
            var tick = getTickBuilder(i, strategyFactory).build();
            ticks.add(tick);
        }
        return ticks;
    }

    private List<Tick> getTicksDuringTrade(Trade trade, StrategyFactory strategyFactory)
    {
        var ticks = new ArrayList<Tick>();
        var entryOrder = trade.getEntry();
        var exitOrder = trade.getExit();
        var entryOrderIndex = entryOrder.getIndex();
        var exitOrderIndex = exitOrder.getIndex();
        for (int i = entryOrderIndex; i <= exitOrderIndex; i++)
        {
            var tickBuilder = getTickBuilder(i, strategyFactory);
            if (i == entryOrderIndex)
            {
                tickBuilder.signal(entryOrder.getType())
                        .levels(getEntryOrderRelatedLevels(i, strategyFactory));
            }
            else if (i == exitOrderIndex)
            {
                tickBuilder.signal(exitOrder.getType());
            }
            ticks.add(tickBuilder.build());
        }
        return ticks;
    }

    private List<Level> getEntryOrderRelatedLevels(int index, StrategyFactory strategyFactory)
    {
        return strategyFactory.getStopLossLevelProvider()
                .map(levelProvider -> levelProvider.getLevel(index))
                .map(List::of)
                .orElseGet(List::of);
    }

    private Tick.TickBuilder getTickBuilder(int index, StrategyFactory strategyFactory)
    {
        var bar = strategyFactory.getBarSeries().getBar(index);
        var mainChartNumIndicators = getMainChartNumIndicators(index, strategyFactory);
        var additionalChartNumIndicators = getAdditionalChartNumIndicators(index, strategyFactory);
        return Tick.builder()
                .open(bar.getOpenPrice().doubleValue())
                .high(bar.getHighPrice().doubleValue())
                .low(bar.getLowPrice().doubleValue())
                .close(bar.getClosePrice().doubleValue())
                .volume(bar.getVolume().doubleValue())
                .timestamp(bar.getEndTime().toInstant().toEpochMilli())
                .mainChartNumIndicators(mainChartNumIndicators)
                .additionalChartNumIndicators(additionalChartNumIndicators);
    }

    private Map<String, Double> getMainChartNumIndicators(int index, StrategyFactory strategyFactory)
    {
        Predicate<Indicator<Num>> predicate = indicator -> indicator instanceof MainChartNumIndicator;
        return getNumberIndicators(index, strategyFactory, predicate);
    }

    private Map<String, Double> getAdditionalChartNumIndicators(int index, StrategyFactory strategyFactory)
    {
        Predicate<Indicator<Num>> predicate = indicator -> indicator instanceof AdditionalChartNumIndicator;
        return getNumberIndicators(index, strategyFactory, predicate);
    }

    private Map<String, Double> getNumberIndicators(int index, StrategyFactory strategyFactory, Predicate<Indicator<Num>> predicate)
    {
        var indicatorNameToValueMap = new LinkedHashMap<String, Double>();
        strategyFactory.getNumIndicators()
                .stream()
                .filter(predicate)
                .forEach(indicator -> {
                    var indicatorName = indicator.getName();
                    var indicatorValue = getIndicatorValue(indicator, index);
                    indicatorNameToValueMap.put(indicatorName, indicatorValue);
                });
        return indicatorNameToValueMap;
    }

    private Double getIndicatorValue(Indicator<Num> indicator, int index)
    {
        var indicatorValue = indicator.getValue(index);
        return indicatorValue.isNaN() ? null : indicatorValue.doubleValue();
    }

    private Long getEntryTimestamp(List<Tick> ticksDuringTrade)
    {
        var entryTick = ticksDuringTrade.get(0);
        return entryTick.getTimestamp();
    }

    private Long getExitTimestamp(List<Tick> ticksDuringTrade)
    {
        var lastIndex = ticksDuringTrade.size() - 1;
        var exitTick = ticksDuringTrade.get(lastIndex);
        return exitTick.getTimestamp();
    }
}
