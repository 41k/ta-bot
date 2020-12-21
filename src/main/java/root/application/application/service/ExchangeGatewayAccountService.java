package root.application.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import root.application.application.StrategyExecutionManagersStore;
import root.application.application.model.ExchangeGatewayAccount;
import root.application.application.model.command.CreateExchangeGatewayAccountCommand;
import root.application.application.model.command.DeleteExchangeGatewayAccountCommand;
import root.application.application.repository.ExchangeGatewayAccountRepository;
import root.application.domain.trading.StrategyExecutionsManager;
import root.framework.service.UserService;

import java.util.Collection;
import java.util.NoSuchElementException;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public class ExchangeGatewayAccountService
{
    private final StrategyExecutionManagersStore strategyExecutionManagersStore;
    private final ExchangeGatewayAccountRepository exchangeGatewayAccountRepository;
    private final UserService userService;

    public void execute(CreateExchangeGatewayAccountCommand command)
    {
        var userId = userService.getCurrentUserId();
        var exchangeGatewayId = command.getExchangeGatewayId();
        log.info("Try to create account for user [{}] and exchange gateway [{}].", userId, exchangeGatewayId);
        var exchangeGatewayAccount = ExchangeGatewayAccount.builder()
            .userId(userId)
            .exchangeGatewayId(exchangeGatewayId)
            .configuration(command.getConfiguration())
            .build();
        exchangeGatewayAccountRepository.save(exchangeGatewayAccount);
        log.info("Account for user [{}] and exchange gateway [{}] has been created successfully.", userId, exchangeGatewayId);
    }

    public void execute(DeleteExchangeGatewayAccountCommand command)
    {
        var exchangeGatewayAccountId = command.getAccountId();
        log.info("Try to delete exchange gateway account [{}].", exchangeGatewayAccountId);
        performPreDeleteCheck(command);
        strategyExecutionManagersStore.deleteByExchangeGatewayAccountId(exchangeGatewayAccountId);
        exchangeGatewayAccountRepository.deleteById(exchangeGatewayAccountId);
        log.info("Exchange gateway account [{}] has been deleted successfully.", exchangeGatewayAccountId);
    }

    public ExchangeGatewayAccount getAccount(String exchangeGatewayId)
    {
        var userId = userService.getCurrentUserId();
        return exchangeGatewayAccountRepository.findByUserIdAndExchangeGatewayId(userId, exchangeGatewayId)
            .orElseThrow(() -> new NoSuchElementException(format(
                "Account is not found for user [%s] and exchange gateway [%s].", userId, exchangeGatewayId)));
    }

    public Collection<ExchangeGatewayAccount> getAllAccounts()
    {
        var userId = userService.getCurrentUserId();
        return exchangeGatewayAccountRepository.findAllByUserId(userId);
    }

    public void verifyAccount(String exchangeGatewayId, Long exchangeGatewayAccountId)
    {
        var exchangeGatewayAccount = getAccount(exchangeGatewayId);
        if (!exchangeGatewayAccount.getId().equals(exchangeGatewayAccountId))
        {
            throw new IllegalArgumentException(format(
                "Account [%s] does not exist for gateway exchange [%s].",
                exchangeGatewayAccountId, exchangeGatewayAccount));
        }
    }

    private void performPreDeleteCheck(DeleteExchangeGatewayAccountCommand command)
    {
        var exchangeGatewayAccountId = command.getAccountId();
        var accountHasStrategyExecutions = strategyExecutionManagersStore.get(exchangeGatewayAccountId)
            .map(StrategyExecutionsManager::getStrategyExecutions)
            .map(CollectionUtils::isNotEmpty)
            .orElse(Boolean.FALSE);
        if (accountHasStrategyExecutions)
        {
            var accountId = command.getAccountId();
            throw new IllegalStateException(format(
                "Attempt to delete exchange gateway account [%s] which has active strategy executions.", accountId));
        }
    }
}
