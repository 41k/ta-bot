package root.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.application.domain.ExchangeGateway;
import root.application.domain.report.BarRepository;
import root.application.domain.strategy.StrategyFactory;
import root.application.domain.strategy.rsi.RsiStrategy1Factory;
import root.application.domain.trading.StrategiesExecutor;
import root.application.infrastructure.exchange.StubExchangeGateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
public class ApplicationConfiguration
{
//    @Bean
//    public Map<String, ExchangeGateway> exchangeGatewaysStore(BarRepository barRepository)
//    {
//        var exchangeGateways = List.of(
//            new StubExchangeGateway(barRepository)
//        );
//        return exchangeGateways.stream().collect(toMap(ExchangeGateway::getExchangeId, identity()));
//    }

    @Bean
    public Map<String, StrategyFactory> strategyFactoriesStore()
    {
        var strategyFactories = List.of(
            new RsiStrategy1Factory()
        );
        return strategyFactories.stream().collect(toMap(StrategyFactory::getStrategyId, identity()));
    }

    @Bean
    public Map<String, StrategiesExecutor> strategyExecutorsStore()
    {
        // NOTE:
        // * Single user env: key = exchangeId
        // * Multiple users env: key = pair(userId, exchangeId)
        return new HashMap<>();
    }
}
