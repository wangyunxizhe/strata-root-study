/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure.deposit;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.MultiCurrencyAmount;
import com.opengamma.strata.data.scenario.CurrencyScenarioArray;
import com.opengamma.strata.data.scenario.DoubleScenarioArray;
import com.opengamma.strata.data.scenario.MultiCurrencyScenarioArray;
import com.opengamma.strata.data.scenario.ScenarioArray;
import com.opengamma.strata.data.scenario.ScenarioMarketData;
import com.opengamma.strata.market.param.CurrencyParameterSensitivities;
import com.opengamma.strata.measure.rate.RatesMarketDataLookup;
import com.opengamma.strata.pricer.deposit.DiscountingTermDepositTradePricer;
import com.opengamma.strata.pricer.rate.RatesProvider;
import com.opengamma.strata.product.deposit.ResolvedTermDepositTrade;
import com.opengamma.strata.product.deposit.TermDepositTrade;

/**
 * 计算定期存款交易的定价和风险措施。
 * <p>
 * 这为定期存款定价和风险度量提供了一个高层次的切入点。
 * <p>
 * 每个方法都使用{@link ResolvedTermDepositTrade}，而应用程序代码通常使用{@link TermDepositTrade}。
 * 调用
 * {@link TermDepositTrade#resolve(com.opengamma.strata.basics.ReferenceData) TermDepositTrade::resolve(ReferenceData)}
 * 将{@code TermDepositTrade}转换为{@code ResolvedTermDepositTrade}。
 */
public class TermDepositTradeCalculations {

    /**
     * Default implementation.
     */
    public static final TermDepositTradeCalculations DEFAULT = new TermDepositTradeCalculations(
            DiscountingTermDepositTradePricer.DEFAULT);

    /**
     * Pricer for {@link ResolvedTermDepositTrade}.
     */
    private final TermDepositMeasureCalculations calc;

    /**
     * Creates an instance.
     * <p>
     * 在大多数情况下，应用程序应该使用{@link #DEFAULT}实例。
     *
     * @param tradePricer {@link ResolvedTermDepositTrade}的价格
     */
    public TermDepositTradeCalculations(
            DiscountingTermDepositTradePricer tradePricer) {
        this.calc = new TermDepositMeasureCalculations(tradePricer);
    }

    //-------------------------------------------------------------------------

    /**
     * 计算一个或多个场景的现值。
     *
     * @param trade      the trade
     * @param lookup     用于查询市场数据的lookup
     * @param marketData the market data
     * @return 现值，每个场景一个条目
     */
    public CurrencyScenarioArray presentValue(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.presentValue(trade, lookup.marketDataView(marketData));
    }

    /**
     * 计算单一市场数据集的现值。
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the present value
     */
    public CurrencyAmount presentValue(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.presentValue(trade, ratesProvider);
    }

    //-------------------------------------------------------------------------

    /**
     * Calculates present value sensitivity across one or more scenarios.
     * <p>
     * This is the sensitivity of
     * {@linkplain #presentValue(ResolvedTermDepositTrade, RatesMarketDataLookup, ScenarioMarketData) present value}
     * to a one basis point shift in the calibrated curves.
     * The result is the sum of the sensitivities of all affected curves.
     *
     * @param trade      the trade
     * @param lookup     the lookup used to query the market data
     * @param marketData the market data
     * @return the present value sensitivity, one entry per scenario
     */
    public MultiCurrencyScenarioArray pv01CalibratedSum(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.pv01CalibratedSum(trade, lookup.marketDataView(marketData));
    }

    /**
     * Calculates present value sensitivity for a single set of market data.
     * <p>
     * This is the sensitivity of
     * {@linkplain #presentValue(ResolvedTermDepositTrade, RatesMarketDataLookup, ScenarioMarketData) present value}
     * to a one basis point shift in the calibrated curves.
     * The result is the sum of the sensitivities of all affected curves.
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the present value sensitivity
     */
    public MultiCurrencyAmount pv01CalibratedSum(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.pv01CalibratedSum(trade, ratesProvider);
    }

    //-------------------------------------------------------------------------

    /**
     * Calculates present value sensitivity across one or more scenarios.
     * <p>
     * This is the sensitivity of
     * {@linkplain #presentValue(ResolvedTermDepositTrade, RatesMarketDataLookup, ScenarioMarketData) present value}
     * to a one basis point shift in the calibrated curves.
     * The result is provided for each affected curve and currency, bucketed by curve node.
     *
     * @param trade      the trade
     * @param lookup     the lookup used to query the market data
     * @param marketData the market data
     * @return the present value sensitivity, one entry per scenario
     */
    public ScenarioArray<CurrencyParameterSensitivities> pv01CalibratedBucketed(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.pv01CalibratedBucketed(trade, lookup.marketDataView(marketData));
    }

    /**
     * Calculates present value sensitivity for a single set of market data.
     * <p>
     * This is the sensitivity of
     * {@linkplain #presentValue(ResolvedTermDepositTrade, RatesMarketDataLookup, ScenarioMarketData) present value}
     * to a one basis point shift in the calibrated curves.
     * The result is provided for each affected curve and currency, bucketed by curve node.
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the present value sensitivity
     */
    public CurrencyParameterSensitivities pv01CalibratedBucketed(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.pv01CalibratedBucketed(trade, ratesProvider);
    }

    //-------------------------------------------------------------------------

