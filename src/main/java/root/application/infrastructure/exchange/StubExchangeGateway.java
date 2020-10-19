package root.application.infrastructure.exchange;

import lombok.extern.slf4j.Slf4j;
import root.application.domain.ExchangeGateway;

@Slf4j
public class StubExchangeGateway implements ExchangeGateway
{
    @Override
    public void buy(double amount)
    {
    }

    @Override
    public void sell(double amount)
    {
    }
}
