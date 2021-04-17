package root.application.domain.strategy.prod;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.application.domain.indicator.bar.BarIndicator;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

import static java.lang.Double.MAX_VALUE;
import static root.application.domain.indicator.NumberIndicators.*;
import static root.application.domain.indicator.bar.BarType.BULLISH;

public class ETHUSD_5m_BB_Strategy1Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "8089fd49-3437-49e4-bd03-78e023368a0b";
    private static final String STRATEGY_NAME = "ETH-USD-5m-BB-s1";

    public ETHUSD_5m_BB_Strategy1Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
    }

    @Override
    public Strategy create(BarSeries series)
    {
        var closePrice = new ClosePriceIndicator(series);
        var lowPrice = new LowPriceIndicator(series);
        var periodLength = 20;
        var bbm = bollingerBandsMiddle(closePrice, periodLength);
        var bbu = bollingerBandsUpper(bbm, closePrice, periodLength);
        var bbl = bollingerBandsLower(bbm, closePrice, periodLength);
        var numberIndicators = List.of(bbu, bbm, bbl);

        var hammerBar = new BooleanIndicatorRule(new BarIndicator(BULLISH, 0, 5, 11, MAX_VALUE, 0, MAX_VALUE, series));
        var entryRule = new UnderIndicatorRule(lowPrice, bbl).and(hammerBar);
        var exitRule = new OverIndicatorRule(closePrice, bbm);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numberIndicators, entryRule, exitRule);
    }
}
