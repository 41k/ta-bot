package root.application.domain.strategy.qa;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

import static root.application.domain.indicator.NumberIndicators.rsi;
import static root.application.domain.indicator.NumberIndicators.rsiLevel;

//    Buy rule:
//        (rsi(12) crosses down rsiLevel(30))
//
//    Sell rule:
//        (rsi(12) crosses up rsiLevel(30))

public class RsiStrategy1Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "0e18f4e3-e645-46c9-9565-3344f726f1e1";
    private static final String STRATEGY_NAME = "RSI-1";

    public RsiStrategy1Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
    }

    @Override
    public Strategy create(BarSeries series)
    {
        var closePrice = new ClosePriceIndicator(series);
        var rsi = rsi(closePrice, 12);
        var rsiLevel30 = rsiLevel(30, series);
        var numberIndicators = List.of(
            rsi, rsiLevel30
        );

        Rule entryRule = // Buy rule:
                // (rsi(12) crosses down rsiLevel(30))
                new CrossedDownIndicatorRule(rsi, rsiLevel30);

        Rule exitRule = // Sell rule:
                // (rsi(12) crosses up rsiLevel(30))
                new CrossedUpIndicatorRule(rsi, rsiLevel30);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numberIndicators, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 123.11
//        N trades: 32
//        N profitable trades (UP): 29
//        N unprofitable trades (DOWN): 3
//        Risk/Reward ratio: 0.10344827586206896
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 414.91
//        N trades: 168
//        N profitable trades (UP): 126
//        N unprofitable trades (DOWN): 42
//        Risk/Reward ratio: 0.3333333333333333
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 242.04
//        N trades: 110
//        N profitable trades (UP): 79
//        N unprofitable trades (DOWN): 31
//        Risk/Reward ratio: 0.3924050632911392
