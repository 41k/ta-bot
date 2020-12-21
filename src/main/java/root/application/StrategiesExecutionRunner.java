package root.application;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import root.application.application.model.HistoryFilter;
import root.application.application.model.command.CreateExchangeGatewayAccountCommand;
import root.application.application.model.command.RunStrategyExecutionCommand;
import root.application.application.service.ExchangeGatewayAccountService;
import root.application.application.service.HistoryService;
import root.application.application.service.StrategyExecutionService;
import root.application.domain.history.TradeHistoryItem;
import root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.API_KEY;
import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.SECRET_KEY;
import static root.application.domain.trading.Interval.ONE_MINUTE;
import static root.application.domain.trading.Symbol.BTC_USD;

// Note: Remove tabot db before launch
//@Component
@Slf4j
@RequiredArgsConstructor
public class StrategiesExecutionRunner implements CommandLineRunner
{
    private static final String EXCHANGE_GATEWAY_ID = "d1609961-d114-419d-8146-20f4e90dde66";
    private static final String EXCHANGE_GATEWAY_NAME = "StubExchange";
    private static final Map<ExchangeGatewayAccountConfigurationProperty, String> EXCHANGE_GATEWAY_ACCOUNT_CONFIGURATION = Map.of(
        API_KEY, "20f4e90dde6620f4e90dde6620f4e90dde66",
        SECRET_KEY, "d1609961d1609961d1609961d1609961"
    );
    private final static List<String> STRATEGY_IDS = List.of(
        "8795316a-c6ef-4cab-bc84-6e508701f95f",
        "0e18f4e3-e645-46c9-9565-3344f726f1e1",
        "ed2fb8fb-d1b4-4bf2-b9c5-68074931e2a8"
    );
    private final static RunStrategyExecutionCommand.RunStrategyExecutionCommandBuilder RUN_STRATEGY_EXECUTION_COMMAND_BUILDER =
        RunStrategyExecutionCommand.builder()
            .exchangeGatewayId(EXCHANGE_GATEWAY_ID)
            .symbol(BTC_USD)
            .amount(1d)
            .interval(ONE_MINUTE);
    private final static HistoryFilter.HistoryFilterBuilder HISTORY_FILTER_BUILDER =
        HistoryFilter.builder()
            .fromTimestamp(1597901099999L)
            .toTimestamp(1598009399999L)
            .exchangeGateway(EXCHANGE_GATEWAY_NAME);

    private final ExchangeGatewayAccountService exchangeGatewayAccountService;
    private final StrategyExecutionService strategyExecutionService;
    private final HistoryService historyService;

    @Override
    @SneakyThrows
    public void run(String... args)
    {
        TimeUnit.SECONDS.sleep(10);

        var exchangeGatewayAccountId = createExchangeGatewayAccount();
        runStrategies(exchangeGatewayAccountId);
        printReport(exchangeGatewayAccountId);
    }

    private void runStrategies(Long exchangeGatewayAccountId)
    {
        STRATEGY_IDS.forEach(strategyId -> {
            var runStrategyExecutionCommand = RUN_STRATEGY_EXECUTION_COMMAND_BUILDER
                .exchangeGatewayAccountId(exchangeGatewayAccountId)
                .strategyId(strategyId)
                .build();
            strategyExecutionService.execute(runStrategyExecutionCommand);
        });
    }

    @SneakyThrows
    private void printReport(Long exchangeGatewayAccountId)
    {
        TimeUnit.SECONDS.sleep(40);

        var strategyExecutions = strategyExecutionService.getStrategyExecutions(EXCHANGE_GATEWAY_ID, exchangeGatewayAccountId);
        strategyExecutions.forEach(strategyExecution -> {
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

    private Long createExchangeGatewayAccount()
    {
        var createExchangeGatewayAccountCommand = new CreateExchangeGatewayAccountCommand(EXCHANGE_GATEWAY_ID, EXCHANGE_GATEWAY_ACCOUNT_CONFIGURATION);
        exchangeGatewayAccountService.execute(createExchangeGatewayAccountCommand);
        var exchangeGatewayAccount = exchangeGatewayAccountService.getAccount(EXCHANGE_GATEWAY_ID);
        return exchangeGatewayAccount.getId();
    }

    private Double calculateTotalProfit(Collection<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getTotalProfit)
            .sum();
    }

    private long calculateNumberOfProfitableTrades(Collection<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getTotalProfit)
            .filter(profit -> profit > 0)
            .count();
    }

    private long calculateNumberOfUnprofitableTrades(Collection<TradeHistoryItem> trades)
    {
        return trades.stream()
            .mapToDouble(TradeHistoryItem::getTotalProfit)
            .filter(profit -> profit <= 0)
            .count();
    }
}
