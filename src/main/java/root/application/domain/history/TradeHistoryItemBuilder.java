package root.application.domain.history;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Trade;
import org.ta4j.core.num.Num;
import root.application.domain.ChartType;
import root.application.domain.level.MainChartLevel;
import root.application.domain.strategy.Strategy;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class TradeHistoryItemBuilder
{
    private static final int N_TICKS_BEFORE_TRADE = 20;

    public TradeHistoryItem build(Trade trade, TradeContext tradeContext)
    {
        var strategyExecutionContext = tradeContext.getStrategyExecutionContext();
        var totalProfit = trade.getProfit().doubleValue();
        var amount = strategyExecutionContext.getAmount();
        var series = tradeContext.getSeries();
        var strategy = tradeContext.getStrategy();
        var ticksBeforeTrade = getTicksBeforeTrade(trade, series, strategy);
        var ticksDuringTrade = getTicksDuringTrade(trade, series, strategy);
        var ticks = Stream.of(ticksBeforeTrade, ticksDuringTrade).flatMap(Collection::stream).collect(toList());
        var entryTimestamp = getEntryTimestamp(ticksDuringTrade);
        var exitTimestamp = getExitTimestamp(ticksDuringTrade);
        return TradeHistoryItem.builder()
                .userId(strategyExecutionContext.getUserId())
                .exchangeGateway(strategyExecutionContext.getExchangeGateway().getName())
                .strategyExecutionId(tradeContext.getStrategyExecutionId())
                .strategyName(strategy.getName())
                .symbol(strategyExecutionContext.getSymbol())
                .amount(amount)
                .interval(strategyExecutionContext.getInterval())
                .totalProfit(totalProfit)
                .absoluteProfit(totalProfit / amount)
                .entryTimestamp(entryTimestamp)
                .exitTimestamp(exitTimestamp)
                .ticks(ticks)
                .build();
    }

    private List<Tick> getTicksBeforeTrade(Trade trade, BarSeries series, Strategy strategy)
    {
        var entryOrderIndex = trade.getEntry().getIndex();
        var seriesBeginIndex = series.getBeginIndex();
        if (entryOrderIndex == seriesBeginIndex)
        {
            return List.of();
        }
        var ticks = new ArrayList<Tick>();
        var startIndex = Math.max(0, entryOrderIndex - N_TICKS_BEFORE_TRADE - 1);
        for (int i = startIndex; i < entryOrderIndex; i++)
        {
            var tick = getTickBuilder(i, series, strategy).build();
            ticks.add(tick);
        }
        return ticks;
    }

    private List<Tick> getTicksDuringTrade(Trade trade, BarSeries series, Strategy strategy)
    {
        var ticks = new ArrayList<Tick>();
        var entryOrder = trade.getEntry();
        var exitOrder = trade.getExit();
        var entryOrderIndex = entryOrder.getIndex();
        var exitOrderIndex = exitOrder.getIndex();
        for (int i = entryOrderIndex; i <= exitOrderIndex; i++)
        {
            var tickBuilder = getTickBuilder(i, series, strategy);
            if (i == entryOrderIndex)
            {
                tickBuilder.signal(entryOrder.getType())
                    .mainChartLevels(getMainChartLevels(i, strategy));
            }
            else if (i == exitOrderIndex)
            {
                tickBuilder.signal(exitOrder.getType());
            }
            ticks.add(tickBuilder.build());
        }
        return ticks;
    }

    private List<MainChartLevel> getMainChartLevels(int entryOrderIndex, Strategy strategy)
    {
        return strategy.getMainChartLevelProviders()
                .stream()
                .map(mainChartLevelProvider -> mainChartLevelProvider.getLevel(entryOrderIndex))
                .collect(toList());
    }

    private Tick.TickBuilder getTickBuilder(int index, BarSeries series, Strategy strategy)
    {
        var bar = series.getBar(index);
        var mainChartNumIndicators = getNumberIndicators(index, strategy, ChartType.MAIN);
        var additionalChartNumIndicators = getNumberIndicators(index, strategy, ChartType.ADDITIONAL);
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

    private Map<String, Double> getNumberIndicators(int index, Strategy strategy, ChartType chartType)
    {
        var indicatorNameToValueMap = new LinkedHashMap<String, Double>();
        strategy.getNumberIndicators()
            .stream()
            .filter(indicator -> chartType.equals(indicator.getChartType()))
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
