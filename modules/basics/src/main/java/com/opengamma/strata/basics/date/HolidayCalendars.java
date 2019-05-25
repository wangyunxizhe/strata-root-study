/*
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.date;

import com.google.common.base.Splitter;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.collect.named.ExtendedEnum;

/**
 * 标准holiday calendars的常量和实现。
 * <p>
 * 每个holiday calendars的目的是定义某个日期是假日还是工作日。
 * 标准holiday calendar数据由直接研究提供，不是由holiday calendar数据的供应商派生的。由{@code HolidayCalendar.ini}实现。
 * 这些数据可能足以满足您的生产需要，也可能不足以满足您的生产需要。
 * <p>
 * Applications should refer to holidays using {@link HolidayCalendarId}.
 * The identifier must be {@linkplain HolidayCalendarId#resolve(ReferenceData) resolved}
 * to a {@link HolidayCalendar} before holidays can be accessed.
 */
public final class HolidayCalendars {

    /**
     * 传入{@code ReferenceData}对象，以便{@code HolidayCalendarId}的所有请求都返回一个值。
     * <p>
     * If the {@link HolidayCalendarId} is not found in the underlying reference data,
     * an instance with Saturday/Sunday holidays will be returned.
     *
     * @param underlying the underlying instance
     * @return the holiday safe reference data
     */
    public static ReferenceData defaultingReferenceData(ReferenceData underlying) {
        return new HolidaySafeReferenceData(underlying);
    }

    //-------------------------------------------------------------------------
    /**
     * 声明没有假日和周末的HolidayCalendar实例。
     * <p>
     * 这个HolidayCalendar对象的作用是使每一天都成为一个工作日。
     * It is often used to indicate that a holiday calendar does not apply.
     */
    public static final HolidayCalendar NO_HOLIDAYS = NoHolidaysCalendar.INSTANCE;
    /**
     * 声明所有日期都为工作日的HolidayCalendar实例，周六/周日除外。
     * <p>
     * 这个实例在测试场景中非常有用。
     * Note that not all countries use Saturday and Sunday weekends.
     */
    public static final HolidayCalendar SAT_SUN = WeekendHolidayCalendar.SAT_SUN;
    /**
     * 声明所有日期都为工作日的HolidayCalendar实例，星期五/星期六除外。
     * <p>
     * 这个实例在测试场景中非常有用。
     */
    public static final HolidayCalendar FRI_SAT = WeekendHolidayCalendar.FRI_SAT;
    /**
     * 声明所有日期都为工作日的HolidayCalendar实例，星期四/星期五除外。
     * <p>
     * 这个实例在测试场景中非常有用。
     */
    public static final HolidayCalendar THU_FRI = WeekendHolidayCalendar.THU_FRI;

    // 此常量必须在上述常量之后。
    /**
     * The extended enum lookup from name to instance.
     */
    private static final ExtendedEnum<HolidayCalendar> ENUM_LOOKUP = ExtendedEnum.of(HolidayCalendar.class);

    //-------------------------------------------------------------------------

    /**
     * 从标准HolidayCalendars集合中获取实例。
     * <p>
     * 通过name（大写简称）来获取对应的HolidayCalendar对象
     * <p>
     * 程序通常应避免使用此方法。
     * 相反，应用程序应使用{@link HolidayCalendarId}获取HolidayCalendar，并使用{@link ReferenceData}解析。
     * <p>
     * 入参可以使用“+”符号组合。
     * For example, 'GBLO+USNY' will combine the separate 'GBLO' and 'USNY' calendars.
     *
     * @param uniqueName calendar的唯一名称
     * @return the holiday calendar
     */
    public static HolidayCalendar of(String uniqueName) {
        if (uniqueName.contains("+")) {
            return Splitter.on('+').splitToList(uniqueName).stream()
                    .map(HolidayCalendars::of)
                    .reduce(NO_HOLIDAYS, HolidayCalendar::combinedWith);
        }
        return ENUM_LOOKUP.lookup(uniqueName);
    }

    /**
     * 获取扩展枚举帮助方法。
     * <p>
     * 此帮助方法允许查找HolidayCalendar的实例。它还提供完整的可用HolidayCalendar集合。
     *
     * @return the extended enum helper
     */
    public static ExtendedEnum<HolidayCalendar> extendedEnum() {
        return HolidayCalendars.ENUM_LOOKUP;
    }

    //-------------------------------------------------------------------------

    /**
     * Restricted constructor.
     */
    private HolidayCalendars() {
    }

}
