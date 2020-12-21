package root.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import root.application.domain.trading.ExchangeGateway;
import root.application.domain.trading.TradingOperationContext;

import java.util.Map;

import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.API_KEY;
import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.SECRET_KEY;
import static root.application.domain.trading.Symbol.ETH_USD;

//@Component
@Slf4j
public class CurrencyComExchangeGatewayRunner implements CommandLineRunner
{
    @Autowired
    @Qualifier("exchangeGatewaysStore")
    private Map<String, ExchangeGateway> exchangeGatewaysStore;

    @Override
    public void run(String... args)
    {
        var exchangeGateway = exchangeGatewaysStore.get("58d0dcdc-0103-4eff-bf4e-3a6e678e6fa3");
        var tradingOperationContext = TradingOperationContext.builder()
            .symbol(ETH_USD)
            .amount(0.03d)
            .price(1670d)
            .exchangeGatewayAccountConfiguration(Map.of(
                API_KEY, "aaapppiiikkeeyy",
                SECRET_KEY, "sseeccrreettkkeeyy"
            ))
            .build();
        var tradingOperationResult = exchangeGateway.buy(tradingOperationContext);
        log.info("### " + tradingOperationResult);
    }
}
