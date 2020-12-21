package root.application.infrastructure.exchange_gateway.stub;

import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;
import root.application.domain.history.BarRepository;
import root.application.domain.trading.*;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static root.application.domain.trading.ExchangeGatewayAccountConfigurationProperty.API_KEY;
import static root.application.domain.trading.Interval.ONE_MINUTE;
import static root.application.domain.trading.Symbol.BTC_USD;

@Slf4j
public class StubExchangeGateway implements ExchangeGateway
{
    private static final String EXCHANGE_GATEWAY_ID = "d1609961-d114-419d-8146-20f4e90dde66";
    private static final String EXCHANGE_GATEWAY_NAME = "StubExchange";

    private static final double TRANSACTION_FEE = 0d;

    private static final Set<Symbol> SUPPORTED_SYMBOLS = Set.of(BTC_USD);

    private final BarRepository barRepository;
    private final CsvBarProvider barProvider;
    private final Map<Interval, Runnable> intervalToBarAcceptorMap;
    private final Map<BarStreamSubscription, Map<String, Consumer<Bar>>> barStreamSubscribersStore;

    public StubExchangeGateway(BarRepository barRepository)
    {
        this.barRepository = barRepository;
        this.barProvider = new CsvBarProvider();
        this.intervalToBarAcceptorMap = Map.of(
            ONE_MINUTE, this::acceptOneMinuteIntervalBars
        );
        this.barStreamSubscribersStore = buildBarStreamSubscribersStore();
        run();
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
    public double getTransactionFee()
    {
        return TRANSACTION_FEE;
    }

    @Override
    public Collection<Symbol> getSupportedSymbols()
    {
        return SUPPORTED_SYMBOLS;
    }

    @Override
    public Collection<Interval> getSupportedIntervals()
    {
        return intervalToBarAcceptorMap.keySet();
    }

    @Override
    public Collection<ExchangeGatewayAccountConfigurationProperty> getSupportedAccountConfigurationProperties()
    {
        return Set.of(API_KEY);
    }

    @Override
    public void subscribeToBarStream(BarStreamSubscriptionContext subscriptionContext)
    {
        var subscribers = getSubscribers(subscriptionContext);
        var subscriberId = subscriptionContext.getBarStreamSubscriberId();
        var subscriber = subscriptionContext.getBarStreamSubscriber();
        subscribers.put(subscriberId, subscriber);
    }

    @Override
    public void unsubscribeFromBarStream(BarStreamSubscriptionContext subscriptionContext)
    {
        var subscribers = getSubscribers(subscriptionContext);
        var subscriberId = subscriptionContext.getBarStreamSubscriberId();
        subscribers.remove(subscriberId);
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

    private Map<BarStreamSubscription, Map<String, Consumer<Bar>>> buildBarStreamSubscribersStore()
    {
        return intervalToBarAcceptorMap.keySet().stream()
            .map(interval ->
                SUPPORTED_SYMBOLS.stream()
                    .map(symbol -> new BarStreamSubscription(symbol, interval))
                    .collect(toSet()))
            .flatMap(Collection::stream)
            .collect(toMap(
                Function.identity(),
                subscription -> new HashMap<>()
            ));
    }

    private void run()
    {
        intervalToBarAcceptorMap.values().forEach(Runnable::run);
    }

    private void acceptOneMinuteIntervalBars()
    {
        Flux.fromIterable(barProvider.getBars())
            .delaySubscription(Duration.ofSeconds(30))
            //.delayElements(Duration.ofSeconds(1))
            .map(this::saveBar)
            .map(bar -> acceptBar(BTC_USD, ONE_MINUTE, bar))
            .subscribe();
    }

    private Bar saveBar(Bar bar)
    {
        barRepository.save(bar, EXCHANGE_GATEWAY_NAME, BTC_USD, ONE_MINUTE);
        return bar;
    }

    private Bar acceptBar(Symbol symbol, Interval interval, Bar bar)
    {
        var subscription = new BarStreamSubscription(symbol, interval);
        ofNullable(barStreamSubscribersStore.get(subscription)).ifPresentOrElse(
            subscribers -> subscribers.values().forEach(subscriber -> subscriber.accept(bar)),
            () -> log.warn("Bar {} has been skipped because {} is not supported by exchange gateway [{}].", bar, subscription, EXCHANGE_GATEWAY_NAME)
        );
        return bar;
    }

    private Map<String, Consumer<Bar>> getSubscribers(BarStreamSubscriptionContext subscriptionContext)
    {
        var symbol = subscriptionContext.getSymbol();
        var interval = subscriptionContext.getInterval();
        var subscription = new BarStreamSubscription(symbol, interval);
        return ofNullable(barStreamSubscribersStore.get(subscription))
            .orElseThrow(() -> new IllegalArgumentException(format(
                "Exchange gateway [%s] does not support %s.", EXCHANGE_GATEWAY_NAME, subscription.toString())));
    }
}
