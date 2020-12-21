package root.application.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import root.application.domain.history.BarRepository;
import root.application.domain.trading.ExchangeGateway;
import root.application.infrastructure.exchange_gateway.currency_com.CurrencyComExchangeGateway;
import root.application.infrastructure.exchange_gateway.stub.StubExchangeGateway;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
@EnableConfigurationProperties(ExchangeGatewaysConfigurationProperties.class)
public class ExchangeGatewaysConfiguration
{
    private static final String CURRENCY_COM_CONFIGURATION_KEY = "currency-com";

    @Bean
    public Map<String, ExchangeGateway> exchangeGatewaysStore(List<ExchangeGateway> exchangeGateways)
    {
        return exchangeGateways.stream().collect(toMap(ExchangeGateway::getId, identity()));
    }

    @Bean
    public ExchangeGateway currencyComExchangeGateway(ExchangeGatewaysConfigurationProperties properties,
                                                      WebSocketClient webSocketClient,
                                                      ObjectMapper objectMapper,
                                                      BarRepository barRepository)
    {
        var configuration = properties.getExchangeGatewayConfiguration(CURRENCY_COM_CONFIGURATION_KEY);
        return new CurrencyComExchangeGateway(configuration, webSocketClient, objectMapper, barRepository);
    }

    //@Bean
    public ExchangeGateway stubExchangeGateway(BarRepository barRepository)
    {
        return new StubExchangeGateway(barRepository);
    }

    @Bean
    public WebSocketClient webSocketClient()
    {
        var client = new ReactorNettyWebSocketClient();
        client.setMaxFramePayloadLength(2621440);
        return client;
    }
}
