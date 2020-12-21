package root.application.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.application.model.StrategyInfo;
import root.application.application.service.StrategyService;

import java.util.Collection;

@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
public class StrategyApiController
{
    private final StrategyService strategyService;

    @GetMapping
    public Collection<StrategyInfo> getStrategies()
    {
        return strategyService.getStrategies();
    }
}
