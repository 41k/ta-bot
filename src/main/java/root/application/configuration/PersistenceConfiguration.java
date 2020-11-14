package root.application.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import root.application.application.ApplicationLevelTradeHistoryItemRepository;
import root.application.domain.report.BarRepository;
import root.application.domain.report.TradeHistoryItemRepository;
import root.application.infrastructure.persistence.bar.BarDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.bar.BarRepositoryImpl;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemDbEntryR2dbcRepository;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemMapper;
import root.application.infrastructure.persistence.trade_history_item.TradeHistoryItemRepositoryImpl;

@Configuration
@EnableR2dbcRepositories("root.application.infrastructure.persistence")
@EnableTransactionManagement
public class PersistenceConfiguration
{
    @Bean
    public TradeHistoryItemMapper tradeHistoryItemMapper(ObjectMapper objectMapper)
    {
        return new TradeHistoryItemMapper(objectMapper);
    }

    @Bean
    public TradeHistoryItemRepository tradeHistoryItemRepository(TradeHistoryItemMapper tradeHistoryItemMapper, TradeHistoryItemDbEntryR2dbcRepository tradeHistoryItemDbEntryR2DbcRepository)
    {
        return new TradeHistoryItemRepositoryImpl(tradeHistoryItemMapper, tradeHistoryItemDbEntryR2DbcRepository);
    }

    @Bean
    public ApplicationLevelTradeHistoryItemRepository applicationLevelTradeHistoryItemRepository(TradeHistoryItemMapper tradeHistoryItemMapper, TradeHistoryItemDbEntryR2dbcRepository tradeHistoryItemDbEntryR2DbcRepository)
    {
        return new TradeHistoryItemRepositoryImpl(tradeHistoryItemMapper, tradeHistoryItemDbEntryR2DbcRepository);
    }

    @Bean
    public BarRepository barRepository(BarDbEntryR2dbcRepository barDbEntryR2dbcRepository)
    {
        return new BarRepositoryImpl(barDbEntryR2dbcRepository);
    }
}
