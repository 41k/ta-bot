package root.application.infrastructure.exchange_gateway;

import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;
import root.application.domain.history.BarRepository;
import root.application.domain.trading.ExchangeGateway;
import root.application.domain.trading.Interval;
import root.application.domain.trading.TradingOperationContext;
import root.application.domain.trading.TradingOperationResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static root.application.domain.trading.Interval.ONE_MINUTE;

@Slf4j
public class StubExchangeGateway implements ExchangeGateway
{
    private static final String EXCHANGE_GATEWAY_ID = "d1609961-d114-419d-8146-20f4e90dde66";
    private static final String EXCHANGE_GATEWAY_NAME = "StubExchange";
    private static final Set<String> SUPPORTED_SYMBOLS = Set.of("BTC/USD");

    private final BarRepository barRepository;
    private final CsvBarProvider barProvider;
    private final Map<Interval, Runnable> intervalToBarAcceptorMap;
    private final Map<Interval, List<Consumer<Bar>>> intervalToBarConsumersMap;

    public StubExchangeGateway(BarRepository barRepository)
    {
        this.barRepository = barRepository;
        this.barProvider = new CsvBarProvider();
        this.intervalToBarAcceptorMap = Map.of(
            ONE_MINUTE, this::acceptOneMinuteIntervalBars
        );
        this.intervalToBarConsumersMap = intervalToBarAcceptorMap.keySet().stream().collect(toMap(
            Function.identity(),
            interval -> new ArrayList<>()
        ));
        //run();
    }

    @Override
    public String getId()
    {
        return EXCHANGE_GATEWAY_ID;
    }

    @Override
    public String getName()
    {
        return EXCHANGE_GATEWAY_NAME;
    }

    @Override
    public Set<Interval> getSupportedIntervals()
    {
        return intervalToBarAcceptorMap.keySet();
    }

    @Override
    public Set<String> getSupportedSymbols()
    {
        return SUPPORTED_SYMBOLS;
    }

    @Override
    public void subscribeToBarStream(Interval interval, Consumer<Bar> barConsumer)
    {
        var barConsumers = ofNullable(intervalToBarConsumersMap.get(interval))
            .orElseThrow(() -> new IllegalArgumentException(
                format("Interval [%s] is not supported by exchange gateway [%s].", interval, EXCHANGE_GATEWAY_ID)));
        barConsumers.add(barConsumer);
    }

    @Override
    public TradingOperationResult buy(TradingOperationContext context)
    {
        return new TradingOperationResult(context.getAmount(), context.getPrice());
    }

    @Override
    public TradingOperationResult sell(TradingOperationContext context)
    {
        return new TradingOperationResult(context.getAmount(), context.getPrice());
    }

    private void run()
    {
        intervalToBarAcceptorMap.values().forEach(Runnable::run);
    }

    private void acceptOneMinuteIntervalBars()
    {
        var interval = ONE_MINUTE;
        Flux.fromIterable(barProvider.getBars())
            .delaySubscription(Duration.ofSeconds(60))
            //.delayElements(Duration.ofSeconds(1))
            .map(bar -> barRepository.save(bar, EXCHANGE_GATEWAY_NAME, interval))
            .doOnNext(bar -> acceptBar(interval, bar))
            .subscribe();
    }

    private void acceptBar(Interval interval, Bar bar)
    {
        intervalToBarConsumersMap.get(interval).forEach(barConsumer -> barConsumer.accept(bar));
    }
}
