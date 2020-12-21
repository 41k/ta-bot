package root.application.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.application.application.StrategyExecutionManagersStore;
import root.application.application.repository.ApplicationLevelTradeHistoryItemRepository;
import root.application.application.repository.ExchangeGatewayAccountRepository;
import root.application.application.service.*;
import root.application.domain.history.TradeHistoryItemRepository;
import root.application.domain.strategy.StrategyFactory;
import root.application.domain.strategy.prod.ETHUSD_5m_BB_Strategy1Factory;
import root.application.domain.strategy.prod.ETHUSD_5m_BB_Strategy2Factory;
import root.application.domain.strategy.prod.ETHUSD_5m_DownTrend_Strategy1Factory;
import root.application.domain.strategy.prod.ETHUSD_5m_UpTrend_Strategy1Factory;
import root.application.domain.strategy.qa.MacdStrategy1Factory;
import root.application.domain.strategy.qa.RsiStrategy1Factory;
import root.application.domain.strategy.qa.SmaStrategy5Factory;
import root.application.domain.trading.ExchangeGateway;
import root.framework.service.UserService;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Configuration
public class ApplicationConfiguration
{
    @Bean
    public Map<String, StrategyFactory> strategyFactoriesStore()
    {
        var strategyFactories = List.of(
            // qa
            new MacdStrategy1Factory(),
            new RsiStrategy1Factory(),
            new SmaStrategy5Factory(),
            // prod
            new ETHUSD_5m_DownTrend_Strategy1Factory(),
            new ETHUSD_5m_UpTrend_Strategy1Factory(),
            new ETHUSD_5m_BB_Strategy1Factory(),
            new ETHUSD_5m_BB_Strategy2Factory()
        );
        return strategyFactories.stream().collect(toMap(StrategyFactory::getStrategyId, identity()));
    }

    @Bean
    public StrategyExecutionManagersStore strategyExecutionManagersStore()
    {
        return new StrategyExecutionManagersStore();
    }

    @Bean
    public ExchangeGatewayService exchangeGatewayService(
        // @Qualifier is necessary since Spring sets bean name as key of the map.
        @Qualifier("exchangeGatewaysStore") Map<String, ExchangeGateway> exchangeGatewaysStore,
        ExchangeGatewayAccountService exchangeGatewayAccountService)
    {
        return new ExchangeGatewayService(exchangeGatewaysStore, exchangeGatewayAccountService);
    }

    @Bean
    public ExchangeGatewayAccountService exchangeGatewayAccountService(
        StrategyExecutionManagersStore strategyExecutionManagersStore,
        ExchangeGatewayAccountRepository exchangeGatewayAccountRepository,
        UserService userService)
    {
        return new ExchangeGatewayAccountService(
            strategyExecutionManagersStore, exchangeGatewayAccountRepository, userService);
    }

    @Bean
    public StrategyService strategyService(Map<String, StrategyFactory> strategyFactoriesStore)
    {
        return new StrategyService(strategyFactoriesStore);
    }

    @Bean
    public StrategyExecutionService strategyExecutionService(
        StrategyExecutionManagersStore strategyExecutionManagersStore,
        ExchangeGatewayAccountService exchangeGatewayAccountService,
        ExchangeGatewayService exchangeGatewayService,
        StrategyService strategyService,
        HistoryService historyService,
        TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        return new StrategyExecutionService(
            strategyExecutionManagersStore, exchangeGatewayAccountService, exchangeGatewayService,
            strategyService, historyService, tradeHistoryItemRepository);
    }

    @Bean
    public HistoryService historyService(
        ApplicationLevelTradeHistoryItemRepository applicationLevelTradeHistoryItemRepository, UserService userService)
    {
        return new HistoryService(applicationLevelTradeHistoryItemRepository, userService);
    }
}
