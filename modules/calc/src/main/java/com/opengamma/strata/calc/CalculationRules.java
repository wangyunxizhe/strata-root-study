/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc;

import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.calc.runner.CalculationFunction;
import com.opengamma.strata.calc.runner.CalculationFunctions;
import com.opengamma.strata.calc.runner.CalculationParameter;
import com.opengamma.strata.calc.runner.CalculationParameters;
import org.joda.beans.*;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutableDefaults;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 定义计算运行程序执行计算的规则。
 * <p>
 * {@link CalculationRunner}提供对许多目标（如trades和positions）进行计算的能力。
 * 它返回一个结果网格，目标为行。每个单独的计算由三个因素控制：
 * <ul>
 * <li>按目标类型选择的{@linkplain CalculationFunction function}函数</li>
 * <li>{@linkplain Measure measure}，要计算的高级输出</li>
 * <li>{@linkplain CalculationParameters parameters}, 调整如何计算度量值</li>
 * </ul>
 * {@code CalculationRules}与{@link Column}一起操作。
 * 列用于定义度量值。它还可以用于指定列特定的参数。
 * 规则（rules）包含完整的函数集（functions）和默认的参数集。
 */
@BeanDefinition(builderScope = "private")
public final class CalculationRules implements ImmutableBean {

    /**
     * 计算功能。
     * <p>
     * 函数提供计算逻辑。每种类型的目标都必须有一个关联的CalculationFunctions才能执行计算。
     */
    @PropertyDefinition(validate = "notNull")
    private final CalculationFunctions functions;
    /**
     * 用于控制货币转换的报告货币。
     * <p>
     * 用于指定结果应报告的货币。如果结果与货币不关联，例如“票面利率”，则报告货币将被有效忽略。
     */
    @PropertyDefinition(validate = "notNull")
    private final ReportingCurrency reportingCurrency;
    /**
     * 用于控制计算的执行方式。
     * <p>
     * Parameters用于参数化要计算的{@link Measure}。
     * 它们可以在两个位置指定--此处和{@link Column}。
     * 此处指定的参数是应用于所有列的默认值。
     * <p>
     * 如果在此处和列中定义的参数具有相同的{@linkplain CalculationParameter#queryType() query type}，则列参数优先。
     */
    @PropertyDefinition(validate = "notNull")
    private final CalculationParameters parameters;

    //-------------------------------------------------------------------------

    /**
     * Obtains an instance specifying the functions to use and some additional parameters.
     * <p>
     * The output will uses the "natural" {@linkplain ReportingCurrency reporting currency}.
     * Most functions require a parameter to control their behavior, such as {@code RatesMarketDataLookup}.
     *
     * @param functions  the calculation functions
     * @param parameters the parameters that control the calculation, may be empty
     * @return the rules
     */
    public static CalculationRules of(CalculationFunctions functions, CalculationParameter... parameters) {
        CalculationParameters params = CalculationParameters.of(parameters);
        return new CalculationRules(functions, ReportingCurrency.NATURAL, params);
    }

    /**
     * Obtains an instance specifying the functions to use and some additional parameters.
     * <p>
     * The output will uses the "natural" {@linkplain ReportingCurrency reporting currency}.
     * Most functions require a parameter to control their behavior, such as {@code RatesMarketDataLookup}.
     *
     * @param functions  the calculation functions
     * @param parameters the parameters that control the calculation, may be empty
     * @return the rules
     */
    public static CalculationRules of(CalculationFunctions functions, CalculationParameters parameters) {
        return new CalculationRules(functions, ReportingCurrency.NATURAL, parameters);
    }

    /**
     * Obtains an instance specifying the functions, reporting currency and additional parameters.
     * <p>
     * Most functions require a parameter to control their behavior, such as {@code RatesMarketDataLookup}.
     *
     * @param functions         the calculation functions
     * @param reportingCurrency the reporting currency
     * @param parameters        the parameters that control the calculation, may be empty
     * @return the rules
     */
    public static CalculationRules of(
            CalculationFunctions functions,
            Currency reportingCurrency,
            CalculationParameter... parameters) {

        CalculationParameters params = CalculationParameters.of(parameters);
        return new CalculationRules(functions, ReportingCurrency.of(reportingCurrency), params);
    }

