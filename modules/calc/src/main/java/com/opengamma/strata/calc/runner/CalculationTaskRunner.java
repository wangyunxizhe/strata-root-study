/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.runner;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.calc.CalculationRules;
import com.opengamma.strata.calc.CalculationRunner;
import com.opengamma.strata.calc.Results;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.data.scenario.ScenarioMarketData;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 提供运行计算任务的能力的组件。
 * <p>
 * 此接口是{@link CalculationRunner}的下级对应。
 * 它提供了基于{@link CalculationTasks}计算结果的能力。
 * 除非需要优化，{@code CalculationRunner}是一个更简单的入口点。
 * <p>
 * 运行程序的目的是生成一个结果网格，每个目标有一行，每个度量有一列。
 * 定义结果网格的目标和列使用{@code CalculationTasks}的实例传入。
 * <p>
 * 使用{@linkplain CalculationTasks#of(CalculationRules, List, List, ReferenceData) static factory method}.
 * 获取{@code CalculationTasks}的实例。
 * 它由{@code CalculationTask}实例列表组成，其中每个任务实例对应于结果网格中的单个单元格。
 * 当为一组交易和度量创建{@code CalculationTask}实例时，会执行一些一次性初始化。
 * 提供对该实例的访问权限可使初始化发生一次，如果对同一组交易和度量执行许多不同的计算，这可能是性能优化。
 * <p>
 * 一旦获得，{@code CalculationTasks}实例，就可以用来计算结果。
 * 四种“计算”方法处理单一市场数据与情景市场数据以及同步与异步数据的组合。
 * <p>
 * 计算运行程序通常是使用此接口上的静态方法获取的。该实例包含执行器线程池，因此应注意确保正确管理线程池。
 * For example, try-with-resources could be used:
 * <pre>
 *  try (CalculationTaskRunner runner = CalculationTaskRunner.ofMultiThreaded()) {
 *    // use the runner
 *  }
 * </pre>
 */
public interface CalculationTaskRunner extends AutoCloseable {

    /**
     * 创建能够执行计算的标准多线程计算任务运行程序。
     * <p>
     * 这个工厂根据可用处理器的数量创建一个执行器。
     * 建议使用try-with-resources管理运行程序：
     * <pre>
     *  try (CalculationTaskRunner runner = CalculationTaskRunner.ofMultiThreaded()) {
     *    // use the runner
     *  }
     * </pre>
     *
     * @return 计算任务运行程序
     */
    public static CalculationTaskRunner ofMultiThreaded() {
        return DefaultCalculationTaskRunner.ofMultiThreaded();
    }

    /**
     * 创建能够执行计算的计算任务运行程序，指定执行者。
     * <p>
     * 呼叫者有责任管理执行者的生命周期。
     *
     * @param executor the executor to use
     * @return 计算任务运行程序
     */
    public static CalculationTaskRunner of(ExecutorService executor) {
        return DefaultCalculationTaskRunner.of(executor);
    }

    //-------------------------------------------------------------------------

    /**
     * 对一组市场数据执行计算。
     * <p>
     * 这将返回基于指定任务和市场数据的结果网格。
     * 网格将包含每个目标的一行和每个度量的一列。
     * <p>
     * 如果此方法被阻止时线程被中断，计算将停止，并返回一个结果，指示失败的任务，并设置中断标志。
     * 对于其他控件，请使用 {@link #calculateAsync(CalculationTasks, MarketData, ReferenceData, CalculationListener)}.
     *
     * @param tasks      要调用的计算任务
     * @param marketData 计算中使用的市场数据
     * @param refData    计算中使用的参考数据
     * @return 基于任务和市场数据的计算结果网格
     */
    public abstract Results calculate(
            CalculationTasks tasks,
            MarketData marketData,
            ReferenceData refData);

    /**
     * 对单个市场数据集异步执行计算，在每次计算完成时调用侦听器。
     * <p>
     * 此方法要求侦听器组装结果，但在计算聚合结果时，它可以提高内存效率。
     * 如果单个结果在合并到聚合中之后被丢弃，则可以对它们进行垃圾收集。
     *
     * @param tasks      要调用的计算任务
     * @param marketData 计算中使用的市场数据
     * @param refData    计算中使用的参考数据
     * @param listener   计算单个结果时调用的侦听器
     */
    public abstract void calculateAsync(
            CalculationTasks tasks,
            MarketData marketData,
            ReferenceData refData,
            CalculationListener listener);

    //-------------------------------------------------------------------------

    /**
     * 为多个方案执行计算，每个方案都有一组不同的市场数据。
     * <p>
     * 这将返回基于指定任务和市场数据的结果网格。
     * 该网格将为每个目标包含一行，为每个度量值包含一列。每个单元格将包含多个结果，每个方案一个结果。
     * <p>
     * 如果此方法被阻止时线程被中断，计算将停止，并返回一个结果，指示失败的任务，并设置中断标志。
     * 对于其他控件，请使用
     * {@link #calculateMultiScenarioAsync(CalculationTasks, ScenarioMarketData, ReferenceData, CalculationListener)}.
     *
     * @param tasks      要调用的计算任务
     * @param marketData 计算中使用的市场数据
     * @param refData    计算中使用的参考数据
     * @return 基于任务和市场数据的计算结果网格
     */
    public abstract Results calculateMultiScenario(
            CalculationTasks tasks,
            ScenarioMarketData marketData,
            ReferenceData refData);

    /**
     * 为多个方案异步执行计算，每个方案都有一组不同的市场数据，每次计算完成时调用侦听器。
     * <p>
     * 此方法要求侦听器组装结果，但在计算聚合结果时，它可以提高内存效率。
     * 如果单个结果在合并到聚合中之后被丢弃，则可以对它们进行垃圾收集。
     *
     * @param tasks      要调用的计算任务
     * @param marketData 计算中使用的市场数据
     * @param refData    计算中使用的参考数据
     * @param listener   计算单个结果时调用的侦听器
     */
    public abstract void calculateMultiScenarioAsync(
            CalculationTasks tasks,
            ScenarioMarketData marketData,
            ReferenceData refData,
            CalculationListener listener);

    //-------------------------------------------------------------------------

    /**
     * Closes any resources held by the component.
     * <p>
     * If the component holds an {@link ExecutorService}, this method will typically
     * call {@link ExecutorService#shutdown()}.
     */
    @Override
    public abstract void close();

}
