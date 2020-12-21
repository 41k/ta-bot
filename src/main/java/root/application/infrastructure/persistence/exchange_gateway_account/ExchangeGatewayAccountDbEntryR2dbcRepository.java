package root.application.infrastructure.persistence.exchange_gateway_account;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeGatewayAccountDbEntryR2dbcRepository extends R2dbcRepository<ExchangeGatewayAccountDbEntry, Long>
{
    @Query("SELECT * FROM exchange_gateway_account WHERE user_id = :userId")
    Flux<ExchangeGatewayAccountDbEntry> findAllByUserId(Long userId);

    @Query("SELECT * FROM exchange_gateway_account WHERE user_id = :userId AND exchange_gateway_id = :exchangeGatewayId")
    Mono<ExchangeGatewayAccountDbEntry> findByUserIdAndExchangeGatewayId(Long userId, String exchangeGatewayId);
}
