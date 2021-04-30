package root.application.domain.rule;

import org.ta4j.core.Rule;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.num.Num;
import root.application.domain.level.MainChartLevelProvider;

public abstract class AbstractMainChartLevelRule implements Rule
{
    protected final PriceIndicator priceIndicator;
    private final MainChartLevelProvider levelProvider;

    public AbstractMainChartLevelRule(PriceIndicator priceIndicator, MainChartLevelProvider levelProvider)
    {
        this.priceIndicator = priceIndicator;
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
        var levelValue = getLevelValue(currentTrade);
        return isSatisfied(index, levelValue);
    }

    private Num getLevelValue(Trade currentTrade)
    {
        var entryIndex = currentTrade.getEntry().getIndex();
        var levelValue = levelProvider.getLevel(entryIndex).getValue();
        return priceIndicator.numOf(levelValue);
    }

    protected abstract boolean isSatisfied(int index, Num levelValue);
}
