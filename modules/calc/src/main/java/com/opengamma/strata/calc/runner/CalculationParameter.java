/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.runner;

import com.opengamma.strata.basics.CalculationTarget;
import com.opengamma.strata.calc.CalculationRules;
import com.opengamma.strata.calc.Column;
import com.opengamma.strata.calc.Measure;
import com.opengamma.strata.calc.ReportingCurrency;

import java.util.Optional;

/**
 * 计算参数的基本接口。
 * <p>
 * 参数用于控制计算。
 * <p>
 * 例如，{@link ReportingCurrency}是一个控制货币转换的参数。
 * 如果在{@link Column}或{@link CalculationRules}中指定，则输出将转换为指定的货币。
 * <p>
 * 应用程序可以实现此接口来向系统添加新参数。
 * 为了使用，必须编写新的{@link CalculationFunction}实现，该实现接收参数并执行适当的行为。
 * <p>
 * 这个接口的实现必须是不可变的。
 */
public interface CalculationParameter {

    /**
     * 获取要查询参数的类型。
     * <p>
     * Parameters can be queried using {@link CalculationParameters#findParameter(Class)}.
     * （可以使用{@link CalculationParameters#findParameter(Class)}查询参数。）
     * This type is the key that callers must use in that method.
     * <p>
     * 默认情况下，这只是{@link Object#getClass()}。只有当查询类型是接口而不是具体类时才会有所不同。
     *
     * @return the type of the parameter implementation
     */
    public default Class<? extends CalculationParameter> queryType() {
        return getClass();
    }

    /**
     * 将此参数设置为指定的目标和度量。
     * <p>
     * 参数可以应用于所有目标和度量，也可以只应用于子集。
     * {@link CalculationParameters#filter(CalculationTarget, Measure)}方法使用此方法过滤一组完整的参数。
     * <p>
     * 默认情况下，它返回{@code Optional.of(this)}。如果参数不适用于目标或度量，则必须返回可选空。
     * 如果需要，结果可以是不同的参数，允许一个参数在过滤时委托给另一个参数。
     *
     * @param target  要计算的目标，比如trade
     * @param measure 要计算的度量值
     * @return 适合于目标和度量的参数，如果该参数不适用则为空
     */
    public default Optional<CalculationParameter> filter(CalculationTarget target, Measure measure) {
        return Optional.of(this);
    }

}
