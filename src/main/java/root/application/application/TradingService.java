package root.application.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import org.ta4j.core.Trade;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import root.application.domain.ExchangeGateway;
import root.application.domain.report.TradeHistoryItem;
import root.application.domain.report.TradeHistoryItemBuilder;
import root.application.domain.strategy.StrategyFactory;
import root.application.domain.strategy.rsi.RsiStrategy1Factory;
import root.application.domain.strategyexecution.StrategyExecution;

@RequiredArgsConstructor
public class TradingService
{
    private final BarProvider barProvider;
    private final ExchangeGateway exchangeGateway;
    private final BarRepository barRepository;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;

    public Flux<Trade> trade()
    {
        StrategyFactory strategyFactory = new RsiStrategy1Factory("RSI#1", Exchange.BINANCE.name());
        Num amountToBuy = PrecisionNum.valueOf(1);
        StrategyExecution strategyExecution = new StrategyExecution(strategyFactory, exchangeGateway, amountToBuy);
        return Flux.fromIterable(barProvider.getBars())
            .map(this::recordBar)
            .concatMap(bar -> processBar(bar, strategyExecution))
            .map(trade -> recordTrade(trade, strategyFactory));
    }

    private Bar recordBar(Bar bar)
    {
        barRepository.save(bar, Exchange.BINANCE.name()).subscribe();
        return bar;
    }

    private Mono<Trade> processBar(Bar bar, StrategyExecution strategyExecution)
    {
        return strategyExecution.processBar(bar).map(Mono::just).orElseGet(Mono::empty);
    }

    private Trade recordTrade(Trade trade, StrategyFactory strategyFactory)
    {
        TradeHistoryItem tradeHistoryItem = new TradeHistoryItemBuilder().build(trade, strategyFactory);
        tradeHistoryItemRepository.save(tradeHistoryItem).subscribe();
        return trade;
    }
}