    /**
     * Obtains an instance specifying the functions, reporting currency and additional parameters.
     * <p>
     * Most functions require a parameter to control their behavior, such as {@code RatesMarketDataLookup}.
     *
     * @param functions         the calculation functions
     * @param reportingCurrency the reporting currency
     * @param parameters        the parameters that control the calculation, may be empty
     * @return the rules
     */
    public static CalculationRules of(
            CalculationFunctions functions,
            ReportingCurrency reportingCurrency,
            CalculationParameters parameters) {

        return new CalculationRules(functions, reportingCurrency, parameters);
    }

    @ImmutableDefaults
    private static void applyDefaults(Builder builder) {
        builder.parameters = CalculationParameters.empty();
    }

    //------------------------- AUTOGENERATED START -------------------------

    /**
     * The meta-bean for {@code CalculationRules}.
     *
     * @return the meta-bean, not null
     */
    public static CalculationRules.Meta meta() {
        return CalculationRules.Meta.INSTANCE;
    }

    static {
        MetaBean.register(CalculationRules.Meta.INSTANCE);
    }

    private CalculationRules(
            CalculationFunctions functions,
            ReportingCurrency reportingCurrency,
            CalculationParameters parameters) {
        JodaBeanUtils.notNull(functions, "functions");
        JodaBeanUtils.notNull(reportingCurrency, "reportingCurrency");
        JodaBeanUtils.notNull(parameters, "parameters");
        this.functions = functions;
        this.reportingCurrency = reportingCurrency;
        this.parameters = parameters;
    }

