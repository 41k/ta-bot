package root.application;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import root.application.application.QueryProcessor;
import root.application.domain.ExchangeGateway;
import root.application.domain.report.TradeHistoryItem;
import root.application.domain.report.TradeHistoryItemRepository;
import root.application.domain.strategy.StrategyFactory;
import root.application.domain.trading.StrategiesExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// NOTE: Remove tabot db before launch
@Component
@Slf4j
@RequiredArgsConstructor
public class StrategiesExecutionRunner implements CommandLineRunner
{
    private final Map<String, ExchangeGateway> exchangeGatewaysStore;
    private final Map<String, StrategyFactory> strategyFactoriesStore;
    private final Map<String, StrategiesExecutor> strategyExecutorsStore;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;
    private final QueryProcessor queryProcessor;

    @Override
    public void run(String... args)
    {
        runStrategyExecution();
        printReport();
    }

    private void runStrategyExecution()
    {
        var exchangeId = "stub-exchange";
        var exchangeGateway = exchangeGatewaysStore.get(exchangeId);
        var strategiesExecutor = new StrategiesExecutor(strategyFactoriesStore, exchangeGateway, tradeHistoryItemRepository);
        strategyExecutorsStore.put(exchangeId, strategiesExecutor);
        var strategyId = "RSI#1";
        strategiesExecutor.activateStrategy(strategyId, 1d);
    }

    @SneakyThrows
    private void printReport()
    {
        TimeUnit.SECONDS.sleep(120);

        var trades = queryProcessor.findAllTrades().collectList().block();
        System.out.println("----------------------------------------");
        System.out.println("Total profit: " + calculateTotalProfit(trades));
        System.out.println("N trades: " + trades.size());
        var nProfitableTrades = calculateNumberOfProfitableTrades(trades);
        System.out.println("N profitable trades: " + nProfitableTrades);
        var nUnprofitableTrades = calculateNumberOfUnprofitableTrades(trades);
        System.out.println("N unprofitable trades: " + nUnprofitableTrades);
        var riskRewardRatio = nUnprofitableTrades / (double) nProfitableTrades;
        System.out.println("Risk/Reward ratio: " + riskRewardRatio);
        System.out.println("----------------------------------------");
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
