package root.application.infrastructure.persistence.bar;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import root.application.domain.history.BarRepository;
import root.application.domain.trading.Interval;

@RequiredArgsConstructor
public class BarRepositoryImpl implements BarRepository
{
    private final BarDbEntryR2dbcRepository r2dbcRepository;

    @Override
    public Bar save(Bar bar, String exchangeGateway, Interval interval)
    {
         r2dbcRepository.save(BarDbEntry.fromDomainObject(bar, exchangeGateway, interval)).subscribe();
         return bar;
    }
}
