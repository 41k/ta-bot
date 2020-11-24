package root.application.infrastructure.persistence.trade_history_item;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import root.application.application.ApplicationLevelTradeHistoryItemRepository;
import root.application.domain.report.TradeHistoryItem;
import root.application.domain.report.TradeHistoryItemRepository;

import java.util.List;

@RequiredArgsConstructor
public class TradeHistoryItemRepositoryImpl implements TradeHistoryItemRepository, ApplicationLevelTradeHistoryItemRepository
{
    private final TradeHistoryItemMapper mapper;
    private final TradeHistoryItemDbEntryR2dbcRepository r2dbcRepository;

    @Override
    public TradeHistoryItem save(TradeHistoryItem tradeHistoryItem)
    {
        Mono.just(tradeHistoryItem)
            .map(mapper::toDbEntry)
            .flatMap(r2dbcRepository::save)
            .subscribe();
        return tradeHistoryItem;
    }

    public List<TradeHistoryItem> findTrades()
    {
        return r2dbcRepository.findAll()
            .map(mapper::toDomainObject)
            .collectList()
            .block();
    }

    public List<TradeHistoryItem> findTrades(Long fromTimestamp, Long toTimestamp)
    {
        return r2dbcRepository.findAllInRange(fromTimestamp, toTimestamp)
            .map(mapper::toDomainObject)
            .collectList()
            .block();
    }

    public List<TradeHistoryItem> findTrades(Long fromTimestamp, Long toTimestamp, String exchangeGatewayId)
    {
        return r2dbcRepository.findAllInRangeByExchangeGatewayId(fromTimestamp, toTimestamp, exchangeGatewayId)
            .map(mapper::toDomainObject)
            .collectList()
            .block();
    }

    public List<TradeHistoryItem> findTrades(Long fromTimestamp, Long toTimestamp, String exchangeGatewayId, String strategyId)
    {
        return r2dbcRepository.findAllInRangeByExchangeGatewayIdAndStrategyId(fromTimestamp, toTimestamp, exchangeGatewayId, strategyId)
            .map(mapper::toDomainObject)
            .collectList()
            .block();
    }
}
