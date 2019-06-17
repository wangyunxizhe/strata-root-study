/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure.deposit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.calc.Measure;
import com.opengamma.strata.calc.runner.CalculationFunction;
import com.opengamma.strata.calc.runner.CalculationParameters;
import com.opengamma.strata.calc.runner.FunctionRequirements;
import com.opengamma.strata.collect.result.FailureReason;
import com.opengamma.strata.collect.result.Result;
import com.opengamma.strata.data.scenario.ScenarioMarketData;
import com.opengamma.strata.measure.Measures;
import com.opengamma.strata.measure.rate.RatesMarketDataLookup;
import com.opengamma.strata.measure.rate.RatesScenarioMarketData;
import com.opengamma.strata.product.deposit.ResolvedTermDepositTrade;
import com.opengamma.strata.product.deposit.TermDeposit;
import com.opengamma.strata.product.deposit.TermDepositTrade;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 定期存款的计算方法类：为每个场景集执行单个{@code TermDepositTrade}的计算。
 * <p>
 * 这使用了标准的折现计算方法。
 * 必须指定{@link RatesMarketDataLookup}的实例。支持的Measure包括：
 * <ul>
 * <li>{@linkplain Measures#PRESENT_VALUE Present value}
 * <li>{@linkplain Measures#PV01_CALIBRATED_SUM PV01 calibrated sum}
 * <li>{@linkplain Measures#PV01_CALIBRATED_BUCKETED PV01 calibrated bucketed}
 * <li>{@linkplain Measures#PV01_MARKET_QUOTE_SUM PV01 market quote sum}
 * <li>{@linkplain Measures#PV01_MARKET_QUOTE_BUCKETED PV01 market quote bucketed}
 * <li>{@linkplain Measures#PAR_RATE Par rate}
 * <li>{@linkplain Measures#PAR_SPREAD Par spread}
 * <li>{@linkplain Measures#CURRENCY_EXPOSURE Currency exposure}
 * <li>{@linkplain Measures#CURRENT_CASH Current cash}
 * <li>{@linkplain Measures#RESOLVED_TARGET Resolved trade}
 * </ul>
 */
public class TermDepositTradeCalculationFunction
        implements CalculationFunction<TermDepositTrade> {

    /**
     * The calculations by measure.
     */
    private static final ImmutableMap<Measure, SingleMeasureCalculation> CALCULATORS =
            ImmutableMap.<Measure, SingleMeasureCalculation>builder()
                    .put(Measures.PRESENT_VALUE, TermDepositMeasureCalculations.DEFAULT::presentValue)
                    .put(Measures.PV01_CALIBRATED_SUM, TermDepositMeasureCalculations.DEFAULT::pv01CalibratedSum)
                    .put(Measures.PV01_CALIBRATED_BUCKETED, TermDepositMeasureCalculations.DEFAULT::pv01CalibratedBucketed)
                    .put(Measures.PV01_MARKET_QUOTE_SUM, TermDepositMeasureCalculations.DEFAULT::pv01MarketQuoteSum)
                    .put(Measures.PV01_MARKET_QUOTE_BUCKETED, TermDepositMeasureCalculations.DEFAULT::pv01MarketQuoteBucketed)
                    .put(Measures.PAR_RATE, TermDepositMeasureCalculations.DEFAULT::parRate)
                    .put(Measures.PAR_SPREAD, TermDepositMeasureCalculations.DEFAULT::parSpread)
                    .put(Measures.CURRENCY_EXPOSURE, TermDepositMeasureCalculations.DEFAULT::currencyExposure)
                    .put(Measures.CURRENT_CASH, TermDepositMeasureCalculations.DEFAULT::currentCash)
                    .put(Measures.RESOLVED_TARGET, (rt, smd) -> rt)
                    .build();

    private static final ImmutableSet<Measure> MEASURES = CALCULATORS.keySet();

    /**
     * Creates an instance.
     */
    public TermDepositTradeCalculationFunction() {
    }

    //-------------------------------------------------------------------------
    @Override
    public Class<TermDepositTrade> targetType() {
        return TermDepositTrade.class;
    }

    @Override
    public Set<Measure> supportedMeasures() {
        return MEASURES;
    }

    @Override
    public Optional<String> identifier(TermDepositTrade target) {
        return target.getInfo().getId().map(id -> id.toString());
    }

    @Override
    public Currency naturalCurrency(TermDepositTrade trade, ReferenceData refData) {
        return trade.getProduct().getCurrency();
    }

    //-------------------------------------------------------------------------
    @Override
    public FunctionRequirements requirements(
            TermDepositTrade trade,
            Set<Measure> measures,
            CalculationParameters parameters,
            ReferenceData refData) {

        // 从产品中提取数据
        TermDeposit product = trade.getProduct();
        Currency currency = product.getCurrency();

        // 使用lookup来构建需求
        RatesMarketDataLookup ratesLookup = parameters.getParameter(RatesMarketDataLookup.class);
        return ratesLookup.requirements(currency);
    }

    //-------------------------------------------------------------------------
    @Override
    public Map<Measure, Result<?>> calculate(
            TermDepositTrade trade,
            Set<Measure> measures,
            CalculationParameters parameters,
            ScenarioMarketData scenarioMarketData,
            ReferenceData refData) {

        // 一次性解决所有measures和所有场景的trade
        ResolvedTermDepositTrade resolved = trade.resolve(refData);

        // use lookup to query market data
        RatesMarketDataLookup ratesLookup = parameters.getParameter(RatesMarketDataLookup.class);
        RatesScenarioMarketData marketData = ratesLookup.marketDataView(scenarioMarketData);

        // 循环measures，计算一个measure的所有场景
        Map<Measure, Result<?>> results = new HashMap<>();
        for (Measure measure : measures) {
            results.put(measure, calculate(measure, resolved, marketData));
        }
        return results;
    }

    // calculate one measure
    private Result<?> calculate(
            Measure measure,
            ResolvedTermDepositTrade trade,
            RatesScenarioMarketData marketData) {

        SingleMeasureCalculation calculator = CALCULATORS.get(measure);
        if (calculator == null) {
            return Result.failure(FailureReason.UNSUPPORTED, "Unsupported measure for TermDepositTrade: {}", measure);
        }
        return Result.of(() -> calculator.calculate(trade, marketData));
    }

    //-------------------------------------------------------------------------
    @FunctionalInterface
    interface SingleMeasureCalculation {
        public abstract Object calculate(
                ResolvedTermDepositTrade trade,
                RatesScenarioMarketData marketData);
    }

}
