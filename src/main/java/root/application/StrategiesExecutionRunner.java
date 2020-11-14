package root.application;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import root.application.application.HistoryService;
import root.application.domain.report.TradeHistoryItem;
import root.application.domain.trading.StrategiesExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;

// NOTE: Remove tabot db before launch
//@Component
@Slf4j
@RequiredArgsConstructor
public class StrategiesExecutionRunner implements CommandLineRunner
{
    private final static String EXCHANGE_ID = "stub-exchange";
    private final static List<String> STRATEGY_IDS = List.of("MACD#1", "RSI#1", "SMA#5");
    private final static double AMOUNT_TO_BUY = 1d;
    private final static long FROM_TIMESTAMP = 1597901099999L;
    private final static long TO_TIMESTAMP = 1598009399999L;

    private final Map<String, StrategiesExecutor> strategyExecutorsStore;
    private final HistoryService historyService;

    @Override
    public void run(String... args)
    {
        runStrategies();
        printReport();
    }

    private void runStrategies()
    {
        ofNullable(strategyExecutorsStore.get(EXCHANGE_ID)).ifPresent(strategiesExecutor ->
            STRATEGY_IDS.forEach(strategyId -> strategiesExecutor.activateStrategy(strategyId, AMOUNT_TO_BUY)));
    }

    @SneakyThrows
    private void printReport()
    {
        TimeUnit.SECONDS.sleep(120);

        STRATEGY_IDS.forEach(strategyId -> {
            var trades = historyService.searchForTrades(FROM_TIMESTAMP, TO_TIMESTAMP, EXCHANGE_ID, strategyId);
            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Strategy Id: " + strategyId);
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

    private Double calculateTotalProfit(List<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getProfit)
            .sum();
    }

    private long calculateNumberOfProfitableTrades(List<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getProfit)
            .filter(profit -> profit > 0)
            .count();
    }

    private long calculateNumberOfUnprofitableTrades(List<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getProfit)
            .filter(profit -> profit <= 0)
            .count();
    }
}
