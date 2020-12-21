package root.application.infrastructure.persistence.bar;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import root.application.domain.history.BarRepository;
import root.application.domain.trading.Interval;
import root.application.domain.trading.Symbol;

@RequiredArgsConstructor
public class BarRepositoryImpl implements BarRepository
{
    private final BarDbEntryR2dbcRepository r2dbcRepository;

    @Override
    public void save(Bar bar, String exchangeGateway, Symbol symbol, Interval interval)
    {
         r2dbcRepository.save(BarDbEntry.fromDomainObject(bar, exchangeGateway, symbol, interval)).subscribe();
    }
}