    @Override
    public CalculationRules.Meta metaBean() {
        return CalculationRules.Meta.INSTANCE;
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the calculation functions.
     * <p>
     * Functions provide the logic of the calculation.
     * Each type of target must have an associated function in order for calculations to be performed.
     *
     * @return the value of the property, not null
     */
    public CalculationFunctions getFunctions() {
        return functions;
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the reporting currency, used to control currency conversion.
     * <p>
     * This is used to specify the currency that the result should be reporting in.
     * If the result is not associated with a currency, such as for "par rate", then the
     * reporting currency will effectively be ignored.
     *
     * @return the value of the property, not null
     */
    public ReportingCurrency getReportingCurrency() {
        return reportingCurrency;
    }

    //-----------------------------------------------------------------------

    /**
     * Gets the calculation parameters, used to control the how the calculation is performed.
     * <p>
     * Parameters are used to parameterize the {@link Measure} to be calculated.
     * They may be specified in two places - here and in the {@link Column}.
     * The parameters specified here are the defaults that apply to all columns.
     * <p>
     * If a parameter is defined here and in the column with the same
     * {@linkplain CalculationParameter#queryType() query type}, then the column parameter takes precedence.
     *
     * @return the value of the property, not null
     */
    public CalculationParameters getParameters() {
        return parameters;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            CalculationRules other = (CalculationRules) obj;
            return JodaBeanUtils.equal(functions, other.functions) &&
                    JodaBeanUtils.equal(reportingCurrency, other.reportingCurrency) &&
                    JodaBeanUtils.equal(parameters, other.parameters);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        hash = hash * 31 + JodaBeanUtils.hashCode(functions);
        hash = hash * 31 + JodaBeanUtils.hashCode(reportingCurrency);
        hash = hash * 31 + JodaBeanUtils.hashCode(parameters);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append("CalculationRules{");
        buf.append("functions").append('=').append(functions).append(',').append(' ');
        buf.append("reportingCurrency").append('=').append(reportingCurrency).append(',').append(' ');
        buf.append("parameters").append('=').append(JodaBeanUtils.toString(parameters));
        buf.append('}');
        return buf.toString();
    }

    //-----------------------------------------------------------------------

    /**
     * The meta-bean for {@code CalculationRules}.
     */
    public static final class Meta extends DirectMetaBean {
        /**
         * The singleton instance of the meta-bean.
         */
        static final Meta INSTANCE = new Meta();

        /**
         * The meta-property for the {@code functions} property.
         */
        private final MetaProperty<CalculationFunctions> functions = DirectMetaProperty.ofImmutable(
                this, "functions", CalculationRules.class, CalculationFunctions.class);
        /**
         * The meta-property for the {@code reportingCurrency} property.
         */
        private final MetaProperty<ReportingCurrency> reportingCurrency = DirectMetaProperty.ofImmutable(
                this, "reportingCurrency", CalculationRules.class, ReportingCurrency.class);
        /**
         * The meta-property for the {@code parameters} property.
         */
        private final MetaProperty<CalculationParameters> parameters = DirectMetaProperty.ofImmutable(
                this, "parameters", CalculationRules.class, CalculationParameters.class);
        /**
         * The meta-properties.
         */
        private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
                this, null,
                "functions",
                "reportingCurrency",
                "parameters");

        /**
         * Restricted constructor.
         */
        private Meta() {
        }

        @Override
        protected MetaProperty<?> metaPropertyGet(String propertyName) {
            switch (propertyName.hashCode()) {
                case -140572773:  // functions
                    return functions;
                case -1287844769:  // reportingCurrency
                    return reportingCurrency;
                case 458736106:  // parameters
                    return parameters;
            }
            return super.metaPropertyGet(propertyName);
        }

        @Override
        public BeanBuilder<? extends CalculationRules> builder() {
            return new CalculationRules.Builder();
        }

        @Override
        public Class<? extends CalculationRules> beanType() {
            return CalculationRules.class;
        }

        @Override
        public Map<String, MetaProperty<?>> metaPropertyMap() {
            return metaPropertyMap$;
        }

        //-----------------------------------------------------------------------

        /**
         * The meta-property for the {@code functions} property.
         *
         * @return the meta-property, not null
         */
        public MetaProperty<CalculationFunctions> functions() {
            return functions;
        }

        /**
         * The meta-property for the {@code reportingCurrency} property.
         *
         * @return the meta-property, not null
         */
        public MetaProperty<ReportingCurrency> reportingCurrency() {
            return reportingCurrency;
        }

        /**
         * The meta-property for the {@code parameters} property.
         *
         * @return the meta-property, not null
         */
        public MetaProperty<CalculationParameters> parameters() {
            return parameters;
        }

        //-----------------------------------------------------------------------
        @Override
        protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
            switch (propertyName.hashCode()) {
                case -140572773:  // functions
                    return ((CalculationRules) bean).getFunctions();
                case -1287844769:  // reportingCurrency
                    return ((CalculationRules) bean).getReportingCurrency();
                case 458736106:  // parameters
                    return ((CalculationRules) bean).getParameters();
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
     * The bean-builder for {@code CalculationRules}.
     */
    private static final class Builder extends DirectPrivateBeanBuilder<CalculationRules> {

        private CalculationFunctions functions;
        private ReportingCurrency reportingCurrency;
        private CalculationParameters parameters;

        /**
         * Restricted constructor.
         */
        private Builder() {
            applyDefaults(this);
        }

        //-----------------------------------------------------------------------
        @Override
        public Object get(String propertyName) {
            switch (propertyName.hashCode()) {
                case -140572773:  // functions
                    return functions;
                case -1287844769:  // reportingCurrency
                    return reportingCurrency;
                case 458736106:  // parameters
                    return parameters;
                default:
                    throw new NoSuchElementException("Unknown property: " + propertyName);
            }
        }

        @Override
        public Builder set(String propertyName, Object newValue) {
            switch (propertyName.hashCode()) {
                case -140572773:  // functions
                    this.functions = (CalculationFunctions) newValue;
                    break;
                case -1287844769:  // reportingCurrency
                    this.reportingCurrency = (ReportingCurrency) newValue;
                    break;
                case 458736106:  // parameters
                    this.parameters = (CalculationParameters) newValue;
                    break;
                default:
                    throw new NoSuchElementException("Unknown property: " + propertyName);
            }
            return this;
        }

        @Override
        public CalculationRules build() {
            return new CalculationRules(
                    functions,
                    reportingCurrency,
                    parameters);
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder(128);
            buf.append("CalculationRules.Builder{");
            buf.append("functions").append('=').append(JodaBeanUtils.toString(functions)).append(',').append(' ');
            buf.append("reportingCurrency").append('=').append(JodaBeanUtils.toString(reportingCurrency)).append(',').append(' ');
            buf.append("parameters").append('=').append(JodaBeanUtils.toString(parameters));
            buf.append('}');
            return buf.toString();
        }

    }

    //-------------------------- AUTOGENERATED END --------------------------
}
