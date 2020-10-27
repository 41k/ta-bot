package root.application.domain.strategy.rsi;

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.application.domain.indicator.Indicator;
import root.application.domain.indicator.rsi.RSIIndicator;
import root.application.domain.indicator.rsi.RSILevelIndicator;
import root.application.domain.strategy.AbstractStrategyFactory;

import java.util.List;

//    Buy rule:
//        (rsi(12) crosses down rsiLevel(30))
//
//    Sell rule:
//        (rsi(12) crosses up rsiLevel(30))

public class RsiStrategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePriceIndicator;
    private final RSIIndicator rsiIndicator;
    private final RSILevelIndicator rsiLevel30Indicator;

    public RsiStrategy1Factory()
    {
        super("RSI#1");
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.rsiIndicator = new RSIIndicator(closePriceIndicator, 12);
        this.rsiLevel30Indicator = new RSILevelIndicator(series, series.numOf(30));
        numIndicators.addAll(List.of(
                rsiIndicator, rsiLevel30Indicator
        ));
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                // (rsi(12) crosses down rsiLevel(30))
                new CrossedDownIndicatorRule(rsiIndicator, rsiLevel30Indicator);

        Rule exitRule = // Sell rule:
                // (rsi(12) crosses up rsiLevel(30))
                new CrossedUpIndicatorRule(rsiIndicator, rsiLevel30Indicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
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
