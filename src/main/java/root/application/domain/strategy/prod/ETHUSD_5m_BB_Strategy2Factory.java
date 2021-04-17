package root.application.domain.strategy.prod;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.candles.LowerShadowIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

import static root.application.domain.indicator.NumberIndicators.*;

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
        var bbm = bollingerBandsMiddle(closePrice, periodLength);
        var bbu = bollingerBandsUpper(bbm, closePrice, periodLength);
        var bbl = bollingerBandsLower(bbm, closePrice, periodLength);
        var numberIndicators = List.of(bbu, bbm, bbl);

        var barWithLongLowerShadow = new OverIndicatorRule(new LowerShadowIndicator(series), 15);
        var entryRule = new UnderIndicatorRule(lowPrice, bbl).and(barWithLongLowerShadow);
        var exitRule = new OverIndicatorRule(closePrice, bbm);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numberIndicators, entryRule, exitRule);
    }
}
