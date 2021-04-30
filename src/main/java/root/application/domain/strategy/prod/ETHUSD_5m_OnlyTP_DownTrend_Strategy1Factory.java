package root.application.domain.strategy.prod;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import root.application.domain.level.MainChartLevelProvider;
import root.application.domain.rule.OverMainChartLevelRule;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

import static root.application.domain.indicator.NumberIndicators.ema;

public class ETHUSD_5m_OnlyTP_DownTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "0721fe91-1741-4e1f-9253-c33fad8ff2fb";
    private static final String STRATEGY_NAME = "ETH-USD-5m-OnlyTP-DownTrend-s1";

    public ETHUSD_5m_OnlyTP_DownTrend_Strategy1Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
    }

    @Override
    public Strategy create(BarSeries series)
    {
        var closePrice = new ClosePriceIndicator(series);
        var ema50 = ema(closePrice, 50);
        var ema100 = ema(closePrice, 100);
        var ema200 = ema(closePrice, 200);
        var ema300 = ema(closePrice, 300);
        var numberIndicators = List.of(ema50, ema100, ema200, ema300);

        var takeProfitLevel = new MainChartLevelProvider("TP", entryIndex -> closePrice.getValue(entryIndex).doubleValue() + 25);
        var mainChartLevelProviders = List.of(takeProfitLevel);

        var entryRule = new OverIndicatorRule(ema300, ema200)
            .and(new OverIndicatorRule(ema200, ema100))
            .and(new OverIndicatorRule(ema100, ema50))
            .and(new CrossedUpIndicatorRule(closePrice, ema100));

        var exitRule = new OverMainChartLevelRule(closePrice, takeProfitLevel);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numberIndicators, entryRule, exitRule, mainChartLevelProviders, null);
    }
}
