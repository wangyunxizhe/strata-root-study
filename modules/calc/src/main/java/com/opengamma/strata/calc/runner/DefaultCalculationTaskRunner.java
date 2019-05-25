/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.runner;

import com.opengamma.strata.basics.CalculationTarget;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.calc.Column;
import com.opengamma.strata.calc.Results;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.Messages;
import com.opengamma.strata.collect.result.Result;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.data.scenario.ScenarioArray;
import com.opengamma.strata.data.scenario.ScenarioMarketData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.opengamma.strata.collect.Guavate.toImmutableList;

/**
 * 默认计算任务运行程序。
 * <p>
 * 这将使用{@link ExecutorService}单个实例。
 */
final class DefaultCalculationTaskRunner implements CalculationTaskRunner {

    /**
     * 执行单个计算的任务。
     * 这通常是多线程的，但是单个或直接的执行器也可以工作。
     */
    private final ExecutorService executor;

    //-------------------------------------------------------------------------

    /**
     * 创建能够执行计算的标准多线程计算任务运行程序。
     * <p>
     * 这个工厂根据可用处理器的数量创建一个执行器。
     * 建议使用 try-with-resources 管理运行程序：
     * <pre>
     *  try (DefaultCalculationTaskRunner runner = DefaultCalculationTaskRunner.ofMultiThreaded()) {
     *    // use the runner
     *  }
     * </pre>
     *
     * @return the calculation task runner
     */
    static DefaultCalculationTaskRunner ofMultiThreaded() {
        return new DefaultCalculationTaskRunner(createExecutor(Runtime.getRuntime().availableProcessors()));
    }

    /**
     * 创建能够执行计算的计算任务运行程序，指定执行者。
     * <p>
     * 呼叫者有责任管理执行者的生命周期。
     *
     * @param executor the executor to use
     * @return the calculation task runner
     */
    static DefaultCalculationTaskRunner of(ExecutorService executor) {
        return new DefaultCalculationTaskRunner(executor);
    }

    // 使用守护进程线程创建执行器（一个线程池）
    private static ExecutorService createExecutor(int threads) {
        int effectiveThreads = (threads <= 0 ? Runtime.getRuntime().availableProcessors() : threads);
        ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        ThreadFactory threadFactory = r -> {
            Thread t = defaultFactory.newThread(r);
            t.setName("CalculationTaskRunner-" + t.getName());
            t.setDaemon(true);
            return t;
        };
        return Executors.newFixedThreadPool(effectiveThreads, threadFactory);
    }

    //-------------------------------------------------------------------------

    /**
     * 创建指定要使用的执行器的实例。
     *
     * @param executor 用于执行计算的执行器
     */
    private DefaultCalculationTaskRunner(ExecutorService executor) {
        this.executor = ArgChecker.notNull(executor, "executor");
    }

    //-------------------------------------------------------------------------
    @Override
    public Results calculate(
            CalculationTasks tasks,
            MarketData marketData,
            ReferenceData refData) {

        // perform the calculations
        ScenarioMarketData md = ScenarioMarketData.of(1, marketData);
        Results results = calculateMultiScenario(tasks, md, refData);

        // unwrap the results
        // since there is only one scenario it is not desirable to return scenario result containers
        List<Result<?>> mappedResults = results.getCells().stream()
                .map(r -> unwrapScenarioResult(r))
                .collect(toImmutableList());
        return Results.of(results.getColumns(), mappedResults);
    }

    //-------------------------------------------------------------------------

