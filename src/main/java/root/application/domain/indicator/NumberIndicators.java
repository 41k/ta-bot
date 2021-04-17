package root.application.domain.indicator;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.indicators.helpers.DifferenceIndicator;
import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import static java.lang.String.format;
import static root.application.domain.ChartType.ADDITIONAL;
import static root.application.domain.ChartType.MAIN;

public final class NumberIndicators
{
    private static final String ONE_PARAMETER_NAME_FORMAT = "%s(%d)";
    private static final String TWO_PARAMETERS_NAME_FORMAT = "%s(%d, %d)";
    private static final String LEVEL_FORMAT = "%s-level(%d)";

    public static NumberIndicator tema(Indicator<Num> indicator, int length)
    {
        return NumberIndicator.builder()
            .name(format(ONE_PARAMETER_NAME_FORMAT, "TEMA", length))
            .chartType(MAIN)
            .indicator(new TripleEMAIndicator(indicator, length))
            .build();
    }

    public static NumberIndicator ema(Indicator<Num> indicator, int length)
    {
        return NumberIndicator.builder()
            .name(format(ONE_PARAMETER_NAME_FORMAT, "EMA", length))
            .chartType(MAIN)
            .indicator(new EMAIndicator(indicator, length))
            .build();
    }

    public static NumberIndicator sma(Indicator<Num> indicator, int length)
    {
        return NumberIndicator.builder()
            .name(format(ONE_PARAMETER_NAME_FORMAT, "SMA", length))
            .chartType(MAIN)
            .indicator(new SMAIndicator(indicator, length))
            .build();
    }

    public static NumberIndicator bollingerBandsMiddle(PriceIndicator priceIndicator, int length)
    {
        return NumberIndicator.builder()
            .name("BBM")
            .chartType(MAIN)
            .indicator(new BollingerBandsMiddleIndicator(new SMAIndicator(priceIndicator, length)))
            .build();
    }

    public static NumberIndicator bollingerBandsUpper(NumberIndicator bbm, PriceIndicator priceIndicator, int length)
    {
        var standardDeviation = new StandardDeviationIndicator(priceIndicator, length);
        var bbmIndicator = (BollingerBandsMiddleIndicator) bbm.getIndicator();
        return NumberIndicator.builder()
            .name("BBU")
            .chartType(MAIN)
            .indicator(new BollingerBandsUpperIndicator(bbmIndicator, standardDeviation, bbm.numOf(2)))
            .build();
    }

    public static NumberIndicator bollingerBandsLower(NumberIndicator bbm, PriceIndicator priceIndicator, int length)
    {
        var standardDeviation = new StandardDeviationIndicator(priceIndicator, length);
        var bbmIndicator = (BollingerBandsMiddleIndicator) bbm.getIndicator();
        return NumberIndicator.builder()
            .name("BBL")
            .chartType(MAIN)
            .indicator(new BollingerBandsLowerIndicator(bbmIndicator, standardDeviation, bbm.numOf(2)))
            .build();
    }

    public static NumberIndicator parabolicSAR(BarSeries series)
    {
        return NumberIndicator.builder()
            .name("SAR")
            .chartType(MAIN)
            .indicator(new ParabolicSarIndicator(series))
            .build();
    }

    public static NumberIndicator williamsR(int length, BarSeries series)
    {
        return NumberIndicator.builder()
            .name(format(ONE_PARAMETER_NAME_FORMAT, "WR", length))
            .chartType(ADDITIONAL)
            .indicator(new WilliamsRIndicator(series, length))
            .build();
    }

    public static NumberIndicator williamsRLevel(int value, BarSeries series)
    {
        return level("WR", value, series);
    }

    public static NumberIndicator rsi(PriceIndicator priceIndicator, int length)
    {
        return NumberIndicator.builder()
            .name(format(ONE_PARAMETER_NAME_FORMAT, "RSI", length))
            .chartType(ADDITIONAL)
            .indicator(new RSIIndicator(priceIndicator, length))
            .build();
    }

    public static NumberIndicator rsiLevel(int value, BarSeries series)
    {
        return level("RSI", value, series);
    }

    public static NumberIndicator macd(PriceIndicator priceIndicator, int shortLength, int longLength)
    {
        return NumberIndicator.builder()
            .name(format(TWO_PARAMETERS_NAME_FORMAT, "MACD", shortLength, longLength))
            .chartType(ADDITIONAL)
            .indicator(new MACDIndicator(priceIndicator, shortLength, longLength))
            .build();
    }

    public static NumberIndicator macdSignal(NumberIndicator macd, int length)
    {
        var macdIndicator = (MACDIndicator) macd.getIndicator();
        return NumberIndicator.builder()
            .name(format(ONE_PARAMETER_NAME_FORMAT, "MACD-signal", length))
            .chartType(ADDITIONAL)
            .indicator(new EMAIndicator(macdIndicator, length))
            .build();
    }

    public static NumberIndicator macdDifference(NumberIndicator macd, NumberIndicator macdSignal)
    {
        var macdIndicator = (MACDIndicator) macd.getIndicator();
        var macdSignalIndicator = (EMAIndicator) macdSignal.getIndicator();
        return NumberIndicator.builder()
            .name("MACD-diff")
            .chartType(ADDITIONAL)
            .indicator(new DifferenceIndicator(macdIndicator, macdSignalIndicator))
            .build();
    }

    public static NumberIndicator macdLevel(int value, BarSeries series)
    {
        return level("MACD", value, series);
    }

    public static NumberIndicator adx(BarSeries series, int diBarCount, int adxBarCount)
    {
        return NumberIndicator.builder()
            .name(format(TWO_PARAMETERS_NAME_FORMAT, "ADX", diBarCount, adxBarCount))
            .chartType(ADDITIONAL)
            .indicator(new ADXIndicator(series, diBarCount, adxBarCount))
            .build();
    }

    public static NumberIndicator adxLevel(int value, BarSeries series)
    {
        return level("ADX", value, series);
    }

    public static NumberIndicator level(String namePrefix, int value, BarSeries series)
    {
        return NumberIndicator.builder()
            .name(format(LEVEL_FORMAT, namePrefix, value))
            .chartType(ADDITIONAL)
            .indicator(new ConstantIndicator<>(series, series.numOf(value)))
            .build();
    }

    private NumberIndicators()
    {
    }
}
