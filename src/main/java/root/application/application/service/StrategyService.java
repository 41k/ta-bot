package root.application.application.service;

import lombok.RequiredArgsConstructor;
import root.application.application.model.StrategyInfo;
import root.application.domain.strategy.StrategyFactory;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class StrategyService
{
    private final Map<String, StrategyFactory> strategyFactoriesStore;

    public Collection<StrategyInfo> getStrategies()
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

    public StrategyFactory getStrategyFactory(String strategyId)
    {
        return ofNullable(strategyFactoriesStore.get(strategyId)).orElseThrow(() ->
            new NoSuchElementException(format("Strategy factory is not found for strategyId [%s].", strategyId)));
    }
}
