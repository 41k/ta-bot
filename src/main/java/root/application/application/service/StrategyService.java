package root.application.application.service;

import lombok.RequiredArgsConstructor;
import root.application.application.model.StrategyInfo;
import root.application.domain.strategy.StrategyFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class StrategyService
{
    private final Map<String, StrategyFactory> strategyFactoriesStore;

    public Set<StrategyInfo> getStrategies()
    {
        return strategyFactoriesStore.values()
            .stream()
            .map(strategyFactory ->
                StrategyInfo.builder()
                    .id(strategyFactory.getStrategyId())
                    .name(strategyFactory.getStrategyName())
                    .build())
            .collect(toSet());
    }
}