    /**
     * Calculates present value sensitivity across one or more scenarios.
     * <p>
     * This is the sensitivity of
     * {@linkplain #presentValue(ResolvedTermDepositTrade, RatesMarketDataLookup, ScenarioMarketData) present value}
     * to a one basis point shift in the market quotes used to calibrate the curves.
     * The result is the sum of the sensitivities of all affected curves.
     *
     * @param trade      the trade
     * @param lookup     the lookup used to query the market data
     * @param marketData the market data
     * @return the present value sensitivity, one entry per scenario
     */
    public MultiCurrencyScenarioArray pv01MarketQuoteSum(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.pv01MarketQuoteSum(trade, lookup.marketDataView(marketData));
    }

    /**
     * Calculates present value sensitivity for a single set of market data.
     * <p>
     * This is the sensitivity of
     * {@linkplain #presentValue(ResolvedTermDepositTrade, RatesMarketDataLookup, ScenarioMarketData) present value}
     * to a one basis point shift in the market quotes used to calibrate the curves.
     * The result is the sum of the sensitivities of all affected curves.
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the present value sensitivity
     */
    public MultiCurrencyAmount pv01MarketQuoteSum(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.pv01MarketQuoteSum(trade, ratesProvider);
    }

    //-------------------------------------------------------------------------

    /**
     * Calculates present value sensitivity across one or more scenarios.
     * <p>
     * This is the sensitivity of
     * {@linkplain #presentValue(ResolvedTermDepositTrade, RatesMarketDataLookup, ScenarioMarketData) present value}
     * to a one basis point shift in the market quotes used to calibrate the curves.
     * The result is provided for each affected curve and currency, bucketed by curve node.
     *
     * @param trade      the trade
     * @param lookup     the lookup used to query the market data
     * @param marketData the market data
     * @return the present value sensitivity, one entry per scenario
     */
    public ScenarioArray<CurrencyParameterSensitivities> pv01MarketQuoteBucketed(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.pv01MarketQuoteBucketed(trade, lookup.marketDataView(marketData));
    }

    /**
     * Calculates present value sensitivity for a single set of market data.
     * <p>
     * This is the sensitivity of
     * {@linkplain #presentValue(ResolvedTermDepositTrade, RatesMarketDataLookup, ScenarioMarketData) present value}
     * to a one basis point shift in the market quotes used to calibrate the curves.
     * The result is provided for each affected curve and currency, bucketed by curve node.
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the present value sensitivity
     */
    public CurrencyParameterSensitivities pv01MarketQuoteBucketed(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.pv01MarketQuoteBucketed(trade, ratesProvider);
    }

    //-------------------------------------------------------------------------

    /**
     * Calculates par rate across one or more scenarios.
     *
     * @param trade      the trade
     * @param lookup     the lookup used to query the market data
     * @param marketData the market data
     * @return the par rate, one entry per scenario
     */
    public DoubleScenarioArray parRate(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.parRate(trade, lookup.marketDataView(marketData));
    }

    /**
     * Calculates par rate for a single set of market data.
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the par rate
     */
    public double parRate(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.parRate(trade, ratesProvider);
    }

    //-------------------------------------------------------------------------

    /**
     * Calculates par spread across one or more scenarios.
     *
     * @param trade      the trade
     * @param lookup     the lookup used to query the market data
     * @param marketData the market data
     * @return the par spread, one entry per scenario
     */
    public DoubleScenarioArray parSpread(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.parSpread(trade, lookup.marketDataView(marketData));
    }

    /**
     * Calculates par spread for a single set of market data.
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the par spread
     */
    public double parSpread(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.parSpread(trade, ratesProvider);
    }

    //-------------------------------------------------------------------------

    /**
     * Calculates currency exposure across one or more scenarios.
     * <p>
     * The currency risk, expressed as the equivalent amount in each currency.
     *
     * @param trade      the trade
     * @param lookup     the lookup used to query the market data
     * @param marketData the market data
     * @return the currency exposure, one entry per scenario
     */
    public MultiCurrencyScenarioArray currencyExposure(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.currencyExposure(trade, lookup.marketDataView(marketData));
    }

    /**
     * Calculates currency exposure for a single set of market data.
     * <p>
     * The currency risk, expressed as the equivalent amount in each currency.
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the currency exposure
     */
    public MultiCurrencyAmount currencyExposure(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.currencyExposure(trade, ratesProvider);
    }

    //-------------------------------------------------------------------------

    /**
     * Calculates current cash across one or more scenarios.
     * <p>
     * The sum of all cash flows paid on the valuation date.
     *
     * @param trade      the trade
     * @param lookup     the lookup used to query the market data
     * @param marketData the market data
     * @return the current cash, one entry per scenario
     */
    public CurrencyScenarioArray currentCash(
            ResolvedTermDepositTrade trade,
            RatesMarketDataLookup lookup,
            ScenarioMarketData marketData) {

        return calc.currentCash(trade, lookup.marketDataView(marketData));
    }

    /**
     * Calculates current cash for a single set of market data.
     * <p>
     * The sum of all cash flows paid on the valuation date.
     *
     * @param trade         the trade
     * @param ratesProvider the market data
     * @return the current cash
     */
    public CurrencyAmount currentCash(
            ResolvedTermDepositTrade trade,
            RatesProvider ratesProvider) {

        return calc.currentCash(trade, ratesProvider);
    }

}
