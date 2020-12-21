package root.application.infrastructure.persistence.exchange_gateway_account;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import root.application.application.model.ExchangeGatewayAccount;
import root.application.application.repository.ExchangeGatewayAccountRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class ExchangeGatewayAccountRepositoryImpl implements ExchangeGatewayAccountRepository
{
    private final ExchangeGatewayAccountMapper mapper;
    private final ExchangeGatewayAccountDbEntryR2dbcRepository r2dbcRepository;

    @Override
    public void save(ExchangeGatewayAccount exchangeGatewayAccount)
    {
        Mono.just(exchangeGatewayAccount)
            .map(mapper::toDbEntry)
            .flatMap(r2dbcRepository::save)
            .block();
    }

    @Override
    public void deleteById(Long id)
    {
        r2dbcRepository.deleteById(id).block();
    }

    @Override
    public Optional<ExchangeGatewayAccount> findByUserIdAndExchangeGatewayId(Long userId, String exchangeGatewayId)
    {
        return r2dbcRepository.findByUserIdAndExchangeGatewayId(userId, exchangeGatewayId)
            .map(mapper::toApplicationModel)
            .blockOptional();
    }

    @Override
    public Collection<ExchangeGatewayAccount> findAllByUserId(Long userId)
    {
        return r2dbcRepository.findAllByUserId(userId)
            .map(mapper::toApplicationModel)
            .collectList()
            .block();
    }
}
