/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure.fx;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.joda.beans.Bean;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutableValidator;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.basics.currency.CurrencyPair;
import com.opengamma.strata.basics.currency.FxRate;
import com.opengamma.strata.collect.Messages;
import com.opengamma.strata.market.observable.QuoteId;

/**
 * Configuration defining how to create {@link FxRate} instances from observable market data.
 * <p>
 * Currently this only supports rates which are observable in the market. Cross rates derived from other
 * rates will be supported later.
 * <p>
 * This class is likely to change when support for cross rates is added.
 * <p>
 * When populating this class all currency pairs must be quoted using the market conventions.
 */
@BeanDefinition
public final class FxRateConfig implements ImmutableBean {

  /**
   * The keys identifying FX rates which are observable in the market.
   * Each entry is keyed by the conventional currency pair.
   */
  @PropertyDefinition(validate = "notNull", get = "private")
  private final ImmutableMap<CurrencyPair, QuoteId> observableRates;

  /**
   * Returns a key identifying the market quote for an observable FX rate.
   * <p>
   * If the FX rate is not observable in the market an empty optional is returned.
   * <p>
   * It is possible the quote is for the rate of the inverse of the currency pair. This does not matter as
   * the market data system ensures that the correct rate is always provided regardless of which way round
   * the pair is quoted.
   *
   * @param currencyPair  the currency pair
   * @return a key identifying the market quote for the rate if it is observable in the market
   */
  public Optional<QuoteId> getObservableRateKey(CurrencyPair currencyPair) {
    QuoteId quoteId = observableRates.get(currencyPair.toConventional());
    return Optional.ofNullable(quoteId);
  }

  /**
   * Returns FX rate configuration built using the data in the map.
   * 
   * @param quotesMap  map of currency pairs to the market quotes defining their rates
   * @return FX rate configuration built using the data in the map
   */
  public static FxRateConfig of(Map<CurrencyPair, QuoteId> quotesMap) {
    return new FxRateConfig(quotesMap);
  }

  @ImmutableValidator
  private void validate() {
    for (CurrencyPair currencyPair : observableRates.keySet()) {
      if (!currencyPair.isConventional()) {
        throw new IllegalArgumentException(
            Messages.format(
                "Currency pairs must be quoted using market conventions but {} is not",
                currencyPair));
      }
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code FxRateConfig}.
   * @return the meta-bean, not null
   */
  public static FxRateConfig.Meta meta() {
    return FxRateConfig.Meta.INSTANCE;
  }

  static {
    MetaBean.register(FxRateConfig.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static FxRateConfig.Builder builder() {
    return new FxRateConfig.Builder();
  }

  private FxRateConfig(
      Map<CurrencyPair, QuoteId> observableRates) {
    JodaBeanUtils.notNull(observableRates, "observableRates");
    this.observableRates = ImmutableMap.copyOf(observableRates);
    validate();
  }

  @Override
  public FxRateConfig.Meta metaBean() {
    return FxRateConfig.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the keys identifying FX rates which are observable in the market.
   * Each entry is keyed by the conventional currency pair.
   * @return the value of the property, not null
   */
  private ImmutableMap<CurrencyPair, QuoteId> getObservableRates() {
    return observableRates;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
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
      FxRateConfig other = (FxRateConfig) obj;
      return JodaBeanUtils.equal(observableRates, other.observableRates);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(observableRates);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("FxRateConfig{");
    buf.append("observableRates").append('=').append(JodaBeanUtils.toString(observableRates));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FxRateConfig}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code observableRates} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableMap<CurrencyPair, QuoteId>> observableRates = DirectMetaProperty.ofImmutable(
        this, "observableRates", FxRateConfig.class, (Class) ImmutableMap.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "observableRates");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1996176400:  // observableRates
          return observableRates;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public FxRateConfig.Builder builder() {
      return new FxRateConfig.Builder();
    }

    @Override
    public Class<? extends FxRateConfig> beanType() {
      return FxRateConfig.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code observableRates} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableMap<CurrencyPair, QuoteId>> observableRates() {
      return observableRates;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1996176400:  // observableRates
          return ((FxRateConfig) bean).getObservableRates();
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
   * The bean-builder for {@code FxRateConfig}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<FxRateConfig> {

    private Map<CurrencyPair, QuoteId> observableRates = ImmutableMap.of();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(FxRateConfig beanToCopy) {
      this.observableRates = beanToCopy.getObservableRates();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1996176400:  // observableRates
          return observableRates;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1996176400:  // observableRates
          this.observableRates = (Map<CurrencyPair, QuoteId>) newValue;
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
    public FxRateConfig build() {
      return new FxRateConfig(
          observableRates);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the keys identifying FX rates which are observable in the market.
     * Each entry is keyed by the conventional currency pair.
     * @param observableRates  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder observableRates(Map<CurrencyPair, QuoteId> observableRates) {
      JodaBeanUtils.notNull(observableRates, "observableRates");
      this.observableRates = observableRates;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("FxRateConfig.Builder{");
      buf.append("observableRates").append('=').append(JodaBeanUtils.toString(observableRates));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}