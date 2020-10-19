package root.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.application.application.BarProvider;
import root.application.application.BarRepository;
import root.application.application.TradeHistoryItemRepository;
import root.application.application.TradingService;
import root.application.domain.ExchangeGateway;
import root.application.infrastructure.exchange.CsvBarProvider;
import root.application.infrastructure.exchange.StubExchangeGateway;

@Configuration
public class ApplicationConfiguration
{
    @Bean
    public BarProvider barProvider()
    {
        return new CsvBarProvider();
    }

    @Bean
    public ExchangeGateway exchangeGateway()
    {
        return new StubExchangeGateway();
    }

    @Bean
    public TradingService tradingService(BarProvider barProvider,
                                         ExchangeGateway exchangeGateway,
                                         BarRepository barRepository,
                                         TradeHistoryItemRepository tradeHistoryItemRepository)
    {
        return new TradingService(barProvider, exchangeGateway, barRepository, tradeHistoryItemRepository);
    }
}
