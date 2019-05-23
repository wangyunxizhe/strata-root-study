/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.marketdata;

import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.collect.result.Failure;
import com.opengamma.strata.collect.timeseries.LocalDateDoubleTimeSeries;
import com.opengamma.strata.data.*;
import org.joda.beans.*;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Market data that has been built.
 * <p>
 * The {@link MarketDataFactory} can be used to build market data from external
 * sources and by calibration. This implementation of {@link MarketData}
 * provides the result, and includes all the market data, such as quotes and curves.
 * <p>
 * This implementation differs from {@link ImmutableMarketData} because it
 * stores the failures that occurred during the build process.
 * These errors are exposed to users when data is queried.
 */
@BeanDefinition(builderScope = "private", constructorScope = "package")
public final class BuiltMarketData
    implements MarketData, ImmutableBean {

  /**
   * The underlying market data.
   */
  @PropertyDefinition(validate = "notNull")
  private final BuiltScenarioMarketData underlying;

  //-------------------------------------------------------------------------
  @Override
  public LocalDate getValuationDate() {
    return underlying.getValuationDate().getSingleValue();
  }

  @Override
  public boolean containsValue(MarketDataId<?> id) {
    return underlying.containsValue(id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue(MarketDataId<T> id) {
    return underlying.getValue(id).getSingleValue();
  }

  @Override
  public <T> Optional<T> findValue(MarketDataId<T> id) {
    return underlying.findValue(id).map(v -> v.getSingleValue());
  }

  @Override
  public Set<MarketDataId<?>> getIds() {
    return underlying.getIds();
  }

  @Override
  public <T> Set<MarketDataId<T>> findIds(MarketDataName<T> name) {
    return underlying.findIds(name);
  }

  @Override
  public Set<ObservableId> getTimeSeriesIds() {
    return underlying.getTimeSeriesIds();
  }

  @Override
  public LocalDateDoubleTimeSeries getTimeSeries(ObservableId id) {
    return underlying.getTimeSeries(id);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the failures when building single market data values.
   * 
   * @return the single value failures
   */
  public ImmutableMap<MarketDataId<?>, Failure> getValueFailures() {
    return underlying.getValueFailures();
  }

  /**
   * Gets the failures that occurred when building time series of market data values.
   * 
   * @return the time-series value failures
   */
  public ImmutableMap<MarketDataId<?>, Failure> getTimeSeriesFailures() {
    return underlying.getTimeSeriesFailures();
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code BuiltMarketData}.
   * @return the meta-bean, not null
   */
  public static BuiltMarketData.Meta meta() {
    return BuiltMarketData.Meta.INSTANCE;
  }

  static {
    MetaBean.register(BuiltMarketData.Meta.INSTANCE);
  }

  /**
   * Creates an instance.
   * @param underlying  the value of the property, not null
   */
  BuiltMarketData(
      BuiltScenarioMarketData underlying) {
    JodaBeanUtils.notNull(underlying, "underlying");
    this.underlying = underlying;
  }

  @Override
  public BuiltMarketData.Meta metaBean() {
    return BuiltMarketData.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlying market data.
   * @return the value of the property, not null
   */
  public BuiltScenarioMarketData getUnderlying() {
    return underlying;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      BuiltMarketData other = (BuiltMarketData) obj;
      return JodaBeanUtils.equal(underlying, other.underlying);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(underlying);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("BuiltMarketData{");
    buf.append("underlying").append('=').append(JodaBeanUtils.toString(underlying));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code BuiltMarketData}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code underlying} property.
     */
    private final MetaProperty<BuiltScenarioMarketData> underlying = DirectMetaProperty.ofImmutable(
        this, "underlying", BuiltMarketData.class, BuiltScenarioMarketData.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "underlying");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1770633379:  // underlying
          return underlying;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends BuiltMarketData> builder() {
      return new BuiltMarketData.Builder();
    }

    @Override
    public Class<? extends BuiltMarketData> beanType() {
      return BuiltMarketData.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code underlying} property.
     * @return the meta-property, not null
     */
    public MetaProperty<BuiltScenarioMarketData> underlying() {
      return underlying;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1770633379:  // underlying
          return ((BuiltMarketData) bean).getUnderlying();
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
   * The bean-builder for {@code BuiltMarketData}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<BuiltMarketData> {

    private BuiltScenarioMarketData underlying;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1770633379:  // underlying
          return underlying;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1770633379:  // underlying
          this.underlying = (BuiltScenarioMarketData) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public BuiltMarketData build() {
      return new BuiltMarketData(
          underlying);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("BuiltMarketData.Builder{");
      buf.append("underlying").append('=').append(JodaBeanUtils.toString(underlying));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
