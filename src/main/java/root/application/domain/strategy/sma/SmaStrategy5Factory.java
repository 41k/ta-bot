package root.application.domain.strategy.sma;

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.application.domain.indicator.sma.SMAIndicator;
import root.application.domain.strategy.AbstractStrategyFactory;

import java.util.List;

//    * SMA(7) - shortSma,
//    * SMA(25) - mediumSma,
//    * SMA(100) - longSma
//
//    Buy rule:
//        (shortSma crosses down mediumSma)
//
//    Sell rule:
//        (closePrise crosses up mediumSma)

public class SmaStrategy5Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "ed2fb8fb-d1b4-4bf2-b9c5-68074931e2a8";
    private static final String STRATEGY_NAME = "SMA-5";

    private final ClosePriceIndicator closePriceIndicator;
    private final SMAIndicator shortSmaIndicator;
    private final SMAIndicator mediumSmaIndicator;
    private final SMAIndicator longSmaIndicator;

    public SmaStrategy5Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);
        this.mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        this.longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        numIndicators.addAll(List.of(
            shortSmaIndicator, mediumSmaIndicator, longSmaIndicator
        ));
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                // (shortSma crosses down mediumSma)
                new CrossedDownIndicatorRule(shortSmaIndicator, mediumSmaIndicator);

        Rule exitRule = // Sell rule:
                // (closePrise crosses up mediumSma)
                new CrossedUpIndicatorRule(closePriceIndicator, mediumSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 276.65
//        N trades: 48
//        N profitable trades (UP): 39
//        N unprofitable trades (DOWN): 9
//        Risk/Reward ratio: 0.23076923076923078
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 268.14
//        N trades: 201
//        N profitable trades (UP): 146
//        N unprofitable trades (DOWN): 55
//        Risk/Reward ratio: 0.3767123287671233
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 424.77
//        N trades: 175
//        N profitable trades (UP): 123
//        N unprofitable trades (DOWN): 52
//        Risk/Reward ratio: 0.42276422764227645

