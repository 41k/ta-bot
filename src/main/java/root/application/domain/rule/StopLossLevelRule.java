package root.application.domain.rule;

import org.ta4j.core.Order;
import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.num.Num;
import root.application.domain.level.MainChartLevelProvider;

public class StopLossLevelRule implements Rule
{
    private final PriceIndicator price;
    private final MainChartLevelProvider levelProvider;

    public StopLossLevelRule(PriceIndicator price, MainChartLevelProvider levelProvider)
    {
        this.price = price;
        this.levelProvider = levelProvider;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord)
    {
        if (tradingRecord == null)
        {
            return false;
        }
        var currentTrade = tradingRecord.getCurrentTrade();
        if (!currentTrade.isOpened())
        {
            return  false;
        }
        var entryOrder = currentTrade.getEntry();
        var stopLossLevel = getStopLossLevel(entryOrder);
        var currentPrice = price.getValue(index);
        if (entryOrder.isBuy())
        {
            return currentPrice.isLessThanOrEqual(stopLossLevel);
        }
        else
        {
            return currentPrice.isGreaterThanOrEqual(stopLossLevel);
        }
    }

    private Num getStopLossLevel(Order entryOrder)
    {
        var entryIndex = entryOrder.getIndex();
        var stopLossLevel = levelProvider.getLevel(entryIndex).getValue();
        return price.numOf(stopLossLevel);
    }
}
