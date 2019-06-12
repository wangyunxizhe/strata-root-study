/*
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.date;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

/**
 * 功能界面，可以调整日期。
 * <p>
 * 对于需要调整的时间是ISO-8601日期的情况，这扩展了{@link TemporalAdjuster}。
 */
@FunctionalInterface
public interface DateAdjuster
        extends TemporalAdjuster {

    /**
     * 根据实施细则调整日期。
     * <p>
     * 实现必须指定如何调整日期。
     *
     * @param date 调整前的日期
     * @return 调整后的日期
     * @throws DateTimeException   if unable to make the adjustment
     * @throws ArithmeticException if numeric overflow occurs
     */
    public abstract LocalDate adjust(LocalDate date);

    /**
     * 根据实施规则调整时间。
     * <p>
     * 这个方法通过调用{@link #adjust(LocalDate)}来实现{@link TemporalAdjuster}。
     * 注意，转换到{@code LocalDate}会忽略输入的日历系统，这是本例中需要的行为。
     *
     * @param temporal the temporal to adjust
     * @return the adjusted temporal
     * @throws DateTimeException   if unable to make the adjustment
     * @throws ArithmeticException if numeric overflow occurs
     */
    @Override
    public default Temporal adjustInto(Temporal temporal) {
        // conversion to LocalDate ensures that other calendar systems are ignored
        return temporal.with(adjust(LocalDate.from(temporal)));
    }

}
