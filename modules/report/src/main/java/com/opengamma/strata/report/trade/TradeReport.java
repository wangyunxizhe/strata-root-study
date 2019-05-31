/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.report.trade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.opengamma.strata.collect.result.Result;
import com.opengamma.strata.report.Report;
import com.opengamma.strata.report.ReportCalculationResults;
import org.joda.beans.*;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.opengamma.strata.collect.Guavate.toImmutableList;

/**
 * 最终生成的交易报告类
 */
@BeanDefinition
public final class TradeReport
        implements Report, ImmutableBean {

    /**
     * 估价日期。
     */
    @PropertyDefinition(validate = "notNull", overrideGet = true)
    private final LocalDate valuationDate;
    /**
     * 运行报告的瞬间。
     * java8新特性：所谓的Instant类代表的是某个时间（有点像 java.util.Date），
     * 它是精确到纳秒的（而不是象旧版本的Date精确到毫秒）。
     */
    @PropertyDefinition(validate = "notNull", overrideGet = true)
    private final Instant runInstant;
    /**
     * 报表列，其中可能包含格式化所需的信息。
     */
    @PropertyDefinition(validate = "notNull")
    private final ImmutableList<TradeReportColumn> columns;
    /**
     * The calculation results.
     */
    @PropertyDefinition(validate = "notNull")
    private final ImmutableTable<Integer, Integer, Result<?>> data;

    //-------------------------------------------------------------------------

    /**
     * Returns a new trade report.
     *
     * @param calculationResults the results of the calculations
     * @param reportTemplate     the template used to generate the report
     * @return a new trade report
     */
    public static TradeReport of(ReportCalculationResults calculationResults, TradeReportTemplate reportTemplate) {
        return TradeReportRunner.INSTANCE.runReport(calculationResults, reportTemplate);
    }

    //-------------------------------------------------------------------------
    @Override
    public int getRowCount() {
        return data.rowKeySet().size();
    }

    @Override
    public ImmutableList<String> getColumnHeaders() {
        return columns.stream().map(c -> c.getHeader()).collect(toImmutableList());
    }

    //-------------------------------------------------------------------------
    @Override
    public void writeCsv(OutputStream out) {
        TradeReportFormatter.INSTANCE.writeCsv(this, out);
    }

    @Override
    public void writeAsciiTable(OutputStream out) {
        TradeReportFormatter.INSTANCE.writeAsciiTable(this, out);
    }

    //------------------------- AUTOGENERATED START -------------------------

    /**
     * The meta-bean for {@code TradeReport}.
     *
     * @return the meta-bean, not null
     */
    public static TradeReport.Meta meta() {
        return TradeReport.Meta.INSTANCE;
    }

    static {
        MetaBean.register(TradeReport.Meta.INSTANCE);
    }

    /**
     * Returns a builder used to create an instance of the bean.
     *
     * @return the builder, not null
     */
    public static TradeReport.Builder builder() {
        return new TradeReport.Builder();
    }

    private TradeReport(
            LocalDate valuationDate,
            Instant runInstant,
            List<TradeReportColumn> columns,
            Table<Integer, Integer, Result<?>> data) {
        JodaBeanUtils.notNull(valuationDate, "valuationDate");
        JodaBeanUtils.notNull(runInstant, "runInstant");
        JodaBeanUtils.notNull(columns, "columns");
        JodaBeanUtils.notNull(data, "data");
        this.valuationDate = valuationDate;
        this.runInstant = runInstant;
        this.columns = ImmutableList.copyOf(columns);
        this.data = ImmutableTable.copyOf(data);
    }

    @Override
    public TradeReport.Meta metaBean() {
        return TradeReport.Meta.INSTANCE;
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the valuation date.
     *
     * @return the value of the property, not null
     */
    @Override
    public LocalDate getValuationDate() {
        return valuationDate;
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the instant at which the report was run.
     *
     * @return the value of the property, not null
     */
    @Override
    public Instant getRunInstant() {
        return runInstant;
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the report columns, which may contain information required for formatting.
     *
     * @return the value of the property, not null
     */
    public ImmutableList<TradeReportColumn> getColumns() {
        return columns;
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the calculation results.
     *
     * @return the value of the property, not null
     */
    public ImmutableTable<Integer, Integer, Result<?>> getData() {
        return data;
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a builder that allows this bean to be mutated.
     *
     * @return the mutable builder, not null
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            TradeReport other = (TradeReport) obj;
            return JodaBeanUtils.equal(valuationDate, other.valuationDate) &&
                    JodaBeanUtils.equal(runInstant, other.runInstant) &&
                    JodaBeanUtils.equal(columns, other.columns) &&
                    JodaBeanUtils.equal(data, other.data);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        hash = hash * 31 + JodaBeanUtils.hashCode(valuationDate);
        hash = hash * 31 + JodaBeanUtils.hashCode(runInstant);
        hash = hash * 31 + JodaBeanUtils.hashCode(columns);
        hash = hash * 31 + JodaBeanUtils.hashCode(data);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(160);
        buf.append("TradeReport{");
        buf.append("valuationDate").append('=').append(valuationDate).append(',').append(' ');
        buf.append("runInstant").append('=').append(runInstant).append(',').append(' ');
        buf.append("columns").append('=').append(columns).append(',').append(' ');
        buf.append("data").append('=').append(JodaBeanUtils.toString(data));
        buf.append('}');
        return buf.toString();
    }

    //-----------------------------------------------------------------------

    /**
     * The meta-bean for {@code TradeReport}.
     */
    public static final class Meta extends DirectMetaBean {
        /**
         * The singleton instance of the meta-bean.
         */
        static final Meta INSTANCE = new Meta();

        /**
         * The meta-property for the {@code valuationDate} property.
         */
        private final MetaProperty<LocalDate> valuationDate = DirectMetaProperty.ofImmutable(
                this, "valuationDate", TradeReport.class, LocalDate.class);
        /**
         * The meta-property for the {@code runInstant} property.
         */
        private final MetaProperty<Instant> runInstant = DirectMetaProperty.ofImmutable(
                this, "runInstant", TradeReport.class, Instant.class);
        /**
         * The meta-property for the {@code columns} property.
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private final MetaProperty<ImmutableList<TradeReportColumn>> columns = DirectMetaProperty.ofImmutable(
                this, "columns", TradeReport.class, (Class) ImmutableList.class);
        /**
         * The meta-property for the {@code data} property.
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private final MetaProperty<ImmutableTable<Integer, Integer, Result<?>>> data = DirectMetaProperty.ofImmutable(
                this, "data", TradeReport.class, (Class) ImmutableTable.class);
        /**
         * The meta-properties.
         */
        private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
                this, null,
                "valuationDate",
                "runInstant",
                "columns",
                "data");

        /**
         * Restricted constructor.
         */
        private Meta() {
        }

        @Override
        protected MetaProperty<?> metaPropertyGet(String propertyName) {
            switch (propertyName.hashCode()) {
                case 113107279:  // valuationDate
                    return valuationDate;
                case 111354070:  // runInstant
                    return runInstant;
                case 949721053:  // columns
                    return columns;
                case 3076010:  // data
                    return data;
            }
            return super.metaPropertyGet(propertyName);
        }

        @Override
        public TradeReport.Builder builder() {
            return new TradeReport.Builder();
        }

        @Override
        public Class<? extends TradeReport> beanType() {
            return TradeReport.class;
        }

        @Override
        public Map<String, MetaProperty<?>> metaPropertyMap() {
            return metaPropertyMap$;
        }

        //-----------------------------------------------------------------------

        /**
         * The meta-property for the {@code valuationDate} property.
         *
         * @return the meta-property, not null
         */
        public MetaProperty<LocalDate> valuationDate() {
            return valuationDate;
        }

        /**
         * The meta-property for the {@code runInstant} property.
         *
         * @return the meta-property, not null
         */
        public MetaProperty<Instant> runInstant() {
            return runInstant;
        }

        /**
         * The meta-property for the {@code columns} property.
         *
         * @return the meta-property, not null
         */
        public MetaProperty<ImmutableList<TradeReportColumn>> columns() {
            return columns;
        }

        /**
         * The meta-property for the {@code data} property.
         *
         * @return the meta-property, not null
         */
        public MetaProperty<ImmutableTable<Integer, Integer, Result<?>>> data() {
            return data;
        }

        //-----------------------------------------------------------------------
        @Override
        protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
            switch (propertyName.hashCode()) {
                case 113107279:  // valuationDate
                    return ((TradeReport) bean).getValuationDate();
                case 111354070:  // runInstant
                    return ((TradeReport) bean).getRunInstant();
                case 949721053:  // columns
                    return ((TradeReport) bean).getColumns();
                case 3076010:  // data
                    return ((TradeReport) bean).getData();
            }
            return super.propertyGet(bean, propertyName, quiet);
        }

        @Override
        protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
            metaProperty(propertyName);
            if (quiet) {
                return;
            }
            throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
        }

    }

    //-----------------------------------------------------------------------

    /**
     * The bean-builder for {@code TradeReport}.
     */
    public static final class Builder extends DirectFieldsBeanBuilder<TradeReport> {

        private LocalDate valuationDate;
        private Instant runInstant;
        private List<TradeReportColumn> columns = ImmutableList.of();
        private Table<Integer, Integer, Result<?>> data = ImmutableTable.of();

        /**
         * Restricted constructor.
         */
        private Builder() {
        }

        /**
         * Restricted copy constructor.
         *
         * @param beanToCopy the bean to copy from, not null
         */
        private Builder(TradeReport beanToCopy) {
            this.valuationDate = beanToCopy.getValuationDate();
            this.runInstant = beanToCopy.getRunInstant();
            this.columns = beanToCopy.getColumns();
            this.data = beanToCopy.getData();
        }

        //-----------------------------------------------------------------------
        @Override
        public Object get(String propertyName) {
            switch (propertyName.hashCode()) {
                case 113107279:  // valuationDate
                    return valuationDate;
                case 111354070:  // runInstant
                    return runInstant;
                case 949721053:  // columns
                    return columns;
                case 3076010:  // data
                    return data;
                default:
                    throw new NoSuchElementException("Unknown property: " + propertyName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Builder set(String propertyName, Object newValue) {
            switch (propertyName.hashCode()) {
                case 113107279:  // valuationDate
                    this.valuationDate = (LocalDate) newValue;
                    break;
                case 111354070:  // runInstant
                    this.runInstant = (Instant) newValue;
                    break;
                case 949721053:  // columns
                    this.columns = (List<TradeReportColumn>) newValue;
                    break;
                case 3076010:  // data
                    this.data = (Table<Integer, Integer, Result<?>>) newValue;
                    break;
                default:
                    throw new NoSuchElementException("Unknown property: " + propertyName);
            }
            return this;
        }

        @Override
        public Builder set(MetaProperty<?> property, Object value) {
            super.set(property, value);
            return this;
        }

        @Override
        public TradeReport build() {
            return new TradeReport(
                    valuationDate,
                    runInstant,
                    columns,
                    data);
        }

        //-----------------------------------------------------------------------

        /**
         * Sets the valuation date.
         *
         * @param valuationDate the new value, not null
         * @return this, for chaining, not null
         */
        public Builder valuationDate(LocalDate valuationDate) {
            JodaBeanUtils.notNull(valuationDate, "valuationDate");
            this.valuationDate = valuationDate;
            return this;
        }

        /**
         * Sets the instant at which the report was run.
         *
         * @param runInstant the new value, not null
         * @return this, for chaining, not null
         */
        public Builder runInstant(Instant runInstant) {
            JodaBeanUtils.notNull(runInstant, "runInstant");
            this.runInstant = runInstant;
            return this;
        }

        /**
         * Sets the report columns, which may contain information required for formatting.
         *
         * @param columns the new value, not null
         * @return this, for chaining, not null
         */
        public Builder columns(List<TradeReportColumn> columns) {
            JodaBeanUtils.notNull(columns, "columns");
            this.columns = columns;
            return this;
        }

        /**
         * Sets the {@code columns} property in the builder
         * from an array of objects.
         *
         * @param columns the new value, not null
         * @return this, for chaining, not null
         */
        public Builder columns(TradeReportColumn... columns) {
            return columns(ImmutableList.copyOf(columns));
        }

        /**
         * Sets the calculation results.
         *
         * @param data the new value, not null
         * @return this, for chaining, not null
         */
        public Builder data(Table<Integer, Integer, Result<?>> data) {
            JodaBeanUtils.notNull(data, "data");
            this.data = data;
            return this;
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder(160);
            buf.append("TradeReport.Builder{");
            buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(valuationDate)).append(',').append(' ');
            buf.append("runInstant").append('=').append(JodaBeanUtils.toString(runInstant)).append(',').append(' ');
            buf.append("columns").append('=').append(JodaBeanUtils.toString(columns)).append(',').append(' ');
            buf.append("data").append('=').append(JodaBeanUtils.toString(data));
            buf.append('}');
            return buf.toString();
        }

    }

    //-------------------------- AUTOGENERATED END --------------------------
}
