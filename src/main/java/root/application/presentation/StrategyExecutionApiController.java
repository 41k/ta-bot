package root.application.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import root.application.application.model.StrategyExecutionInfo;
import root.application.application.model.command.RunStrategyExecutionCommand;
import root.application.application.model.command.StopStrategyExecutionCommand;
import root.application.application.service.StrategyExecutionService;
import root.application.presentation.dto.RunStrategyExecutionRequest;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-gateways/{exchangeGatewayId}/accounts/{accountId}/strategy-executions")
@RequiredArgsConstructor
public class StrategyExecutionApiController
{
    private final StrategyExecutionService strategyExecutionService;

    @GetMapping
    public Collection<StrategyExecutionInfo> getStrategyExecutions(@PathVariable String exchangeGatewayId,
                                                                   @PathVariable Long accountId)
    {
        return strategyExecutionService.getStrategyExecutions(exchangeGatewayId, accountId);
    }

    @PostMapping
    public void runStrategyExecution(@PathVariable String exchangeGatewayId,
                                     @PathVariable Long accountId,
                                     @RequestBody RunStrategyExecutionRequest request)
    {
        // Todo: Fix it
        // By default calls to this endpoint is executed on reactor-http-nio-* thread which does not allow blocking operations.
        // So it is necessary to switch to boundedElastic thread which allows blocking operations in order to avoid failures on
        // blocking operations which are performed on repository level.
        Mono.just(exchangeGatewayId)
            .publishOn(Schedulers.boundedElastic())
            .map(id -> {
                var runStrategyExecutionCommand = RunStrategyExecutionCommand.builder()
                    .exchangeGatewayId(exchangeGatewayId)
                    .exchangeGatewayAccountId(accountId)
                    .strategyId(request.getStrategyId())
                    .symbol(request.getSymbol())
                    .amount(request.getAmount())
                    .interval(request.getInterval())
                    .build();
                strategyExecutionService.execute(runStrategyExecutionCommand);
                return id;
            })
            .subscribe();
    }

    @DeleteMapping("/{strategyExecutionId}")
    public void stopStrategyExecution(@PathVariable String exchangeGatewayId,
                                      @PathVariable Long accountId,
                                      @PathVariable String strategyExecutionId)
    {
        var stopStrategyExecutionCommand = StopStrategyExecutionCommand.builder()
            .exchangeGatewayId(exchangeGatewayId)
            .exchangeGatewayAccountId(accountId)
            .strategyExecutionId(strategyExecutionId)
            .build();
        strategyExecutionService.execute(stopStrategyExecutionCommand);
    }
}
