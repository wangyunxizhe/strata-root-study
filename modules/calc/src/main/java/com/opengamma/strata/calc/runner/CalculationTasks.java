/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.runner;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.opengamma.strata.basics.CalculationTarget;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.ResolvableCalculationTarget;
import com.opengamma.strata.calc.*;
import com.opengamma.strata.calc.marketdata.MarketDataRequirements;
import com.opengamma.strata.calc.marketdata.MarketDataRequirementsBuilder;
import com.opengamma.strata.collect.Messages;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.TypedMetaBean;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static com.opengamma.strata.collect.Guavate.toImmutableList;

/**
 * 用于执行计算的任务。
 * <p>
 * 这将捕获定义结果网格的目标、列和任务。可以执行每个任务来生成结果。
 * 应用程序通常使用{@link CalculationRunner}或{@link CalculationTaskRunner}来执行任务。
 */
@BeanDefinition(style = "light")
public final class CalculationTasks implements ImmutableBean {

  /**
   * The targets that calculations will be performed on.
   * <p>
   * 计算的结果将是一个网格，其中的每一行都取自这个列表。
   */
  @PropertyDefinition(validate = "notEmpty")
  private final List<CalculationTarget> targets;
  /**
   * The columns that will be calculated.
   * <p>
   * 计算的结果将是一个网格，其中的每一列都取自这个列表。
   */
  @PropertyDefinition(validate = "notEmpty")
  private final List<Column> columns;
  /**
   * The tasks that perform the individual calculations.
   * <p>
   * 结果可以可视化为一个网格，每个目标有一行，每个度量值有一列。每个任务都可以计算网格中一个或多个单元格的结果。
   */
  @PropertyDefinition(validate = "notEmpty")
  private final List<CalculationTask> tasks;

  //-------------------------------------------------------------------------
  /**
   * 从一组目标、列和规则中获取实例。
   * <p>
   * 目标通常是trades。
   * columns表示要计算的度量值。
   * <p>
   * 任何实现{@link ResolvableCalculationTarget}的目标都会导致任务失败。
   * 
   * @param rules  the rules defining how the calculation is performed
   * @param targets  the targets for which values of the measures will be calculated
   * @param columns  the columns that will be calculated
   * @return the calculation tasks
   */
  public static CalculationTasks of(
      CalculationRules rules,
      List<? extends CalculationTarget> targets,
      List<Column> columns) {

    return of(rules, targets, columns, ReferenceData.empty());
  }

  /**
   * 从一组目标、列和规则中获取实例，并解析目标。
   * <p>
   * 目标通常是trades和positions。columns表示要计算的度量。
   * <p>
   * 如果这些targets实现了{@link ResolvableCalculationTarget}，那么它们将被解析。
   * 
   * @param rules  the rules defining how the calculation is performed
   * @param targets  the targets for which values of the measures will be calculated
   * @param columns  the columns that will be calculated
   * @param refData  用于解析目标的引用数据
   * @return the calculation tasks
   */
  public static CalculationTasks of(
      CalculationRules rules,
      List<? extends CalculationTarget> targets,
      List<Column> columns,
      ReferenceData refData) {

    // create columns that are a combination of the column overrides and the defaults
    // this is done once as it is the same for all targets
    List<Column> effectiveColumns =
        columns.stream()
            .map(column -> column.combineWithDefaults(rules.getReportingCurrency(), rules.getParameters()))
            .collect(toImmutableList());

    // 循环目标，然后是列，以构建任务
    ImmutableList.Builder<CalculationTask> taskBuilder = ImmutableList.builder();
    for (int rowIndex = 0; rowIndex < targets.size(); rowIndex++) {
      CalculationTarget target = resolveTarget(targets.get(rowIndex), refData);

      // find the applicable function, resolving the target if necessary
      CalculationFunction<?> fn = target instanceof UnresolvableTarget ?
          UnresolvableTargetCalculationFunction.INSTANCE :
          rules.getFunctions().getFunction(target);

      // create the tasks
      List<CalculationTask> targetTasks = createTargetTasks(target, rowIndex, fn, effectiveColumns);
      taskBuilder.addAll(targetTasks);
    }

    // 计算任务保存原始的用户指定列，而不是派生列
    return new CalculationTasks(taskBuilder.build(), columns);
  }

  // resolves the target
  private static CalculationTarget resolveTarget(CalculationTarget target, ReferenceData refData) {
    if (target instanceof ResolvableCalculationTarget) {
      ResolvableCalculationTarget resolvable = (ResolvableCalculationTarget) target;
      try {
        return resolvable.resolveTarget(refData);
      } catch (RuntimeException ex) {
        return new UnresolvableTarget(resolvable, ex.getMessage());
      }
    }
    return target;
  }

  // 为单个目标创建任务
  private static List<CalculationTask> createTargetTasks(
      CalculationTarget resolvedTarget,
      int rowIndex,
      CalculationFunction<?> function,
      List<Column> columns) {

    // 创建单元格并对它们进行分组
    ListMultimap<CalculationParameters, CalculationTaskCell> grouped = ArrayListMultimap.create();
    for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
      Column column = columns.get(colIndex);
      Measure measure = column.getMeasure();

      ReportingCurrency reportingCurrency = column.getReportingCurrency().orElse(ReportingCurrency.NATURAL);
      CalculationTaskCell cell = CalculationTaskCell.of(rowIndex, colIndex, measure, reportingCurrency);
      // 找到可以共享的单元格，使用相同的映射和参数(减去报告货币)
      CalculationParameters params = column.getParameters().filter(resolvedTarget, measure);
      grouped.put(params, cell);
    }

