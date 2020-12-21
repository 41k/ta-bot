package root.application.application.repository;

import root.application.application.model.ExchangeGatewayAccount;

import java.util.Collection;
import java.util.Optional;

public interface ExchangeGatewayAccountRepository
{
    void save(ExchangeGatewayAccount exchangeGatewayAccount);

    void deleteById(Long id);

    Optional<ExchangeGatewayAccount> findByUserIdAndExchangeGatewayId(Long userId, String exchangeGatewayId);

    Collection<ExchangeGatewayAccount> findAllByUserId(Long userId);
}
