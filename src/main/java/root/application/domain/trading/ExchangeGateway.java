package root.application.domain.trading;

import java.util.Collection;

public interface ExchangeGateway
{
    String getId();

    String getName();

    double getTransactionFee();

    Collection<Symbol> getSupportedSymbols();

    Collection<Interval> getSupportedIntervals();

    Collection<ExchangeGatewayAccountConfigurationProperty> getSupportedAccountConfigurationProperties();

    void subscribeToBarStream(BarStreamSubscriptionContext subscriptionContext);

    void unsubscribeFromBarStream(BarStreamSubscriptionContext subscriptionContext);

    TradingOperationResult buy(TradingOperationContext context);

    TradingOperationResult sell(TradingOperationContext context);
}
