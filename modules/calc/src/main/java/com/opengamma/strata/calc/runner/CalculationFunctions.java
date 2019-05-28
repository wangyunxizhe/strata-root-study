/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.runner;

import com.opengamma.strata.basics.CalculationTarget;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.opengamma.strata.collect.Guavate.toImmutableMap;

/**
 * 项目的计算功能类。
 * <p>
 * 这提供了将在计算中使用的完整函数集。
 * <p>
 * 默认实现由静态工厂方法访问。
 * 它按{@link CalculationTarget}的类型匹配{@link CalculationFunction}。
 * 因此，默认实现本质上是一个{@code Map}，其中key是函数操作的目标类型{@code Class}。
 */
public interface CalculationFunctions {

    /**
     * Obtains an empty instance with no functions.
     *
     * @return the empty instance
     */
    public static CalculationFunctions empty() {
        return DefaultCalculationFunctions.EMPTY;
    }

    /**
     * 从指定的函数获取实例。
     * <p>
     * 这将返回按目标类型匹配函数的实现，如{@link CalculationFunction#targetType()}返回的那样。
     * 列表将被转换为key=目标类型的{@code Map}。每个函数必须引用不同的目标类型。
     *
     * @param functions the functions
     * @return the calculation functions
     */
    public static CalculationFunctions of(CalculationFunction<?>... functions) {
        return DefaultCalculationFunctions.of(Stream.of(functions).collect(toImmutableMap(fn -> fn.targetType())));
    }

    /**
     * 功能与上个方法相同，传参形式不同，这里是传List集合
     *
     * @param functions the functions
     * @return the calculation functions
     */
    public static CalculationFunctions of(List<? extends CalculationFunction<?>> functions) {
        return DefaultCalculationFunctions.of(functions.stream().collect(toImmutableMap(fn -> fn.targetType())));
    }

    /**
     * 功能与上个方法相同，传参形式不同，这里是传Map集合
     * <p>
     * 返回按目标类型匹配函数的实现。查找匹配函数时，将在指定的Map中查找目标类型。
     * 将验证Map，以确保{@code Class}与{@link CalculationFunction#targetType()}一致。
     *
     * @param functions the functions
     * @return the calculation functions
     */
    public static CalculationFunctions of(Map<Class<?>, ? extends CalculationFunction<?>> functions) {
        return DefaultCalculationFunctions.of(functions);
    }

    //-------------------------------------------------------------------------

    /**
     * 获取处理指定目标的方法。
     * <p>
     * 如果找不到函数，则会提供一个可以不执行计算的合适默认值。
     *
     * @param <T>    the target type
     * @param target 计算的目标，如Trade
     * @return the function
     */
    public default <T extends CalculationTarget> CalculationFunction<? super T> getFunction(T target) {
        return findFunction(target).orElse(MissingConfigCalculationFunction.INSTANCE);
    }

    /**
     * 查找处理指定目标的方法。
     * <p>
     * 如果找不到函数，则结果为空。
     *
     * @param <T>    the target type
     * @param target 计算的目标，如Trade
     * @return the function, empty if not found
     */
    public abstract <T extends CalculationTarget> Optional<CalculationFunction<? super T>> findFunction(T target);

    /**
     * 返回一组CalculationFunctions，将此集合中的CalculationFunctions与另一个集合中的CalculationFunctions组合在一起。
     * <p>
     * 如果两组CalculationFunctions都包含一个目标函数，那么将返回此集合中的函数。
     *
     * @param other another set of calculation functions
     * @return a set of calculation functions which combines the functions in this set with the functions in the other
     */
    public default CalculationFunctions composedWith(CalculationFunctions other) {
        return CompositeCalculationFunctions.of(this, other);
    }

    /**
     * 返回一组CalculationFunctions，将此集合中的函数与某些派生计算函数组合在一起。
     * <p>
     * 每个派生函数为一种类型的目标计算一个度量，可能使用其他计算度量作为输入。
     * <p>
     * 如果任何派生函数彼此依赖，则必须以正确的顺序传递给此方法，以确保满足它们的依赖关系。
     * 例如，如果有一个派生函数{@code fnA}，它取决于由函数{@code fnB}计算的度量，
     * 则必须按{@code fnB, fnA}的顺序传递给此方法。
     *
     * @param functions the functions
     * @return a set of calculation functions which combines the functions in this set with some
     * derived calculation functions
     */
    public default CalculationFunctions composedWith(DerivedCalculationFunction<?, ?>... functions) {
        return new DerivedCalculationFunctions(this, Arrays.asList(functions));
    }

    /**
     * Returns a set of calculation functions which combines the functions in this set with some
     * derived calculation functions.
     * <p>
     * Each derived function calculates one measure for one type of target, possibly using other calculated measures
     * as inputs.
     * <p>
     * If any of the derived functions depend on each other they must be passed to this method in the correct
     * order to ensure their dependencies can be satisfied. For example, if there is a derived function
     * {@code fnA} which depends on the measure calculated by function {@code fnB} they must be passed to
     * this method in the order {@code fnB, fnA}.
     *
     * @param functions the functions
     * @return a set of calculation functions which combines the functions in this set with some
     * derived calculation functions
     */
    public default CalculationFunctions composedWith(List<DerivedCalculationFunction<?, ?>> functions) {
        return new DerivedCalculationFunctions(this, functions);
    }

}
