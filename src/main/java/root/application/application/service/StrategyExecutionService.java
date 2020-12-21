package root.application.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import root.application.application.StrategyExecutionManagersStore;
import root.application.application.model.StrategyExecutionInfo;
import root.application.application.model.command.RunStrategyExecutionCommand;
import root.application.application.model.command.StopStrategyExecutionCommand;
import root.application.domain.history.TradeHistoryItemRepository;
import root.application.domain.trading.StrategyExecution;
import root.application.domain.trading.StrategyExecutionContext;
import root.application.domain.trading.StrategyExecutionsManager;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class StrategyExecutionService
{
    private final StrategyExecutionManagersStore strategyExecutionManagersStore;
    private final ExchangeGatewayAccountService exchangeGatewayAccountService;
    private final ExchangeGatewayService exchangeGatewayService;
    private final StrategyService strategyService;
    private final HistoryService historyService;
    private final TradeHistoryItemRepository tradeHistoryItemRepository;

    public Collection<StrategyExecutionInfo> getStrategyExecutions(String exchangeGatewayId, Long exchangeGatewayAccountId)
    {
        exchangeGatewayAccountService.verifyAccount(exchangeGatewayId, exchangeGatewayAccountId);
        return strategyExecutionManagersStore.get(exchangeGatewayAccountId)
            .map(StrategyExecutionsManager::getStrategyExecutions)
            .orElseGet(List::of)
            .stream()
            .map(this::buildStrategyExecutionInfo)
            .collect(toList());
    }

    public void execute(RunStrategyExecutionCommand command)
    {
        log.info("Try to run strategy execution: {}", command);
        var exchangeGatewayId = command.getExchangeGatewayId();
        var exchangeGateway = exchangeGatewayService.getExchangeGateway(exchangeGatewayId);
        var exchangeGatewayAccount = exchangeGatewayAccountService.getAccount(exchangeGatewayId);
        var strategyId = command.getStrategyId();
        var strategyFactory = strategyService.getStrategyFactory(strategyId);
        var strategyExecutionContext = StrategyExecutionContext.builder()
            .userId(exchangeGatewayAccount.getUserId().toString())
            .exchangeGateway(exchangeGateway)
            .exchangeGatewayAccountConfiguration(exchangeGatewayAccount.getConfiguration())
            .strategyFactory(strategyFactory)
            .symbol(command.getSymbol())
            .amount(command.getAmount())
            .interval(command.getInterval())
            .build();
        var exchangeGatewayAccountId = exchangeGatewayAccount.getId();
        var strategyExecutionsManager = getOrCreateStrategyExecutionsManager(exchangeGatewayAccountId);
        strategyExecutionsManager.runStrategyExecution(strategyExecutionContext);
    }

    public void execute(StopStrategyExecutionCommand command)
    {
        log.info("Try to stop strategy execution: {}", command);
        var exchangeGatewayId = command.getExchangeGatewayId();
        var exchangeGatewayAccountId = command.getExchangeGatewayAccountId();
        exchangeGatewayAccountService.verifyAccount(exchangeGatewayId, exchangeGatewayAccountId);
        var strategyExecutionsManager = strategyExecutionManagersStore.getOrThrowException(exchangeGatewayAccountId);
        var strategyExecutionId = command.getStrategyExecutionId();
        strategyExecutionsManager.stopStrategyExecution(strategyExecutionId);
    }

    private StrategyExecutionsManager getOrCreateStrategyExecutionsManager(Long exchangeGatewayAccountId)
    {
        var strategyExecutionsManager = strategyExecutionManagersStore.get(exchangeGatewayAccountId)
            .orElseGet(() -> new StrategyExecutionsManager(tradeHistoryItemRepository));
        strategyExecutionManagersStore.put(exchangeGatewayAccountId, strategyExecutionsManager);
        return strategyExecutionsManager;
    }

    private StrategyExecutionInfo buildStrategyExecutionInfo(StrategyExecution.State strategyExecutionState)
    {
        var strategyExecutionId = strategyExecutionState.getId();
        var strategyExecutionStatistics = historyService.getStrategyExecutionStatistics(strategyExecutionId);
        return StrategyExecutionInfo.builder()
            .id(strategyExecutionId)
            .startTime(strategyExecutionState.getStartTime())
            .status(strategyExecutionState.getStatus())
            .strategyName(strategyExecutionState.getStrategyName())
            .symbol(strategyExecutionState.getSymbol())
            .amount(strategyExecutionState.getAmount())
            .interval(strategyExecutionState.getInterval())
            .statistics(strategyExecutionStatistics)
            .build();
    }
}
