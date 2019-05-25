/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.date;

import com.opengamma.strata.collect.ArgChecker;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.opengamma.strata.basics.date.LocalDateUtils.plusDays;

/**
 * 没有假日的HolidayCalendar对象实现。
 * <p>
 * 此{@link HolidayCalendar}的final实现类将所有日期视为有效的工作日。
 */
@BeanDefinition(builderScope = "private")
final class NoHolidaysCalendar
        implements HolidayCalendar, ImmutableBean, Serializable {
    // methods are overridden from default implementations to gain performance

    /**
     * 单例模式
     */
    static final NoHolidaysCalendar INSTANCE = new NoHolidaysCalendar();

    // resolve after deserialization
    private Object readResolve() {
        return INSTANCE;
    }

    //-------------------------------------------------------------------------
    @Override
    public HolidayCalendarId getId() {
        return HolidayCalendarIds.NO_HOLIDAYS;
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        return false;
    }

    @Override
    public boolean isBusinessDay(LocalDate date) {
        return true;
    }

    @Override
    public LocalDate shift(LocalDate date, int amount) {
        return plusDays(date, amount);
    }

    @Override
    public LocalDate next(LocalDate date) {
        return plusDays(date, 1);
    }

    @Override
    public LocalDate nextOrSame(LocalDate date) {
        return ArgChecker.notNull(date, "date");
    }

    @Override
    public LocalDate previous(LocalDate date) {
        return plusDays(date, -1);
    }

    @Override
    public LocalDate previousOrSame(LocalDate date) {
        return ArgChecker.notNull(date, "date");
    }

    @Override
    public LocalDate nextSameOrLastInMonth(LocalDate date) {
        return ArgChecker.notNull(date, "date");
    }

    @Override
    public int daysBetween(LocalDate startInclusive, LocalDate endExclusive) {
        return Math.toIntExact(LocalDateUtils.daysBetween(startInclusive, endExclusive));
    }

    @Override
    public HolidayCalendar combinedWith(HolidayCalendar other) {
        return ArgChecker.notNull(other, "other");
    }

    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        return obj instanceof NoHolidaysCalendar;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "HolidayCalendar[NoHolidays]";
    }

    //------------------------- 自动生成 开始 -------------------------

    /**
     * The meta-bean for {@code NoHolidaysCalendar}.
     *
     * @return the meta-bean, not null
     */
    public static NoHolidaysCalendar.Meta meta() {
        return NoHolidaysCalendar.Meta.INSTANCE;
    }

    static {
        MetaBean.register(NoHolidaysCalendar.Meta.INSTANCE);
    }

    /**
     * The serialization version id.
     */
    private static final long serialVersionUID = 1L;

    private NoHolidaysCalendar() {
    }

    @Override
    public NoHolidaysCalendar.Meta metaBean() {
        return NoHolidaysCalendar.Meta.INSTANCE;
    }

    //-----------------------------------------------------------------------

    /**
     * The meta-bean for {@code NoHolidaysCalendar}.
     */
    static final class Meta extends DirectMetaBean {
        /**
         * The singleton instance of the meta-bean.
         */
        static final Meta INSTANCE = new Meta();

        /**
         * The meta-properties.
         */
        private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
                this, null);

        /**
         * Restricted constructor.
         */
        private Meta() {
        }

        @Override
        public BeanBuilder<? extends NoHolidaysCalendar> builder() {
            return new NoHolidaysCalendar.Builder();
        }

        @Override
        public Class<? extends NoHolidaysCalendar> beanType() {
            return NoHolidaysCalendar.class;
        }

        @Override
        public Map<String, MetaProperty<?>> metaPropertyMap() {
            return metaPropertyMap$;
        }

        //-----------------------------------------------------------------------
    }

    //-----------------------------------------------------------------------

    /**
     * The bean-builder for {@code NoHolidaysCalendar}.
     */
    private static final class Builder extends DirectPrivateBeanBuilder<NoHolidaysCalendar> {

        /**
         * Restricted constructor.
         */
        private Builder() {
        }

        //-----------------------------------------------------------------------
        @Override
        public Object get(String propertyName) {
            throw new NoSuchElementException("Unknown property: " + propertyName);
        }

        @Override
        public Builder set(String propertyName, Object newValue) {
            throw new NoSuchElementException("Unknown property: " + propertyName);
        }

        @Override
        public NoHolidaysCalendar build() {
            return new NoHolidaysCalendar();
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            return "NoHolidaysCalendar.Builder{}";
        }

    }

    //-------------------------- 自动生成 结束 --------------------------
}
