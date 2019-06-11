/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.runner;

import com.opengamma.strata.basics.CalculationTarget;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.calc.CalculationRunner;
import com.opengamma.strata.calc.Measure;
import com.opengamma.strata.calc.ReportingCurrency;
import com.opengamma.strata.collect.result.Result;
import com.opengamma.strata.data.scenario.ScenarioArray;
import com.opengamma.strata.data.scenario.ScenarioFxConvertible;
import com.opengamma.strata.data.scenario.ScenarioMarketData;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 用于计算度量值（measures）的所有计算函数的主界面。
 * <p>
 * 此接口的实现提供了使用一组或多组市场数据（场景）为目标（交易）计算一个或多个度量值（measures）的能力。
 * The methods of the function allow the {@link CalculationRunner} to correctly invoke the function:
 * <ul>
 * <li>{@link #targetType()}
 * - 函数应用到的目标类型
 * <li>{@link #supportedMeasures()}
 * - 可计算的一组度量值
 * <li>{@link #naturalCurrency(CalculationTarget, ReferenceData)}
 * - 目标的“自然”货币
 * <li>{@link #requirements(CalculationTarget, Set, CalculationParameters, ReferenceData)}
 * - 执行计算的市场数据要求
 * <li>{@link #calculate(CalculationTarget, Set, CalculationParameters, ScenarioMarketData, ReferenceData)}
 * - 执行计算
 * </ul>
 * <p>
 * 如果任何计算值包含任何货币金额并实现了{@link ScenarioFxConvertible}，计算运行程序将自动将金额转换为报告货币。
 *
 * @param <T> 由该函数处理的目标类型
 */
public interface CalculationFunction<T extends CalculationTarget> {

    /**
     * 获取此函数应用的目标类型CalculationFunction接口中定义的T的类类型。
     * <p>
     * 目标类型通常是一个具体的类。如XXXXX.Class
     *
     * @return the target type
     */
    public abstract Class<T> targetType();

    /**
     * 返回函数可以计算的一组度量值。
     *
     * @return the read-only set of measures that the function can calculate（函数可以计算的只读度量集）
     */
    public abstract Set<Measure> supportedMeasures();

    /**
     * 返回一个标识符，该标识符应该是唯一标识指定的目标。
     * <p>
     * 错误消息中使用这个标识符识别目标。这通常应该被覆盖以提供合适的标识符。
     * 例如，如果目标是交易，通常会有一个可用的交易标识符。
     * <p>
     * This method must not throw an exception.
     *
     * @param target 要计算的目标
     * @return 目标的标识符，如果没有合适的标识符可用，则为空
     */
    public default Optional<String> identifier(T target) {
        return Optional.empty();
    }

    /**
     * 返回指定目标的“自然”货币。
     * <p>
     * 如果使用{@link ReportingCurrency#NATURAL}请求“自然”报告货币，则将货币金额转换为该货币。
     * 大多数目标货币都是“自然”货币，例如FRA货币或外汇远期的基准货币。
     * <p>
     * 要求所有返回货币可兑换措施的函数必须为每笔交易选择一种“自然”货币。
     * 选择必须是一致的，而不是随机的，因为相同的交易必须返回相同的货币。
     * 这可能涉及从货币对中选择第一货币或基础货币。
     * 只有当函数不处理货币可兑换措施时，才必须抛出异常。
     * <p>
     *
     * @param target  要计算的目标
     * @param refData 计算中使用的参考数据
     * @return the "natural" currency of the target
     * @throws IllegalStateException 如果函数没有计算货币可兑换的度量
     */
    public abstract Currency naturalCurrency(T target, ReferenceData refData);

    /**
     * 确定此函数执行计算所需的市场数据。
     * <p>
     * 应指定{@code calculate}方法所需的市场数据。
     * <p>
     * 一组度量可以包括此函数不支持的度量。
     *
     * @param target     要计算的目标
     * @param measures   要计算的一组度量
     * @param parameters 影响执行计算的参数
     * @param refData    计算中使用的参考数据
     * @return 指定函数执行计算所需的市场数据的需求
     */
    public abstract FunctionRequirements requirements(
            T target,
            Set<Measure> measures,
            CalculationParameters parameters,
            ReferenceData refData);

    /**
     * 使用多个市场数据集计算目标的多个度量值。
     * <p>
     * 一组度量必须只包含函数支持的度量，就像{@link #supportedMeasures()}返回的那样。
     * 市场数据必须至少提供{{@link #requirements(CalculationTarget, Set, CalculationParameters, ReferenceData)}
     * 要求的数据集。
     * <p>
     * 这个方法的结果通常是{@link ScenarioArray}的一个实例，它处理每个场景都有一个计算值的常见情况。
     * 但是，函数也可以计算聚合的结果，
     * 例如所有场景的最大值或最小值，在这种情况下，结果不会实现{@code ScenarioArray}。
     *
     * @param target     要计算的目标
     * @param measures   要计算的一组度量
     * @param parameters 影响执行计算的参数
     * @param marketData 将多情景的市场数据用于计算
     * @param refData    计算中使用的参考数据
     * @return 计算值的只读映射，key为Measure
     */
    public abstract Map<Measure, Result<?>> calculate(
            T target,
            Set<Measure> measures,
            CalculationParameters parameters,
            ScenarioMarketData marketData,
            ReferenceData refData);

}
