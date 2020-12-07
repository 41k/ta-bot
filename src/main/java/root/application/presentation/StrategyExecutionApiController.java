package root.application.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import root.application.application.model.StrategyExecutionInfo;
import root.application.application.model.command.RunStrategyExecutionCommand;
import root.application.application.model.command.StopStrategyExecutionCommand;
import root.application.application.service.StrategyExecutionService;
import root.application.presentation.dto.RunStrategyExecutionRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/exchange-gateways/{exchangeGatewayId}/strategy-executions")
@RequiredArgsConstructor
public class StrategyExecutionApiController
{
    private final StrategyExecutionService strategyExecutionService;

    @GetMapping
    public List<StrategyExecutionInfo> getStrategyExecutions(@PathVariable String exchangeGatewayId)
    {
        return strategyExecutionService.getStrategyExecutions(exchangeGatewayId);
    }

    @PostMapping
    public void runStrategyExecution(@PathVariable String exchangeGatewayId,
                                     @RequestBody RunStrategyExecutionRequest request)
    {
        var runStrategyExecutionCommand = RunStrategyExecutionCommand.builder()
            .exchangeGatewayId(exchangeGatewayId)
            .strategyId(request.getStrategyId())
            .symbol(request.getSymbol())
            .amount(request.getAmount())
            .interval(request.getInterval())
            .build();
        log.info("Try to run strategy execution: {}", runStrategyExecutionCommand);
        strategyExecutionService.execute(runStrategyExecutionCommand);
    }

    @DeleteMapping("/{strategyExecutionId}")
    public void stopStrategyExecution(@PathVariable String exchangeGatewayId,
                                      @PathVariable String strategyExecutionId)
    {
        var stopStrategyExecutionCommand = StopStrategyExecutionCommand.builder()
            .exchangeGatewayId(exchangeGatewayId)
            .strategyExecutionId(strategyExecutionId)
            .build();
        log.info("Try to stop strategy execution: {}", stopStrategyExecutionCommand);
        strategyExecutionService.execute(stopStrategyExecutionCommand);
    }
}
