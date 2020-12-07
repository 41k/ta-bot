package root.application;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import root.application.application.model.HistoryFilter;
import root.application.application.service.HistoryService;
import root.application.domain.history.TradeHistoryItem;
import root.application.domain.trading.StrategyExecutionContext;
import root.application.domain.trading.StrategyExecutionsManager;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static root.application.domain.trading.Interval.ONE_MINUTE;

// NOTE: Remove tabot db before launch
//@Component
@Slf4j
@RequiredArgsConstructor
public class StrategiesExecutionRunner implements CommandLineRunner
{
    private static final String EXCHANGE_GATEWAY_ID = "d1609961-d114-419d-8146-20f4e90dde66";
    private static final String EXCHANGE_GATEWAY_NAME = "StubExchange";
    private final static List<String> STRATEGY_IDS = List.of(
        "8795316a-c6ef-4cab-bc84-6e508701f95f",
        "0e18f4e3-e645-46c9-9565-3344f726f1e1",
        "ed2fb8fb-d1b4-4bf2-b9c5-68074931e2a8"
    );
    private final static StrategyExecutionContext.StrategyExecutionContextBuilder STRATEGY_EXECUTION_CONTEXT_BUILDER =
        StrategyExecutionContext.builder()
            .symbol("BTC/USD")
            .amount(0.01d)
            .interval(ONE_MINUTE);
    private final static HistoryFilter.HistoryFilterBuilder HISTORY_FILTER_BUILDER =
        HistoryFilter.builder()
            .fromTimestamp(1597901099999L)
            .toTimestamp(1598009399999L)
            .exchangeGateway(EXCHANGE_GATEWAY_NAME);

    private final Map<String, StrategyExecutionsManager> strategyExecutionManagersStore;
    private final HistoryService historyService;

    @Override
    public void run(String... args)
    {
        runStrategies();
        printReport();
    }

    private void runStrategies()
    {
        STRATEGY_IDS.forEach(strategyId -> {
            var strategyExecutionContext = STRATEGY_EXECUTION_CONTEXT_BUILDER.strategyId(strategyId).build();
            getStrategyExecutionsManager().runStrategyExecution(strategyExecutionContext);
        });
    }

    @SneakyThrows
    private void printReport()
    {
        TimeUnit.SECONDS.sleep(120);

        getStrategyExecutionsManager().getStrategyExecutions().forEach(strategyExecution -> {
            var filter = HISTORY_FILTER_BUILDER.strategyExecutionId(strategyExecution.getId()).build();
            var pageable = PageRequest.of(0, 10000);
            var trades = historyService.searchForTrades(filter, pageable);
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Strategy: " + strategyExecution.getStrategyName());
            System.out.println("Total profit: " + calculateTotalProfit(trades));
            System.out.println("N trades: " + trades.size());
            var nProfitableTrades = calculateNumberOfProfitableTrades(trades);
            System.out.println("N profitable trades: " + nProfitableTrades);
            var nUnprofitableTrades = calculateNumberOfUnprofitableTrades(trades);
            System.out.println("N unprofitable trades: " + nUnprofitableTrades);
            var riskRewardRatio = nUnprofitableTrades / (double) nProfitableTrades;
            System.out.println("Risk/Reward ratio: " + riskRewardRatio);
            System.out.println("----------------------------------------");
        });

        System.out.println();
        System.out.println("TOTAL TRADES: " + historyService.getAllTrades().size());
        System.out.println();
    }

    private StrategyExecutionsManager getStrategyExecutionsManager()
    {
        return ofNullable(strategyExecutionManagersStore.get(EXCHANGE_GATEWAY_ID)).orElseThrow(() -> new NoSuchElementException(
            format("Strategy executions manager for exchange gateway [%s] is not found.", EXCHANGE_GATEWAY_ID)));
    }

    private Double calculateTotalProfit(List<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getTotalProfit)
            .sum();
    }

    private long calculateNumberOfProfitableTrades(List<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getTotalProfit)
            .filter(profit -> profit > 0)
            .count();
    }

    private long calculateNumberOfUnprofitableTrades(List<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getTotalProfit)
            .filter(profit -> profit <= 0)
            .count();
    }
}
