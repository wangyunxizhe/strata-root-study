/*
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.date;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.schedule.Frequency;
import com.opengamma.strata.collect.named.ExtendedEnum;
import com.opengamma.strata.collect.named.Named;
import org.joda.convert.FromString;
import org.joda.convert.ToString;

import java.time.LocalDate;

/**
 * 该接口定义了如何计算一年分数的惯例。
 * <p>
 * 本公约的目的是定义如何将日期转换为数字年份分数。在计算一段时间内的应计利息时使用。
 * <p>
 * 最常见的实现在{@link DayCounts}中提供。也可通过实现此接口添加其他实现。
 * <p>
 * 此接口的所有实现都必须是不可变的且线程安全的。
 */
public interface DayCount
        extends Named {

    /**
     * Obtains an instance from the specified unique name.
     *
     * @param uniqueName the unique name
     * @return the day count
     * @throws IllegalArgumentException if the name is not known
     */
    @FromString
    public static DayCount of(String uniqueName) {
        return extendedEnum().lookup(uniqueName);
    }

    /**
     * 根据特定日历获取“Bus/252”日计数的实例。
     * <p>
     * “Bus/252”日计数不常见，因为它依赖于特定的假日日历。日历存储在日计数内。
     * <p>
     * 为了避免系统中普遍存在的复杂性，假日日历对于“Bus/252”假日日历，
     * 使用{@linkplain ReferenceData#standard() standard reference data}查找。
     * <p>
     * 今天的计数通常在巴西使用。
     *
     * @param calendar the holiday calendar
     * @return the day count
     */
    public static DayCount ofBus252(HolidayCalendarId calendar) {
        return Business252DayCount.INSTANCE.of(calendar.resolve(ReferenceData.standard()));
    }

    /**
     * Gets the extended enum helper.
     * <p>
     * This helper allows instances of the day count to be looked up.
     * It also provides the complete set of available instances.
     *
     * @return the extended enum helper
     */
    public static ExtendedEnum<DayCount> extendedEnum() {
        return DayCounts.ENUM_LOOKUP;
    }

    //-------------------------------------------------------------------------

    /**
     * 获取指定日期之间的年份分数。
     * <p>
     * 给定两个日期，此方法根据约定返回这些日期之间一年的分数。日期必须排列整齐。
     * <p>
     * 这将使用一个简单的{@link ScheduleInfo}，它将月底约定设置为true，但会为其他方法引发异常。
     * {@code DayCount}的某些实现需要缺少的信息，因此会引发异常。
     *
     * @param firstDate  the first date
     * @param secondDate the second date, on or after the first date
     * @return the year fraction
     * @throws IllegalArgumentException      if the dates are not in order
     * @throws UnsupportedOperationException if the year fraction cannot be obtained
     */
    public default double yearFraction(LocalDate firstDate, LocalDate secondDate) {
        return yearFraction(firstDate, secondDate, DayCounts.SIMPLE_SCHEDULE_INFO);
    }

    /**
     * 获取指定日期之间的年份分数。
     * <p>
     * 给定两个日期，此方法根据约定返回这些日期之间一年的分数。日期必须排列整齐。
     *
     * @param firstDate    the first date
     * @param secondDate   the second date, on or after the first date
     * @param scheduleInfo 时间表信息
     * @return 年份分数，零或更大
     * @throws IllegalArgumentException      if the dates are not in order
     * @throws UnsupportedOperationException if the year fraction cannot be obtained
     */
    public abstract double yearFraction(LocalDate firstDate, LocalDate secondDate, ScheduleInfo scheduleInfo);

    /**
     * 获取指定日期之间的相对年份分数。
     * <p>
     * 给定两个日期，此方法根据约定返回这些日期之间一年的分数。
     * 如果第一个日期在第二个日期之后，此方法的结果将为负数。
     * 该方法使用{@link #yearFraction(LocalDate, LocalDate, ScheduleInfo)}计算结果。
     * <p>
     * 这将使用一个简单的{@link ScheduleInfo}，它将月底约定设置为true，但会为其他方法引发异常。
     * {@code DayCount}的某些实现需要缺少的信息，因此会引发异常。
     *
     * @param firstDate  the first date
     * @param secondDate the second date, which may be before the first date
     * @return the year fraction, may be negative
     * @throws UnsupportedOperationException if the year fraction cannot be obtained
     */
    public default double relativeYearFraction(LocalDate firstDate, LocalDate secondDate) {
        return relativeYearFraction(firstDate, secondDate, DayCounts.SIMPLE_SCHEDULE_INFO);
    }

    /**
     * 获取指定日期之间的相对年份分数。
     * <p>
     * 给定两个日期，此方法根据约定返回这些日期之间一年的分数。
     * 如果第一个日期在第二个日期之后，此方法的结果将为负数。
     * 使用{@link #yearFraction(LocalDate, LocalDate, ScheduleInfo)}计算结果。
     *
     * @param firstDate    the first date
     * @param secondDate   the second date, which may be before the first date
     * @param scheduleInfo the schedule information
     * @return the year fraction, may be negative
     * @throws UnsupportedOperationException if the year fraction cannot be obtained
     */
    public default double relativeYearFraction(LocalDate firstDate, LocalDate secondDate, ScheduleInfo scheduleInfo) {
        if (secondDate.isBefore(firstDate)) {
            return -yearFraction(secondDate, firstDate, scheduleInfo);
        }
        return yearFraction(firstDate, secondDate, scheduleInfo);
    }

    /**
     * 使用当天计数规则计算指定日期之间的天数。
     * <p>
     * 天数通常定义为天数除以年度估计。此方法返回天数，天数是除法的分子。
     * 例如，“Act/Act”日计数将返回两个日期之间的实际天数，但“30/360 ISDA”将返回基于30天/月的值。
     *
     * @param firstDate  the first date
     * @param secondDate the second date, which may be before the first date
     * @return the number of days, as determined by the day count
     */
    public abstract int days(LocalDate firstDate, LocalDate secondDate);

    /**
     * Gets the name that uniquely identifies this convention.
     * <p>
     * This name is used in serialization and can be parsed using {@link #of(String)}.
     *
     * @return the unique name
     */
    @ToString
    @Override
    public abstract String getName();

    //-------------------------------------------------------------------------

    /**
     * 有关计算日计数所需的计划的信息。
     * <p>
     * 某些{@link DayCount}的实现类需要有关计划的其他信息。这个接口的实现提供了这个信息。
     */
    public interface ScheduleInfo {

        /**
         * 获取计划的开始日期。
         * <p>
         * 日程中的第一个日期。如果计划针对工作日进行调整，则这是调整后的日期。
         * <p>
         * This throws an exception by default.
         *
         * @return the schedule start date
         * @throws UnsupportedOperationException if the date cannot be obtained
         */
        public default LocalDate getStartDate() {
            throw new UnsupportedOperationException("The start date of the schedule is required");
        }

        /**
         * 获取计划的结束日期。
         * <p>
         * 日程中的最后一个日期。如果日程调整为工作日，则这是调整后的日期。
         * <p>
         * This throws an exception by default.
         *
         * @return the schedule end date
         * @throws UnsupportedOperationException if the date cannot be obtained
         */
        public default LocalDate getEndDate() {
            throw new UnsupportedOperationException("The end date of the schedule is required");
        }

        /**
         * 获取计划期间的结束日期。
         * <p>
         * This is called when a day count requires the end date of the schedule period.
         * <p>
         * This throws an exception by default.
         *
         * @param date the date to find the period end date for
         * @return the period end date
         * @throws UnsupportedOperationException if the date cannot be obtained
         */
        public default LocalDate getPeriodEndDate(LocalDate date) {
            throw new UnsupportedOperationException("The end date of the schedule period is required");
        }

        /**
         * 获取计划期间的周期频率。
         * <p>
         * This is called when a day count requires the periodic frequency of the schedule.
         * <p>
         * This throws an exception by default.
         *
         * @return the periodic frequency
         * @throws UnsupportedOperationException if the frequency cannot be obtained
         */
        public default Frequency getFrequency() {
            throw new UnsupportedOperationException("The frequency of the schedule is required");
        }

        /**
         * 检查是否正在使用月末约定。
         * <p>
         * This is called when a day count needs to know whether the end-of-month convention is in use.
         * <p>
         * This is true by default.
         *
         * @return true if the end of month convention is in use
         */
        public default boolean isEndOfMonthConvention() {
            return true;
        }
    }

}
