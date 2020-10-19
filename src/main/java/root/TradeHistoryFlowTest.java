package root;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import root.application.application.TradeHistoryItemRepository;
import root.application.application.TradingService;
import root.application.domain.report.TradeHistoryItem;

import java.util.List;
import java.util.concurrent.TimeUnit;

// NOTE: Remove tabot db before launch
@Component
@Slf4j
@RequiredArgsConstructor
public class TradeHistoryFlowTest implements CommandLineRunner
{
    private final TradingService tradingService;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;

    @Override
    public void run(String... args) throws Exception
    {
        TimeUnit.SECONDS.sleep(5);
        tradingService.trade().collectList().block();

        var trades = tradeHistoryItemRepository.findAll().collectList().block();
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
