/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure;

import com.opengamma.strata.basics.Resolvable;
import com.opengamma.strata.calc.Measure;
import com.opengamma.strata.data.scenario.ScenarioArray;

/**
 * Strata计算用的标准度量集合，都是跟金融相关的计量单位。
 * 总结：就是想计算什么就在这个类中找
 * 对应的配置文件位置：
 * modules\measure\src\main\resources\META-INF\com\opengamma\strata\config\base2\Measure.ini
 * <p>
 * 度量值标识所需的计算结果。例如现值、票面利率或价差。
 * <p>
 * 请注意，并非所有Measures对象都适用于所有目标。
 */
public final class Measures {

    /**
     * 表示计算目标的现值的度量值。
     * <p>
     * 结果是以报告货币表示的单一货币金额。
     */
    public static final Measure PRESENT_VALUE = Measure.of(StandardMeasures.PRESENT_VALUE.getName());
    /**
     * 表示目标现值计算细分的度量。
     * <p>
     * 货币金额不进行货币转换。
     */
    public static final Measure EXPLAIN_PRESENT_VALUE = Measure.of(StandardMeasures.EXPLAIN_PRESENT_VALUE.getName());

    //-------------------------------------------------------------------------
    /**
     * 在计算目标上表示校准和PV01的测量值。
     * <p>
     * 这是当前值对校准数据结构中一个基点位移的敏感性。结果是所有受影响曲线的灵敏度之和。以报告货币表示。
     */
    public static final Measure PV01_CALIBRATED_SUM = Measure.of(StandardMeasures.PV01_CALIBRATED_SUM.getName());
    /**
     * 测量在计算目标上表示校准的桶形PV01。
     * <p>
     * 这是当前值对校准数据结构中一个基点位移的敏感性。
     * 结果被提供给每个受影响的曲线和货币，由参数来表示。它用报告货币表示。
     */
    public static final Measure PV01_CALIBRATED_BUCKETED = Measure.of(StandardMeasures.PV01_CALIBRATED_BUCKETED.getName());
    /**
     * 在计算目标上表示市场报价总和PV01。
     * <p>
     * 这是现值对用于校准数据结构的市场报价中一个基点变化的敏感性。
     * 其结果是所有受影响的曲线的灵敏度之和。它用报告货币表示。
     */
    public static final Measure PV01_MARKET_QUOTE_SUM = Measure.of(StandardMeasures.PV01_MARKET_QUOTE_SUM.getName());
    /**
     * 在计算目标上代表市场报价PV01的度量。
     * <p>
     * 这是现值对用于校准数据结构的市场报价中一个基点变化的敏感性。
     * 结果被提供给每个受影响的曲线和货币，由参数来表示。它用报告货币表示。
     */
    public static final Measure PV01_MARKET_QUOTE_BUCKETED = Measure.of(StandardMeasures.PV01_MARKET_QUOTE_BUCKETED.getName());

    //-------------------------------------------------------------------------
    /**
     * 表示计算目标的PAR率的度量。
     */
    public static final Measure PAR_RATE = Measure.of(StandardMeasures.PAR_RATE.getName());
    /**
     * 表示计算目标的PAR扩展的度量。
     */
    public static final Measure PAR_SPREAD = Measure.of(StandardMeasures.PAR_SPREAD.getName());

    //-------------------------------------------------------------------------
    /**
     * 表示计算目标的每一条分支的当前值的度量。
     * <p>
     * 结果以报告货币表示。
     */
    public static final Measure LEG_PRESENT_VALUE = Measure.of(StandardMeasures.LEG_PRESENT_VALUE.getName());
    /**
     * 表示计算目标每一条分支的初始名义金额的度量。
     * <p>
     * 结果以报告货币表示。
     */
    public static final Measure LEG_INITIAL_NOTIONAL = Measure.of(StandardMeasures.LEG_INITIAL_NOTIONAL.getName());
    /**
     * 表示计算目标的应计利息的度量值。
     */
    public static final Measure ACCRUED_INTEREST = Measure.of(StandardMeasures.ACCRUED_INTEREST.getName());
    /**
     * 衡量计算目标的现金流量。
     * <p>
     * 现金流提供目标公司付款的详细信息。结果以报告货币表示。
     */
    public static final Measure CASH_FLOWS = Measure.of(StandardMeasures.CASH_FLOWS.getName());
    /**
     * 表示计算目标的货币风险的度量。
     * <p>
     * 货币风险是货币风险，以每种货币的等值金额表示。
     * 计算值不转换为报告货币，如果目标包含多种货币，则可能包含多种货币的值。
     */
    public static final Measure CURRENCY_EXPOSURE = Measure.of(StandardMeasures.CURRENCY_EXPOSURE.getName());
    /**
     * 表示计算目标当前现金的度量。
     * <p>
     * 当前现金是在估价日支付的所有现金流的总和。结果以报告货币表示。
     */
    public static final Measure CURRENT_CASH = Measure.of(StandardMeasures.CURRENT_CASH.getName());
    /**
     * 表示计算目标的正向FX速率的度量。
     */
    public static final Measure FORWARD_FX_RATE = Measure.of(StandardMeasures.FORWARD_FX_RATE.getName());
    /**
     * 表示仪器单价的计量单位。
     * <p>
     * 这是使用分层市场约定的单个证券单位的价格。
     * 价格表示为{@code double}，即使它实际上是一个货币金额。
     */
    public static final Measure UNIT_PRICE = Measure.of(StandardMeasures.UNIT_PRICE.getName());
    /**
     * 表示计算目标的解析形式的度量。
     * <p>
     * 许多计算目标有一个优化的{@linkplain Resolvable resolved}形式，用于定价。
     * This measure allows the resolved form to be obtained.
     * 由于目标对于所有场景都是相同的，因此结果不包含在{@link ScenarioArray}中。
     */
    public static final Measure RESOLVED_TARGET = Measure.of(StandardMeasures.RESOLVED_TARGET.getName());

    //-------------------------------------------------------------------------
    private Measures() {
    }

}
