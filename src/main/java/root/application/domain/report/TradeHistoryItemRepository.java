package root.application.domain.report;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import root.application.domain.report.TradeHistoryItem;

public interface TradeHistoryItemRepository
{
    TradeHistoryItem save(TradeHistoryItem tradeHistoryItem);
}
