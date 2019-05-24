/*
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.date;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.named.Named;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Stream;

import static com.opengamma.strata.basics.date.LocalDateUtils.plusDays;

/**
 * 将日期分类为假日或工作日的工具类。
 * <p>
 * 许多财务计算都需要知道日期是不是营业日。
 * 这个类概括了这些问题，每一天都被视为假日或营业日。周末实际上被视为一种特殊的假日。
 * <p>
 * Applications should refer to holidays using {@link HolidayCalendarId}.
 * The identifier must be {@linkplain HolidayCalendarId#resolve(ReferenceData) resolved}
 * to a {@link HolidayCalendar} before the holiday data methods can be accessed.
 * See {@link HolidayCalendarIds} for a standard set of identifiers available in {@link ReferenceData#standard()}.
 * <p>
 * 此接口的所有实现都必须是不可变的且线程安全的。
 *
 * @see ImmutableHolidayCalendar
 */
public interface HolidayCalendar
        extends Named {

    /**
     * 检查指定日期是否为假日。
     * <p>
     * 与{@link #isBusinessDay(LocalDate)}校验相反。周末被视为假期。
     *
     * @param date 被检查的入参日期
     * @return 如果指定日期是假日，返回true
     * @throws IllegalArgumentException 日期超出支持的范围抛异常
     */
    public abstract boolean isHoliday(LocalDate date);

    /**
     * 检查指定日期是否为工作日。
     * <p>
     * 与{@link #isHoliday(LocalDate)}校验相反。周末被视为假期。
     *
     * @param date 被检查的入参日期
     * @return 如果指定日期是工作日，返回true
     * @throws IllegalArgumentException 日期超出支持的范围抛异常
     */
    public default boolean isBusinessDay(LocalDate date) {
        return !isHoliday(date);
    }

    //-------------------------------------------------------------------------

    /**
     * 返回更改日期的时间校正器（TemporalAdjuster：java8新特性）。
     * <p>
     * 调节器用于方法 {@link Temporal#with(TemporalAdjuster)}.
     * For example:
     * <pre>
     * threeDaysLater = date.with(businessDays.adjustBy(3));
     * twoDaysEarlier = date.with(businessDays.adjustBy(-2));
     * </pre>
     *
     * @param amount 调整的工作日数
     * @return 调整之后的第一个工作日
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default TemporalAdjuster adjustBy(int amount) {
        return TemporalAdjusters.ofDateAdjuster(date -> shift(date, amount));
    }

    //-------------------------------------------------------------------------

    /**
     * 入参日期按指定的工作日数移动日历后得到的一个工作日。
     * <p>
     * 如果amount为零，则返回入参日期。
     * 如果amount为正数，则返回入参日期 + amount工作日 之后 的一个工作日。
     * 如果amount为负数，则返回入参日期 - amount工作日 之前 的一个工作日。
     *
     * @param date   入参日期
     * @param amount 天数
     * @return 计算后得到的工作日
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default LocalDate shift(LocalDate date, int amount) {
        LocalDate adjusted = date;
        if (amount > 0) {
            for (int i = 0; i < amount; i++) {
                adjusted = next(adjusted);
            }
        } else if (amount < 0) {
            for (int i = 0; i > amount; i--) {
                adjusted = previous(adjusted);
            }
        }
        return adjusted;
    }

    /**
     * 查找入参日期的下一个工作日
     * <p>
     * 返回入参日期的下一个工作日
     *
     * @param date 需要被调整的入参日期
     * @return 入参日期 之后 的第一个工作日
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default LocalDate next(LocalDate date) {
        LocalDate next = plusDays(date, 1);
        return isHoliday(next) ? next(next) : next;
    }

    /**
     * 入参日期若为工作日，则返回当天。若不是则返回 下一个 工作日
     * <p>
     * 给定一个日期，此方法返回一个工作日。如果输入日期是工作日，则返回该日期。否则，将返回下一个工作日。
     *
     * @param date 需要被调整的入参日期
     * @return 调整后的日期
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default LocalDate nextOrSame(LocalDate date) {
        return isHoliday(date) ? next(date) : date;
    }

    //-------------------------------------------------------------------------

    /**
     * 查找入参日期的前一个工作日
     * <p>
     * 返回入参日期的前一个工作日
     *
     * @param date 需要被调整的入参日期
     * @return 调整后的日期
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default LocalDate previous(LocalDate date) {
        LocalDate previous = plusDays(date, -1);
        return isHoliday(previous) ? previous(previous) : previous;
    }

    /**
     * 入参日期若为工作日，则返回当天。若不是则返回 前一个 工作日
     * <p>
     * 给定一个日期，此方法返回一个工作日。如果输入日期是工作日，则返回该日期。否则，将返回前一个工作日。
     *
     * @param date 需要被调整的入参日期
     * @return 调整后的日期
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default LocalDate previousOrSame(LocalDate date) {
        return isHoliday(date) ? previous(date) : date;
    }

    //-------------------------------------------------------------------------

    /**
     * 查找月份中的下一个工作日，如果是工作日，则返回输入日期；
     * 如果下一个工作日在不同的月份，则返回该月份的最后一个工作日。
     * <p>
     * 给定一个日期，此方法返回一个工作日。如果输入日期是工作日，则返回该日期。
     * 如果下一个工作日在同一个月内，则返回，否则返回该月的最后一个工作日。
     * <p>
     * This corresponds to the {@linkplain BusinessDayConventions#MODIFIED_FOLLOWING modified following}
     * business day convention.
     *
     * @param date 需要被调整的入参日期
     * @return 调整后的日期
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default LocalDate nextSameOrLastInMonth(LocalDate date) {
        LocalDate nextOrSame = nextOrSame(date);
        return (nextOrSame.getMonthValue() != date.getMonthValue() ? previous(date) : nextOrSame);
    }

    //-------------------------------------------------------------------------

    /**
     * 检查指定日期是否为该月的最后一个工作日。
     * <p>
     *
     * @param date 需要被校验的入参日期
     * @return 如果指定的日期是该月的最后一个有效工作日，则返回true。
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default boolean isLastBusinessDayOfMonth(LocalDate date) {
        return isBusinessDay(date) && next(date).getMonthValue() != date.getMonthValue();
    }

    /**
     * 计算月份中的最后一个工作日。
     * <p>
     * 给定日期后，此方法返回该月最后一个工作日的日期。
     *
     * @param date 需要被计算的入参日期
     * @return 月份中的最后一个工作日
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default LocalDate lastBusinessDayOfMonth(LocalDate date) {
        return previousOrSame(date.withDayOfMonth(date.lengthOfMonth()));
    }

    //-------------------------------------------------------------------------

    /**
     * 计算两个日期之间的工作日数。
     * <p>
     * 这将计算范围内的工作日数。如果日期相等，则返回零。如果结束时间早于开始时间，则引发异常。
     *
     * @param startInclusive 开始日期
     * @param endExclusive   结束日期
     * @return 开始日期和结束日期之间的总工作日数
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default int daysBetween(LocalDate startInclusive, LocalDate endExclusive) {
        ArgChecker.inOrderOrEqual(startInclusive, endExclusive, "startInclusive", "endExclusive");
        return Math.toIntExact(LocalDateUtils.stream(startInclusive, endExclusive)
                .filter(this::isBusinessDay)
                .count());
    }

    /**
     * 获取两个日期之间的工作日集合。
     * <p>
     * 该方法将周末视为假日。如果日期相等，则返回空流。如果结束时间早于开始时间，则引发异常。
     *
     * @param startInclusive 开始日期
     * @param endExclusive   结束日期
     * @return 工作日集合
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default Stream<LocalDate> businessDays(LocalDate startInclusive, LocalDate endExclusive) {
        ArgChecker.inOrderOrEqual(startInclusive, endExclusive, "startInclusive", "endExclusive");
        return LocalDateUtils.stream(startInclusive, endExclusive)
                .filter(this::isBusinessDay);
    }

    /**
     * 获取两个日期之间的假日集合。
     * <p>
     * 该方法将周末视为假日。如果日期相等，则返回空流。如果结束时间早于开始时间，则引发异常。
     *
     * @param startInclusive 开始日期
     * @param endExclusive   结束日期
     * @return 假日集合
     * @throws IllegalArgumentException 计算超出支持的范围抛异常
     */
    public default Stream<LocalDate> holidays(LocalDate startInclusive, LocalDate endExclusive) {
        ArgChecker.inOrderOrEqual(startInclusive, endExclusive, "startInclusive", "endExclusive");
        return LocalDateUtils.stream(startInclusive, endExclusive)
                .filter(this::isHoliday);
    }

    //-------------------------------------------------------------------------

    /**
     * 将此HolidayCalendar与另一个HolidayCalendar合并。
     * <p>
     * 如果生成的calendar在两个源calendar中都是工作日，那么它将把一天声明为工作日。
     *
     * @param other the other holiday calendar
     * @return the combined calendar
     * @throws IllegalArgumentException if unable to combine the calendars
     */
    public default HolidayCalendar combinedWith(HolidayCalendar other) {
        if (this.equals(other)) {
            return this;
        }
        if (other == HolidayCalendars.NO_HOLIDAYS) {
            return this;
        }
        return new CombinedHolidayCalendar(this, other);
    }

    //-------------------------------------------------------------------------

    /**
     * 获取calendar的id。
     * <p>
     * 此id用于 {@link ReferenceData}.
     *
     * @return the identifier
     */
    public abstract HolidayCalendarId getId();

    //-------------------------------------------------------------------------

    /**
     * 获取calendar的name。
     * <p>
     * 与{@linkplain HolidayCalendarId identifier}关联的名称
     *
     * @return the name
     */
    @Override
    public default String getName() {
        return getId().getName();
    }

}
