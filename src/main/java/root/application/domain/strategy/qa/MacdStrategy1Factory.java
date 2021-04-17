package root.application.domain.strategy.qa;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.application.domain.strategy.AbstractStrategyFactory;
import root.application.domain.strategy.Strategy;

import java.util.List;

import static root.application.domain.indicator.NumberIndicators.*;

//    Buy rule:
//        (macdDiff < level(0))
//        AND
//        (macdSignalLine crosses down macdDiff)
//
//    Sell rule:
//        (macd crosses up level(0))

public class MacdStrategy1Factory extends AbstractStrategyFactory
{
    private static final String STRATEGY_ID = "8795316a-c6ef-4cab-bc84-6e508701f95f";
    private static final String STRATEGY_NAME = "MACD-1";

    public MacdStrategy1Factory()
    {
        super(STRATEGY_ID, STRATEGY_NAME);
    }

    @Override
    public Strategy create(BarSeries series)
    {
        var closePrice = new ClosePriceIndicator(series);
        var macd = macd(closePrice, 12, 26);
        var macdSignalLine = macdSignal(macd, 9);
        var macdDifference = macdDifference(macd, macdSignalLine);
        var macdLevel0 = macdLevel(0, series);
        var numberIndicators = List.of(
            macd, macdSignalLine, macdDifference, macdLevel0
        );

        Rule entryRule = // Buy rule:
                // (macdDiff < level(0))
                new UnderIndicatorRule(macdDifference, macdLevel0)
                // AND
                // (macdSignalLine crosses down macdDiff)
                .and(new CrossedDownIndicatorRule(macdSignalLine, macdDifference));

        Rule exitRule = // Sell rule:
                // (macd crosses up level(0))
                new CrossedUpIndicatorRule(macd, macdLevel0);

        return new Strategy(STRATEGY_ID, STRATEGY_NAME, numberIndicators, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit:	159.01
//        N trades:	26
//        N profitable trades (UP):	19
//        N unprofitable trades (DOWN):	7
//        Risk/Reward ratio:	0.37
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit:	193.6
//        N trades:	114
//        N profitable trades (UP):	81
//        N unprofitable trades (DOWN):	33
//        Risk/Reward ratio:	0.41
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit:	834.05
//        N trades:	111
//        N profitable trades (UP):	82
//        N unprofitable trades (DOWN):	29
//        Risk/Reward ratio:	0.35
