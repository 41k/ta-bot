package root.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.application.application.repository.ApplicationLevelTradeHistoryItemRepository;
import root.application.application.service.ExchangeGatewayService;
import root.application.application.service.HistoryService;
import root.application.application.service.StrategyExecutionService;
import root.application.application.service.StrategyService;
import root.application.domain.history.BarRepository;
import root.application.domain.history.TradeHistoryItemRepository;
import root.application.domain.strategy.StrategyFactory;
import root.application.domain.strategy.macd.MacdStrategy1Factory;
import root.application.domain.strategy.rsi.RsiStrategy1Factory;
import root.application.domain.strategy.sma.SmaStrategy5Factory;
import root.application.domain.trading.ExchangeGateway;
import root.application.domain.trading.StrategyExecutionsManager;
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
    public Map<String, StrategyExecutionsManager> strategyExecutionManagersStore(
        Map<String, ExchangeGateway> exchangeGatewaysStore, Map<String, StrategyFactory> strategyFactoriesStore, TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        // NOTE:
        // * Single user env: key = exchangeGatewayId
        // * Multiple users env: key = pair(userId, exchangeGatewayId)
        return exchangeGatewaysStore.entrySet().stream().collect(toMap(
            Map.Entry::getKey,
            entry -> {
                var exchangeGateway = entry.getValue();
                return new StrategyExecutionsManager(strategyFactoriesStore, exchangeGateway, tradeHistoryItemRepository);
            }));
    }

    @Bean
    public ExchangeGatewayService exchangeGatewayService(Map<String, ExchangeGateway> exchangeGatewaysStore)
    {
        return new ExchangeGatewayService(exchangeGatewaysStore);
    }

    @Bean
    public StrategyService strategyService(Map<String, StrategyFactory> strategyFactoriesStore)
    {
        return new StrategyService(strategyFactoriesStore);
    }

    @Bean
    public StrategyExecutionService strategyExecutionService(Map<String, StrategyExecutionsManager> strategyExecutionManagersStore,
                                                             ApplicationLevelTradeHistoryItemRepository applicationLevelTradeHistoryItemRepository)
    {
        return new StrategyExecutionService(strategyExecutionManagersStore, applicationLevelTradeHistoryItemRepository);
    }

    @Bean
    public HistoryService historyService(ApplicationLevelTradeHistoryItemRepository applicationLevelTradeHistoryItemRepository)
    {
        return new HistoryService(applicationLevelTradeHistoryItemRepository);
    }
}
