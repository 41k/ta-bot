package root.application.domain.strategy.prod;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.*;
import root.application.domain.indicator.trend.UpTrendIndicator;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

import static root.application.domain.indicator.NumberIndicators.*;

public class ETHUSD_5m_UpTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "0be0daf2-00ac-47c9-a783-f211a53eeb43";
    private static final String STRATEGY_NAME = "ETH-USD-5m-ut-s1";

    public ETHUSD_5m_UpTrend_Strategy1Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
    }

    @Override
    public Strategy create(BarSeries series)
    {
        var closePrice = new ClosePriceIndicator(series);
        var sma10 = sma(closePrice, 10);
        var sma100 = sma(closePrice, 100);
        var sma200 = sma(closePrice, 200);
        var wr = williamsR(10, series);
        var wrLevelMinus10 = williamsRLevel(-10, series);
        var wrLevelMinus90 = williamsRLevel(-90, series);
        var numberIndicators = List.of(sma10, sma100, sma200, wr, wrLevelMinus10, wrLevelMinus90);

        var entryRule = new OverIndicatorRule(closePrice, sma200)
            .and(new UnderIndicatorRule(closePrice, sma10))
            .and(new CrossedDownIndicatorRule(wr, wrLevelMinus90))
            .and(new UnderIndicatorRule(sma100, sma200))
            .and(new BooleanIndicatorRule(new UpTrendIndicator(sma100, 10, 0.01)));
            // as more safe replacement for sma100 up trend rule
            //.and(new IsFallingRule(new DifferenceIndicator(sma200, sma100), 10));

        var exitRule = new CrossedUpIndicatorRule(wr, wrLevelMinus10);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numberIndicators, entryRule, exitRule);
    }
}
