/*
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.date;

import com.opengamma.strata.collect.named.ExtendedEnum;
import com.opengamma.strata.collect.named.Named;
import org.joda.convert.FromString;
import org.joda.convert.ToString;

import java.time.LocalDate;

/**
 * 一种规则，定义了当日期落在工作日以外的某一天时如何调整日期。
 * <p>
 * 本规则的目的是规定如何处理非工作日。在处理财务日期时，通常打算将非工作日（如周末和节假日）转换为附近的有效工作日。
 * 该规则与{@linkplain HolidayCalendar holiday calendar}结合起来，精确地定义了应如何进行调整。
 * <p>
 * 最常见的实现在{@link BusinessDayConventions}中提供。可通过实现此接口添加其他实现。
 * <p>
 * 此接口的所有实现都必须是不可变的且线程安全的。
 */
public interface BusinessDayConvention
        extends Named {

    /**
     * Obtains an instance from the specified unique name.
     *
     * @param uniqueName the unique name
     * @return the business convention
     * @throws IllegalArgumentException if the name is not known
     */
    @FromString
    public static BusinessDayConvention of(String uniqueName) {
        return extendedEnum().lookup(uniqueName);
    }

    /**
     * Gets the extended enum helper.
     * <p>
     * This helper allows instances of the convention to be looked up.
     * It also provides the complete set of available instances.
     *
     * @return the extended enum helper
     */
    public static ExtendedEnum<BusinessDayConvention> extendedEnum() {
        return BusinessDayConventions.ENUM_LOOKUP;
    }

    //-------------------------------------------------------------------------

    /**
     * 如果不是工作日，则根据需要调整日期。
     * <p>
     * 如果日期是工作日，则将原封不动地返回。
     * 如果该日期不是工作日，则按规则调整。
     *
     * @param date     the date to adjust
     * @param calendar the calendar that defines holidays and business days
     * @return the adjusted date
     */
    public abstract LocalDate adjust(LocalDate date, HolidayCalendar calendar);

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

}
