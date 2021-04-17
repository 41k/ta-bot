package root.application.domain.rule;

import org.ta4j.core.Order;
import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.num.Num;
import root.application.domain.level.MainChartLevelProvider;

public class TakeProfitLevelRule implements Rule
{
    private final PriceIndicator price;
    private final MainChartLevelProvider levelProvider;

    public TakeProfitLevelRule(PriceIndicator price, MainChartLevelProvider levelProvider)
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
        var takeProfitLevel = getTakeProfitLevel(entryOrder);
        var currentPrice = price.getValue(index);
        if (entryOrder.isBuy())
        {
            return currentPrice.isGreaterThanOrEqual(takeProfitLevel);
        }
        else
        {
            return currentPrice.isLessThanOrEqual(takeProfitLevel);
        }
    }

    private Num getTakeProfitLevel(Order entryOrder)
    {
        var entryIndex = entryOrder.getIndex();
        var takeProfitLevel = levelProvider.getLevel(entryIndex).getValue();
        return price.numOf(takeProfitLevel);
    }
}
