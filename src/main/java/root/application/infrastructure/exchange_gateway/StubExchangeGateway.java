package root.application.infrastructure.exchange_gateway;

import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;
import root.application.domain.ExchangeGateway;
import root.application.domain.report.BarRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class StubExchangeGateway implements ExchangeGateway
{
    private static final String EXCHANGE_GATEWAY_ID = "stub-exchange";

    private final BarRepository barRepository;
    private final CsvBarProvider barProvider;
    private final List<Consumer<Bar>> barStreamConsumers;

    public StubExchangeGateway(BarRepository barRepository)
    {
        this.barRepository = barRepository;
        this.barProvider = new CsvBarProvider();
        this.barStreamConsumers = new ArrayList<>();
        //run();
    }

    @Override
    public String getId()
    {
        return EXCHANGE_GATEWAY_ID;
    }

    @Override
    public void subscribeToBarStream(Consumer<Bar> barConsumer)
    {
        barStreamConsumers.add(barConsumer);
    }

    @Override
    public void buy(double amount)
    {
    }

    @Override
    public void sell(double amount)
    {
    }

    private void run()
    {
        Flux.fromIterable(barProvider.getBars())
            .delaySubscription(Duration.ofSeconds(60))
            .map(bar -> barRepository.save(bar, EXCHANGE_GATEWAY_ID))
            .map(this::processBar)
            .subscribe();
    }

    private Bar processBar(Bar bar)
    {
        barStreamConsumers.forEach(barStreamConsumer -> barStreamConsumer.accept(bar));
        return bar;
    }
}