    // build tasks
    ImmutableList.Builder<CalculationTask> taskBuilder = ImmutableList.builder();
    for (CalculationParameters params : grouped.keySet()) {
      taskBuilder.add(CalculationTask.of(resolvedTarget, function, params, grouped.get(params)));
    }
    return taskBuilder.build();
  }

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance from a set of tasks and columns.
   * 
   * @param tasks  the tasks that perform the calculations
   * @param columns  the columns that define the calculations
   * @return the calculation tasks
   */
  public static CalculationTasks of(List<CalculationTask> tasks, List<Column> columns) {
    return new CalculationTasks(tasks, columns);
  }

  //-------------------------------------------------------------------------
  /**
   * 私有化构造函数
   * 
   * @param tasks  执行计算的任务
   * @param columns  定义计算的列
   */
  private CalculationTasks(List<CalculationTask> tasks, List<Column> columns) {
    this.columns = ImmutableList.copyOf(columns);
    this.tasks = ImmutableList.copyOf(tasks);

    // validate the number of tasks and number of columns tally
    long cellCount = tasks.stream()
        .flatMap(task -> task.getCells().stream())
        .count();
    int columnCount = columns.size();
    if (cellCount != 0) {
      if (columnCount == 0) {
        throw new IllegalArgumentException("There must be at least one column");
      }
      if (cellCount % columnCount != 0) {
        throw new IllegalArgumentException(
            Messages.format(
                "Number of cells ({}) must be exactly divisible by the number of columns ({})",
                cellCount,
                columnCount));
      }
    }

    // pull out the targets from the tasks
    int targetCount = (int) cellCount / columnCount;
    CalculationTarget[] targets = new CalculationTarget[targetCount];
    for (CalculationTask task : tasks) {
      int rowIdx = task.getRowIndex();
      if (targets[rowIdx] == null) {
        targets[rowIdx] = task.getTarget();
      } else if (targets[rowIdx] != task.getTarget()) {
        throw new IllegalArgumentException(Messages.format(
            "Tasks define two different targets for row {}: {} and {}", rowIdx, targets[rowIdx], task.getTarget()));
      }
    }
    this.targets = ImmutableList.copyOf(targets);  // missing targets will be caught here by null check
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the market data that is required to perform the calculations.
   * <p>
   * This can be used to pass into the market data system to obtain and calibrate data.
   *
   * @param refData  the reference data
   * @return the market data required for all calculations
   * @throws RuntimeException if unable to obtain the requirements
   */
  public MarketDataRequirements requirements(ReferenceData refData) {
    // use for loop not streams for shorter stack traces
    MarketDataRequirementsBuilder builder = MarketDataRequirements.builder();
    for (CalculationTask task : tasks) {
      builder.addRequirements(task.requirements(refData));
    }
    return builder.build();
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return Messages.format("CalculationTasks[grid={}x{}]", targets.size(), columns.size());
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code CalculationTasks}.
   */
  private static final TypedMetaBean<CalculationTasks> META_BEAN =
      LightMetaBean.of(
          CalculationTasks.class,
          MethodHandles.lookup(),
          new String[] {
              "targets",
              "columns",
              "tasks"},
          ImmutableList.of(),
          ImmutableList.of(),
          ImmutableList.of());

  /**
   * The meta-bean for {@code CalculationTasks}.
   * @return the meta-bean, not null
   */
  public static TypedMetaBean<CalculationTasks> meta() {
    return META_BEAN;
  }

  static {
    MetaBean.register(META_BEAN);
  }

  private CalculationTasks(
      List<CalculationTarget> targets,
      List<Column> columns,
      List<CalculationTask> tasks) {
    JodaBeanUtils.notEmpty(targets, "targets");
    JodaBeanUtils.notEmpty(columns, "columns");
    JodaBeanUtils.notEmpty(tasks, "tasks");
    this.targets = ImmutableList.copyOf(targets);
    this.columns = ImmutableList.copyOf(columns);
    this.tasks = ImmutableList.copyOf(tasks);
  }

  @Override
  public TypedMetaBean<CalculationTasks> metaBean() {
    return META_BEAN;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the targets that calculations will be performed on.
   * <p>
   * The result of the calculations will be a grid where each row is taken from this list.
   * @return the value of the property, not empty
   */
  public List<CalculationTarget> getTargets() {
    return targets;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the columns that will be calculated.
   * <p>
   * The result of the calculations will be a grid where each column is taken from this list.
   * @return the value of the property, not empty
   */
  public List<Column> getColumns() {
    return columns;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the tasks that perform the individual calculations.
   * <p>
   * The results can be visualized as a grid, with a row for each target and a column for each measure.
   * Each task can calculate the result for one or more cells in the grid.
   * @return the value of the property, not empty
   */
  public List<CalculationTask> getTasks() {
    return tasks;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CalculationTasks other = (CalculationTasks) obj;
      return JodaBeanUtils.equal(targets, other.targets) &&
          JodaBeanUtils.equal(columns, other.columns) &&
          JodaBeanUtils.equal(tasks, other.tasks);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(targets);
    hash = hash * 31 + JodaBeanUtils.hashCode(columns);
    hash = hash * 31 + JodaBeanUtils.hashCode(tasks);
    return hash;
  }

  //-------------------------- AUTOGENERATED END --------------------------
}
