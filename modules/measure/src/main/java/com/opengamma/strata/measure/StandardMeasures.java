/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure;

import com.opengamma.strata.calc.ImmutableMeasure;
import com.opengamma.strata.calc.Measure;

/**
 * Strata计算用的标准度量集。
 */
final class StandardMeasures {

    // 现值，货币换算
    public static final Measure PRESENT_VALUE = ImmutableMeasure.of("PresentValue");
    // 解释现值，不进行货币换算
    public static final Measure EXPLAIN_PRESENT_VALUE = ImmutableMeasure.of("ExplainPresentValue", false);

    // PV01校准值
    public static final Measure PV01_CALIBRATED_SUM = ImmutableMeasure.of("PV01CalibratedSum");
    // PV01 calibrated bucketed
    public static final Measure PV01_CALIBRATED_BUCKETED = ImmutableMeasure.of("PV01CalibratedBucketed");
    // PV01市场报价总额
    public static final Measure PV01_MARKET_QUOTE_SUM = ImmutableMeasure.of("PV01MarketQuoteSum");
    // PV01 market quote bucketed
    public static final Measure PV01_MARKET_QUOTE_BUCKETED = ImmutableMeasure.of("PV01MarketQuoteBucketed");

    //-------------------------------------------------------------------------
    // 应计利息
    public static final Measure ACCRUED_INTEREST = ImmutableMeasure.of("AccruedInterest");
    // 现金流
    public static final Measure CASH_FLOWS = ImmutableMeasure.of("CashFlows");
    // currency exposure, with no currency conversion
    public static final Measure CURRENCY_EXPOSURE = ImmutableMeasure.of("CurrencyExposure", false);
    // 现款
    public static final Measure CURRENT_CASH = ImmutableMeasure.of("CurrentCash");
    // 远期外汇汇率
    public static final Measure FORWARD_FX_RATE = ImmutableMeasure.of("ForwardFxRate", false);
    // 分支现值
    public static final Measure LEG_PRESENT_VALUE = ImmutableMeasure.of("LegPresentValue");
    // 分支初始概念
    public static final Measure LEG_INITIAL_NOTIONAL = ImmutableMeasure.of("LegInitialNotional");
    // 票面利率，即不需要货币兑换的十进制汇率。
    public static final Measure PAR_RATE = ImmutableMeasure.of("ParRate", false);
    // 票面利差，是不需要货币兑换的十进制汇率。
    public static final Measure PAR_SPREAD = ImmutableMeasure.of("ParSpread", false);
    // 已解决的目标
    public static final Measure RESOLVED_TARGET = ImmutableMeasure.of("ResolvedTarget", false);
    // 单价，即使是指一种货币，也被视为一个简单的十进制数。
    public static final Measure UNIT_PRICE = ImmutableMeasure.of("UnitPrice", false);

    //-------------------------------------------------------------------------
    // semi-parallel gamma bucketed PV01
    public static final Measure PV01_SEMI_PARALLEL_GAMMA_BUCKETED = ImmutableMeasure.of("PV01SemiParallelGammaBucketed");
    // single-node gamma bucketed PV01
    public static final Measure PV01_SINGLE_NODE_GAMMA_BUCKETED = ImmutableMeasure.of("PV01SingleNodeGammaBucketed");

}
