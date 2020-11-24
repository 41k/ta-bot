package root.application.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import root.application.application.StrategyService;
import root.application.domain.trading.StrategyExecution;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
public class StrategyApiController
{
    private final StrategyService strategyService;

    @GetMapping("/active")
    public List<StrategyExecution.State> getStrategyExecutions(@RequestParam String exchangeGatewayId)
    {
        return strategyService.getStrategyExecutions(exchangeGatewayId);
    }

    @GetMapping("/inactive")
    public List<String> getInactiveStrategyIds(@RequestParam String exchangeGatewayId)
    {
        return strategyService.getInactiveStrategyIds(exchangeGatewayId);
    }

    @PostMapping("/activate")
    public void activateStrategy(
        @RequestParam String exchangeGatewayId, @RequestParam String strategyId, @RequestParam double amount)
    {
        log.info("Try to activate strategy [{}] for exchange gateway [{}] with amount [{}].", strategyId, exchangeGatewayId, amount);
        strategyService.activateStrategy(exchangeGatewayId, strategyId, amount);
    }

    @PostMapping("/deactivate")
    public void deactivateStrategy(
        @RequestParam String exchangeGatewayId, @RequestParam String strategyId)
    {
        log.info("Try to deactivate strategy [{}] for exchange gateway [{}].", strategyId, exchangeGatewayId);
        strategyService.deactivateStrategy(exchangeGatewayId, strategyId);
    }
}
