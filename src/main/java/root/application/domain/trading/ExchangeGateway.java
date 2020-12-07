package root.application.domain.trading;

import org.ta4j.core.Bar;

import java.util.Set;
import java.util.function.Consumer;

public interface ExchangeGateway
{
    String getId();

    String getName();

    Set<Interval> getSupportedIntervals();

    Set<String> getSupportedSymbols();

    void subscribeToBarStream(Interval interval, Consumer<Bar> barConsumer);

    TradingOperationResult buy(TradingOperationContext context);

    TradingOperationResult sell(TradingOperationContext context);
}
