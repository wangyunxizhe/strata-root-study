/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc;

import com.opengamma.strata.basics.CalculationTarget;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.calc.runner.CalculationListener;
import com.opengamma.strata.calc.runner.CalculationTaskRunner;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.data.scenario.ScenarioMarketData;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 提供对多个目标、度量和方案执行计算的能力的组件。
 * <p>
 * strata-pricer模块提供了计算单笔交易结果的能力，
 * 单指标和单套市场数据。 {@code CalculationRunner}
 * 提供计算多个交易、多个度量和多组市场数据的结果的能力
 * <p>
 * 一旦获得{@code CalculationRunner} 实例，就可以使用它来计算结果。
 * 这四种“计算”方法处理单对情景市场数据和同步对异步数据的组合。
 * <p>
 * 计算运行程序通常是使用该接口上的静态方法获得的。
 * 实例包含执行器线程池，因此应注意确保正确管理线程池。
 * 例如，可以使用 try-with-resources :
 * <pre>
 *  try (CalculationRunner runner = CalculationRunner.ofMultiThreaded()) {
 *    // use the runner
 *  }
 * </pre>
 */
public interface CalculationRunner extends AutoCloseable {

    /**
     * 创建能够执行计算的标准多线程计算运行程序。
     * <p>
     * 这个工厂根据可用处理器的数量创建一个执行器。
     * 建议使用Try-with-Resources管理运行程序：
     * <pre>
     *  try (CalculationRunner runner = CalculationRunner.ofMultiThreaded()) {
     *    // use the runner
     *  }
     * </pre>
     *
     * @return the calculation runner
     */
    public static CalculationRunner ofMultiThreaded() {
        return DefaultCalculationRunner.ofMultiThreaded();
    }

    /**
     * 创建能够执行计算的计算运行程序，指定执行器。
     * <p>
     * 呼叫者有责任管理执行者的生命周期。
     *
     * @param executor the executor to use
     * @return the calculation runner
     */
    public static CalculationRunner of(ExecutorService executor) {
        return DefaultCalculationRunner.of(executor);
    }

    //-------------------------------------------------------------------------

    /**
     * 对一组市场数据执行计算。
     * <p>
     * 这将返回基于指定目标、列、规则和市场数据的结果网格。
     * 网格将包含每个目标的一行和每个度量的一列。
     *
     * @param calculationRules 定义如何执行计算的规则
     * @param targets          计算度量值的目标
     * @param columns          将要计算的列的配置，包括度量值和任何列特定的重写
     * @param marketData       计算中使用的市场数据
     * @param refData          计算中使用的参考数据
     * @return 基于目标和列的计算结果网格
     */
    public abstract Results calculate(
            CalculationRules calculationRules,
            List<? extends CalculationTarget> targets,
            List<Column> columns,
            MarketData marketData,
            ReferenceData refData);

    /**
     * 对单个市场数据集异步执行计算，在每次计算完成时调用侦听器。
     * <p>
     * 此方法要求侦听器组装结果，但在计算聚合结果时，它可以提高内存效率。
     * 如果单个结果在合并到聚合中之后被丢弃，则可以对它们进行垃圾收集。
     *
     * @param calculationRules 定义如何执行计算的规则
     * @param targets          计算度量值的目标
     * @param columns          将要计算的列的配置，包括度量值和任何列特定的重写
     * @param marketData       计算中使用的市场数据
     * @param refData          计算中使用的参考数据
     * @param listener         计算单个结果时调用的侦听器
     */
    public abstract void calculateAsync(
            CalculationRules calculationRules,
            List<? extends CalculationTarget> targets,
            List<Column> columns,
            MarketData marketData,
            ReferenceData refData,
            CalculationListener listener);

    //-------------------------------------------------------------------------

    /**
     * 对多个方案执行计算，每个方案都有一组不同的市场数据。
     * <p>
     * 这将返回基于指定目标、列、规则和市场数据的结果网格。
     * 网格将包含每个目标的一行和每个度量的一列。
     *
     * @param calculationRules 定义如何执行计算的规则
     * @param targets          计算度量值的目标
     * @param columns          将要计算的列的配置，包括度量值和任何列特定的重写
     * @param marketData       计算中使用的市场数据
     * @param refData          计算中使用的参考数据
     * @return 基于目标和列的计算结果网格
     */
    public abstract Results calculateMultiScenario(
            CalculationRules calculationRules,
            List<? extends CalculationTarget> targets,
            List<Column> columns,
            ScenarioMarketData marketData,
            ReferenceData refData);

    /**
     * 为多个方案异步执行计算，每个方案都有一组不同的市场数据，并在每次计算完成时调用侦听器。
     * <p>
     * 此方法要求侦听器组装结果，但在计算聚合结果时，它可以提高内存效率。
     * 如果单个结果在合并到聚合中之后被丢弃，则可以对它们进行垃圾收集。
     *
     * @param calculationRules 定义如何执行计算的规则
     * @param targets          计算度量值的目标
     * @param columns          将要计算的列的配置，包括度量值和任何列特定的重写
     * @param marketData       计算中使用的市场数据
     * @param refData          计算中使用的参考数据
     * @param listener         计算单个结果时调用的侦听器
     */
    public abstract void calculateMultiScenarioAsync(
            CalculationRules calculationRules,
            List<? extends CalculationTarget> targets,
            List<Column> columns,
            ScenarioMarketData marketData,
            ReferenceData refData,
            CalculationListener listener);

    //-------------------------------------------------------------------------

    /**
     * 获取基础任务运行程序。
     * <p>
     * 在大多数情况下，此运行程序将使用{@link CalculationTaskRunner}的实例来实现。
     * 该接口提供较低级别的API，如果重复进行类似的计算，则可以进行优化。
     * <p>
     *
     * @return 底层任务运行程序
     * @throws UnsupportedOperationException 如果未提供对任务运行程序的访问权限
     */
    public abstract CalculationTaskRunner getTaskRunner();

    //-------------------------------------------------------------------------

    /**
     * 关闭组件持有的所有资源。
     * <p>
     * 如果组件持有 {@link ExecutorService}, 此方法通常会调用{@link ExecutorService#shutdown()}.
     */
    @Override
    public abstract void close();

}
