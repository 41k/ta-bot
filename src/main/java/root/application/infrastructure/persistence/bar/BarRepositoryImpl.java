package root.application.infrastructure.persistence.bar;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import root.application.domain.report.BarRepository;

@RequiredArgsConstructor
public class BarRepositoryImpl implements BarRepository
{
    private final BarDbEntryR2dbcRepository r2dbcRepository;

    @Override
    public Bar save(Bar bar, String exchangeId)
    {
         r2dbcRepository.save(BarDbEntry.fromDomainObject(bar, exchangeId)).subscribe();
         return bar;
    }
}
