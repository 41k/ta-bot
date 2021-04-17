package root.application.domain.strategy.prod;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.application.domain.level.MainChartLevelProvider;
import root.application.domain.rule.TakeProfitLevelRule;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

import static root.application.domain.indicator.NumberIndicators.ema;

public class ETHUSD_5m_Pullback_Strategy1Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "bd0e7afa-a2a9-46fc-bb0a-902bbc9f7b67";
    private static final String STRATEGY_NAME = "ETH-USD-5m-Pullback-s1";

    private ClosePriceIndicator closePrice;

    public ETHUSD_5m_Pullback_Strategy1Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
    }

    @Override
    public Strategy create(BarSeries series)
    {
        closePrice = new ClosePriceIndicator(series);
        var ema50 = ema(closePrice, 50);
        var ema100 = ema(closePrice, 100);
        var ema200 = ema(closePrice, 200);
        var ema400 = ema(closePrice, 400);
        var numberIndicators = List.of(ema50, ema100, ema200, ema400);

        var takeProfitLevelProvider = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        var mainChartLevelProviders = List.of(takeProfitLevelProvider);

        var entryRule = new OverIndicatorRule(ema50, ema100)
            .and(new OverIndicatorRule(ema100, ema200))
            .and(new OverIndicatorRule(ema200, ema400))
            .and(new UnderIndicatorRule(closePrice, ema400));

        var exitRule = new TakeProfitLevelRule(closePrice, takeProfitLevelProvider);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numberIndicators, entryRule, exitRule, mainChartLevelProviders, null);
    }

    private Double calculateTakeProfitLevel(Integer entryIndex)
    {
        return closePrice.getValue(entryIndex).doubleValue() + 30;
    }
}
