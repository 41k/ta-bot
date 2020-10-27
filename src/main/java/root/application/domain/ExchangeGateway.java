package root.application.domain;

import org.ta4j.core.Bar;

import java.util.function.Consumer;

public interface ExchangeGateway
{
    String getExchangeId();

    void subscribeToBarStream(Consumer<Bar> barConsumer);

    void buy(double amount);

    void sell(double amount);
}
