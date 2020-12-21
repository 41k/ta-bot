package root.application.domain.strategy.prod;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.application.domain.indicator.Indicator;
import root.application.domain.indicator.ema.EMAIndicator;
import root.application.domain.indicator.trend.DownTrendIndicator;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

public class ETHUSD_5m_DownTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "065c1291-16c5-44a9-ab08-ef759694738f";
    private static final String STRATEGY_NAME = "ETH-USD-5m-dt-s1";

    public ETHUSD_5m_DownTrend_Strategy1Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
    }

    @Override
    public Strategy create(BarSeries series)
    {
        var closePrice = new ClosePriceIndicator(series);
        var ema7 = new EMAIndicator(closePrice, 7);
        var ema20 = new EMAIndicator(closePrice, 20);
        var numIndicators = List.<Indicator<Num>>of(ema7, ema20);

        var entryRule = new BooleanIndicatorRule(new DownTrendIndicator(ema7, 15, 4.2))
            .and(new UnderIndicatorRule(closePrice, ema7))
            .and(new UnderIndicatorRule(ema7, ema20));

        var exitRule = new CrossedUpIndicatorRule(closePrice, ema20);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numIndicators, entryRule, exitRule);
    }
}

//    5m ETH/USD 1610196540000-1611950460000 results:
//
//    length:15_slope:4 -- 9:0 -- 374.9099999999997 -- 41.65666666666663
//    length:15_slope:4.2 -- 7:0 -- 386.2499999999998 -- 55.178571428571395