    /**
     * 从包含单个结果的{@link ScenarioArray}实例中展开结果。
     * <p>
     * 当用户执行单个方案时，将使用一组大小为1的方案市场数据调用函数。
     * 这意味着函数更简单，并且总是处理场景。
     * 但是如果用户要求一组结果，他们不希望看到大小为1的集合，因此需要展开场景结果。
     * <p>
     * 如果{@code result}是故障或不包含{@code ScenarioArray}则返回。
     * <p>
     * 如果使用包含多个值的{@code ScenarioArray}调用此方法，则会引发异常。
     */
    private static Result<?> unwrapScenarioResult(Result<?> result) {
        if (result.isFailure()) {
            return result;
        }
        Object value = result.getValue();
        if (!(value instanceof ScenarioArray)) {
            return result;
        }
        ScenarioArray<?> scenarioResult = (ScenarioArray<?>) value;

        if (scenarioResult.getScenarioCount() != 1) {
            throw new IllegalArgumentException(Messages.format(
                    "Expected one result but found {} in {}",
                    scenarioResult.getScenarioCount(), scenarioResult));
        }
        return Result.success(scenarioResult.get(0));
    }

    @Override
    public void calculateAsync(
            CalculationTasks tasks,
            MarketData marketData,
            ReferenceData refData,
            CalculationListener listener) {

        // the listener is decorated to unwrap ScenarioArrays containing a single result
        ScenarioMarketData md = ScenarioMarketData.of(1, marketData);
        UnwrappingListener unwrappingListener = new UnwrappingListener(listener);
        calculateMultiScenarioAsync(tasks, md, refData, unwrappingListener);
    }

    //-------------------------------------------------------------------------
    @Override
    public Results calculateMultiScenario(
            CalculationTasks tasks,
            ScenarioMarketData marketData,
            ReferenceData refData) {

        ResultsListener listener = new ResultsListener();
        calculateMultiScenarioAsync(tasks, marketData, refData, listener);
        return listener.result();
    }

    @Override
    public void calculateMultiScenarioAsync(
            CalculationTasks tasks,
            ScenarioMarketData marketData,
            ReferenceData refData,
            CalculationListener listener) {

        List<CalculationTask> taskList = tasks.getTasks();
        // the listener is invoked via this wrapper
        // the wrapper ensures thread-safety for the listener
        // it also calls the listener with single CalculationResult cells, not CalculationResults
        Consumer<CalculationResults> consumer =
                new ListenerWrapper(listener, taskList.size(), tasks.getTargets(), tasks.getColumns());

        // run each task using the executor
        taskList.forEach(task -> runTask(task, marketData, refData, consumer));
    }

    // submits a task to the executor to be run
    private void runTask(
            CalculationTask task,
            ScenarioMarketData marketData,
            ReferenceData refData,
            Consumer<CalculationResults> consumer) {

        // the task is executed, with the result passed to the consumer
        // the consumer wraps the listener to ensure thread-safety
        Supplier<CalculationResults> taskExecutor = () -> task.execute(marketData, refData);
        CompletableFuture.supplyAsync(taskExecutor, executor).thenAccept(consumer);
    }

    //-------------------------------------------------------------------------
    @Override
    public void close() {
        executor.shutdown();
    }

    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------

    /**
     * 在将值传递给代理侦听器之前，设置另一个侦听器并打开包含单个值的{@link ScenarioArray}实例的侦听器。
     * This is used by the single scenario async method.
     */
    private static final class UnwrappingListener implements CalculationListener {

        private final CalculationListener delegate;

        private UnwrappingListener(CalculationListener delegate) {
            this.delegate = delegate;
        }

        @Override
        public void calculationsStarted(List<CalculationTarget> targets, List<Column> columns) {
            delegate.calculationsStarted(targets, columns);
        }

        @Override
        public void resultReceived(CalculationTarget target, CalculationResult calculationResult) {
            Result<?> result = calculationResult.getResult();
            Result<?> unwrappedResult = unwrapScenarioResult(result);
            CalculationResult unwrappedCalculationResult = calculationResult.withResult(unwrappedResult);
            delegate.resultReceived(target, unwrappedCalculationResult);
        }

        @Override
        public void calculationsComplete() {
            delegate.calculationsComplete();
        }
    }

}
