/*
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.date;

import com.opengamma.strata.collect.named.ExtendedEnum;

/**
 * 标准工作日规则的常量和实现。
 * <p>
 * 每个规则的目的是定义如何处理非工作日。
 * 在财务部门处理日期时，通常计划将非工作日（如周末和节假日）转换为附近的有效工作日。
 * 该公约与{@linkplain HolidayCalendar holiday calendar}结合起来，精确地定义了应如何进行调整。
 */
public final class BusinessDayConventions {
    // constants are indirected via ENUM_LOOKUP to allow them to be replaced by config

    /**
     * The extended enum lookup from name to instance.
     */
    static final ExtendedEnum<BusinessDayConvention> ENUM_LOOKUP = ExtendedEnum.of(BusinessDayConvention.class);

    /**
     * The 'NoAdjust' convention which makes no adjustment.
     * <p>
     * The input date will not be adjusted even if it is not a business day.
     */
    public static final BusinessDayConvention NO_ADJUST =
            BusinessDayConvention.of(StandardBusinessDayConventions.NO_ADJUST.getName());
    /**
     * 调整到下一个工作日的“Following”约定。
     * <p>
     * 如果输入日期不是工作日，则调整日期。调整日期是下一个工作日。
     * 底层适用HolidayCalendar.nextOrSame(date)方法实现
     */
    public static final BusinessDayConvention FOLLOWING =
            BusinessDayConvention.of(StandardBusinessDayConventions.FOLLOWING.getName());
    /**
     * The 'ModifiedFollowing' convention which adjusts to the next business day without crossing month end.
     * <p>
     * If the input date is not a business day then the date is adjusted.
     * The adjusted date is the next business day unless that day is in a different
     * calendar month, in which case the previous business day is returned.
     */
    public static final BusinessDayConvention MODIFIED_FOLLOWING =
            BusinessDayConvention.of(StandardBusinessDayConventions.MODIFIED_FOLLOWING.getName());
    /**
     * The 'ModifiedFollowingBiMonthly' convention which adjusts to the next business day without
     * crossing mid-month or month end.
     * <p>
     * If the input date is not a business day then the date is adjusted.
     * The month is divided into two parts, the first half, the 1st to 15th and the 16th onwards.
     * The adjusted date is the next business day unless that day is in a different half-month,
     * in which case the previous business day is returned.
     */
    public static final BusinessDayConvention MODIFIED_FOLLOWING_BI_MONTHLY =
            BusinessDayConvention.of(StandardBusinessDayConventions.MODIFIED_FOLLOWING_BI_MONTHLY.getName());
    /**
     * The 'Preceding' convention which adjusts to the previous business day.
     * <p>
     * If the input date is not a business day then the date is adjusted.
     * The adjusted date is the previous business day.
     */
    public static final BusinessDayConvention PRECEDING =
            BusinessDayConvention.of(StandardBusinessDayConventions.PRECEDING.getName());
    /**
     * The 'ModifiedPreceding' convention which adjusts to the previous business day without crossing month start.
     * <p>
     * If the input date is not a business day then the date is adjusted.
     * The adjusted date is the previous business day unless that day is in a different
     * calendar month, in which case the next business day is returned.
     */
    public static final BusinessDayConvention MODIFIED_PRECEDING =
            BusinessDayConvention.of(StandardBusinessDayConventions.MODIFIED_PRECEDING.getName());
    /**
     * The 'Nearest' convention which adjusts Sunday and Monday forward, and other days backward.
     * <p>
     * If the input date is not a business day then the date is adjusted.
     * If the input is Sunday or Monday then the next business day is returned.
     * Otherwise the previous business day is returned.
     * <p>
     * Note that despite the name, the algorithm may not return the business day that is actually nearest.
     */
    public static final BusinessDayConvention NEAREST =
            BusinessDayConvention.of(StandardBusinessDayConventions.NEAREST.getName());

    //-------------------------------------------------------------------------

    /**
     * Restricted constructor.
     */
    private BusinessDayConventions() {
    }

}
