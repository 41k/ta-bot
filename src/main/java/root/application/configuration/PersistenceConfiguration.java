package root.application.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import root.application.application.repository.ApplicationLevelTradeHistoryItemRepository;
import root.application.application.repository.ExchangeGatewayAccountRepository;
import root.application.domain.history.BarRepository;
import root.application.domain.history.TradeHistoryItemRepository;
import root.application.infrastructure.persistence.bar.BarDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.bar.BarRepositoryImpl;
import root.application.infrastructure.persistence.exchange_gateway_account.ExchangeGatewayAccountDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.exchange_gateway_account.ExchangeGatewayAccountMapper;
import root.application.infrastructure.persistence.exchange_gateway_account.ExchangeGatewayAccountRepositoryImpl;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemMapper;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemRepositoryImpl;

@Configuration
@EnableR2dbcRepositories("root.application.infrastructure.persistence")
@EnableTransactionManagement
public class PersistenceConfiguration
{
    @Bean
    public ExchangeGatewayAccountRepository exchangeGatewayAccountRepository(
        ExchangeGatewayAccountMapper exchangeGatewayAccountMapper,
        ExchangeGatewayAccountDbEntryR2dbcRepository exchangeGatewayAccountDbEntryR2dbcRepository)
    {
        return new ExchangeGatewayAccountRepositoryImpl(
            exchangeGatewayAccountMapper, exchangeGatewayAccountDbEntryR2dbcRepository);
    }

    @Bean
    public TradeHistoryItemRepository tradeHistoryItemRepository(
        TradeHistoryItemMapper tradeHistoryItemMapper,
        TradeHistoryItemDbEntryR2dbcRepository tradeHistoryItemDbEntryR2DbcRepository)
    {
        return new TradeHistoryItemRepositoryImpl(tradeHistoryItemMapper, tradeHistoryItemDbEntryR2DbcRepository);
    }

    @Bean
    public ApplicationLevelTradeHistoryItemRepository applicationLevelTradeHistoryItemRepository(
        TradeHistoryItemMapper tradeHistoryItemMapper,
        TradeHistoryItemDbEntryR2dbcRepository tradeHistoryItemDbEntryR2DbcRepository)
    {
        return new TradeHistoryItemRepositoryImpl(tradeHistoryItemMapper, tradeHistoryItemDbEntryR2DbcRepository);
    }

    @Bean
    public BarRepository barRepository(BarDbEntryR2dbcRepository barDbEntryR2dbcRepository)
    {
        return new BarRepositoryImpl(barDbEntryR2dbcRepository);
    }

    @Bean
    public TradeHistoryItemMapper tradeHistoryItemMapper(ObjectMapper objectMapper)
    {
        return new TradeHistoryItemMapper(objectMapper);
    }

    @Bean
    public ExchangeGatewayAccountMapper exchangeGatewayAccountMapper(ObjectMapper objectMapper, TextEncryptor encryptor)
    {
        return new ExchangeGatewayAccountMapper(objectMapper, encryptor);
    }

    @Bean
    public TextEncryptor encryptor(@Value("${encryptionPassword}") String encryptionPassword)
    {
        var encryptor = new BasicTextEncryptor();
        encryptor.setPassword(encryptionPassword);
        return encryptor;
    }
}
