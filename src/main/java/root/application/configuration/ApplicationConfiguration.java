package root.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.application.application.ApplicationLevelTradeHistoryItemRepository;
import root.application.application.ExchangeGatewayService;
import root.application.application.HistoryService;
import root.application.application.StrategyService;
import root.application.domain.ExchangeGateway;
import root.application.domain.report.BarRepository;
import root.application.domain.report.TradeHistoryItemRepository;
import root.application.domain.strategy.StrategyFactory;
import root.application.domain.strategy.macd.MacdStrategy1Factory;
import root.application.domain.strategy.rsi.RsiStrategy1Factory;
import root.application.domain.strategy.sma.SmaStrategy5Factory;
import root.application.domain.trading.StrategiesExecutor;
import root.application.infrastructure.exchange_gateway.StubExchangeGateway;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
public class ApplicationConfiguration
{
    @Bean
    public Map<String, ExchangeGateway> exchangeGatewaysStore(BarRepository barRepository)
    {
        var exchangeGateways = List.of(
            new StubExchangeGateway(barRepository)
        );
        return exchangeGateways.stream().collect(toMap(ExchangeGateway::getId, identity()));
    }

    @Bean
    public Map<String, StrategyFactory> strategyFactoriesStore()
    {
        var strategyFactories = List.of(
            new MacdStrategy1Factory(),
            new RsiStrategy1Factory(),
            new SmaStrategy5Factory()
        );
        return strategyFactories.stream().collect(toMap(StrategyFactory::getStrategyId, identity()));
    }

    @Bean
    public Map<String, StrategiesExecutor> strategyExecutorsStore(Map<String, ExchangeGateway> exchangeGatewaysStore,
                                                                  Map<String, StrategyFactory> strategyFactoriesStore,
                                                                  TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        // NOTE:
        // * Single user env: key = exchangeGatewayId
        // * Multiple users env: key = pair(userId, exchangeGatewayId)
        return exchangeGatewaysStore.entrySet().stream().collect(toMap(
            Map.Entry::getKey,
            entry -> {
                var exchangeGateway = entry.getValue();
                return new StrategiesExecutor(strategyFactoriesStore, exchangeGateway, tradeHistoryItemRepository);
            }));
    }

    @Bean
    public ExchangeGatewayService exchangeGatewayService(Map<String, ExchangeGateway> exchangeGatewaysStore)
    {
        return new ExchangeGatewayService(exchangeGatewaysStore);
    }

    @Bean
    public StrategyService strategyService(Map<String, StrategiesExecutor> strategyExecutorsStore)
    {
        return new StrategyService(strategyExecutorsStore);
    }

    @Bean
    public HistoryService historyService(ApplicationLevelTradeHistoryItemRepository applicationLevelTradeHistoryItemRepository)
    {
        return new HistoryService(applicationLevelTradeHistoryItemRepository);
    }
}
