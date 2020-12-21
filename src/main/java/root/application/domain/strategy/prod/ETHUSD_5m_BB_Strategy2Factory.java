package root.application.domain.strategy.prod;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.candles.LowerShadowIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.application.domain.indicator.Indicator;
import root.application.domain.indicator.bollinger.BBLowerIndicator;
import root.application.domain.indicator.bollinger.BBMiddleIndicator;
import root.application.domain.indicator.bollinger.BBUpperIndicator;
import root.application.domain.indicator.sma.SMAIndicator;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

public class ETHUSD_5m_BB_Strategy2Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "2737b587-c18b-4f8d-b8a2-19d5d114f8f4";
    private static final String STRATEGY_NAME = "ETH-USD-5m-BB-s2";

    public ETHUSD_5m_BB_Strategy2Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
    }

    @Override
    public Strategy create(BarSeries series)
    {
        var closePrice = new ClosePriceIndicator(series);
        var lowPrice = new LowPriceIndicator(series);
        var periodLength = 20;
        var standardDeviation = new StandardDeviationIndicator(closePrice, periodLength);
        var bbm = new BBMiddleIndicator(new SMAIndicator(closePrice, periodLength));
        var bbu = new BBUpperIndicator(bbm, standardDeviation, series.numOf(2));
        var bbl = new BBLowerIndicator(bbm, standardDeviation, series.numOf(2));
        var numIndicators = List.<Indicator<Num>>of(bbu, bbm, bbl);

        var barWithLongLowerShadow = new OverIndicatorRule(new LowerShadowIndicator(series), 15);
        var entryRule = new UnderIndicatorRule(lowPrice, bbl).and(barWithLongLowerShadow);
        var exitRule = new OverIndicatorRule(closePrice, bbm);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numIndicators, entryRule, exitRule);
    }
}
