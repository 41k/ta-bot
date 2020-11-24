package root.application.infrastructure.persistence.bar;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import root.application.domain.report.BarRepository;

@RequiredArgsConstructor
public class BarRepositoryImpl implements BarRepository
{
    private final BarDbEntryR2dbcRepository r2dbcRepository;

    @Override
    public Bar save(Bar bar, String exchangeGatewayId)
    {
         r2dbcRepository.save(BarDbEntry.fromDomainObject(bar, exchangeGatewayId)).subscribe();
         return bar;
    }
}
