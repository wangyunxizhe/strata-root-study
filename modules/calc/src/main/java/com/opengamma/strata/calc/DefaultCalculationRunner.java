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
import com.opengamma.strata.calc.runner.CalculationTasks;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.data.scenario.ScenarioMarketData;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 默认计算运行程序。
 * <p>
 * 这将委托给{@link CalculationTaskRunner}的实例。
 */
class DefaultCalculationRunner implements CalculationRunner {

    /**
     * 基础任务运行程序。
     */
    private final CalculationTaskRunner taskRunner;

    //-------------------------------------------------------------------------

    /**
     * 创建能够执行计算的标准多线程计算运行程序。
     * <p>
     * 这个工厂根据可用处理器的数量创建一个执行器。
     * 建议使用 try-with-resources 管理运行程序
     * <pre>
     *  try (DefaultCalculationRunner runner = DefaultCalculationRunner.ofMultiThreaded()) {
     *    // use the runner
     *  }
     * </pre>
     *
     * @return the calculation runner
     */
    static DefaultCalculationRunner ofMultiThreaded() {
        return new DefaultCalculationRunner(CalculationTaskRunner.ofMultiThreaded());
    }

    /**
     * 创建能够执行计算的计算运行程序，指定执行器。
     * <p>
     * 呼叫者有责任管理执行者的生命周期。
     *
     * @param executor the executor to use
     * @return the calculation runner
     */
    static DefaultCalculationRunner of(ExecutorService executor) {
        return new DefaultCalculationRunner(CalculationTaskRunner.of(executor));
    }

    //-------------------------------------------------------------------------

    /**
     * 创建指定要使用的基础任务运行程序的实例。
     *
     * @param taskRunner 底层任务运行程序
     */
    DefaultCalculationRunner(CalculationTaskRunner taskRunner) {
        this.taskRunner = ArgChecker.notNull(taskRunner, "taskRunner");
    }

    //-------------------------------------------------------------------------
    @Override
    public Results calculate(
            CalculationRules calculationRules,
            List<? extends CalculationTarget> targets,
            List<Column> columns,
            MarketData marketData,
            ReferenceData refData) {

        CalculationTasks tasks = CalculationTasks.of(calculationRules, targets, columns, refData);
        return taskRunner.calculate(tasks, marketData, refData);
    }

    @Override
    public void calculateAsync(
            CalculationRules calculationRules,
            List<? extends CalculationTarget> targets,
            List<Column> columns,
            MarketData marketData,
            ReferenceData refData,
            CalculationListener listener) {

        CalculationTasks tasks = CalculationTasks.of(calculationRules, targets, columns, refData);
        taskRunner.calculateAsync(tasks, marketData, refData, listener);
    }

    //-------------------------------------------------------------------------
    @Override
    public Results calculateMultiScenario(
            CalculationRules calculationRules,
            List<? extends CalculationTarget> targets,
            List<Column> columns,
            ScenarioMarketData marketData,
            ReferenceData refData) {

        CalculationTasks tasks = CalculationTasks.of(calculationRules, targets, columns, refData);
        return taskRunner.calculateMultiScenario(tasks, marketData, refData);
    }

    @Override
    public void calculateMultiScenarioAsync(
            CalculationRules calculationRules,
            List<? extends CalculationTarget> targets,
            List<Column> columns,
            ScenarioMarketData marketData,
            ReferenceData refData,
            CalculationListener listener) {

        CalculationTasks tasks = CalculationTasks.of(calculationRules, targets, columns, refData);
        taskRunner.calculateMultiScenarioAsync(tasks, marketData, refData, listener);
    }

    //-------------------------------------------------------------------------
    @Override
    public CalculationTaskRunner getTaskRunner() {
        return taskRunner;
    }

    @Override
    public void close() {
        taskRunner.close();
    }

}
