/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.rate;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutableValidator;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.index.Index;
import com.opengamma.strata.basics.index.PriceIndex;
import com.opengamma.strata.basics.index.PriceIndexObservation;
import com.opengamma.strata.collect.ArgChecker;

/**
 * Defines the computation of inflation figures from a price index.
 * <p>
 * A price index is typically published monthly and has a delay before publication.
 * The rate observed by this instance will be based on two observations of the index,
 * one relative to the accrual start date and one relative to the accrual end date.
 */
@BeanDefinition(builderScope = "private")
public final class InflationMonthlyRateComputation
    implements RateComputation, ImmutableBean, Serializable {

  /**
   * The observation at the start.
   * <p>
   * The inflation rate is the ratio between the start and end observation.
   * The start month is typically three months before the start of the period.
   */
  @PropertyDefinition(validate = "notNull")
  private final PriceIndexObservation startObservation;
  /**
   * The observation at the end.
   * <p>
   * The inflation rate is the ratio between the start and end observation.
   * The end month is typically three months before the end of the period.
   */
  @PropertyDefinition(validate = "notNull")
  private final PriceIndexObservation endObservation;

  //-------------------------------------------------------------------------
  /**
   * Creates an instance from an index, reference start month and reference end month.
   * 
   * @param index  the index
   * @param referenceStartMonth  the reference start month
   * @param referenceEndMonth  the reference end month
   * @return the inflation rate computation
   */
  public static InflationMonthlyRateComputation of(
      PriceIndex index,
      YearMonth referenceStartMonth,
      YearMonth referenceEndMonth) {

    return new InflationMonthlyRateComputation(
        PriceIndexObservation.of(index, referenceStartMonth),
        PriceIndexObservation.of(index, referenceEndMonth));
  }

  @ImmutableValidator
  private void validate() {
    ArgChecker.isTrue(
        startObservation.getIndex().equals(endObservation.getIndex()), "Both observations must be for the same index");
    ArgChecker.inOrderNotEqual(
        startObservation.getFixingMonth(), endObservation.getFixingMonth(), "referenceStartMonth", "referenceEndMonth");
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the Price index.
   * 
   * @return the Price index
   */
  public PriceIndex getIndex() {
    return startObservation.getIndex();
  }

  //-------------------------------------------------------------------------
  @Override
  public void collectIndices(ImmutableSet.Builder<Index> builder) {
    builder.add(getIndex());
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code InflationMonthlyRateComputation}.
   * @return the meta-bean, not null
   */
  public static InflationMonthlyRateComputation.Meta meta() {
    return InflationMonthlyRateComputation.Meta.INSTANCE;
  }

  static {
    MetaBean.register(InflationMonthlyRateComputation.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private InflationMonthlyRateComputation(
      PriceIndexObservation startObservation,
      PriceIndexObservation endObservation) {
    JodaBeanUtils.notNull(startObservation, "startObservation");
    JodaBeanUtils.notNull(endObservation, "endObservation");
    this.startObservation = startObservation;
    this.endObservation = endObservation;
    validate();
  }

  @Override
  public InflationMonthlyRateComputation.Meta metaBean() {
    return InflationMonthlyRateComputation.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the observation at the start.
   * <p>
   * The inflation rate is the ratio between the start and end observation.
   * The start month is typically three months before the start of the period.
   * @return the value of the property, not null
   */
  public PriceIndexObservation getStartObservation() {
    return startObservation;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the observation at the end.
   * <p>
   * The inflation rate is the ratio between the start and end observation.
   * The end month is typically three months before the end of the period.
   * @return the value of the property, not null
   */
  public PriceIndexObservation getEndObservation() {
    return endObservation;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      InflationMonthlyRateComputation other = (InflationMonthlyRateComputation) obj;
      return JodaBeanUtils.equal(startObservation, other.startObservation) &&
          JodaBeanUtils.equal(endObservation, other.endObservation);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(startObservation);
    hash = hash * 31 + JodaBeanUtils.hashCode(endObservation);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("InflationMonthlyRateComputation{");
    buf.append("startObservation").append('=').append(startObservation).append(',').append(' ');
    buf.append("endObservation").append('=').append(JodaBeanUtils.toString(endObservation));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code InflationMonthlyRateComputation}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code startObservation} property.
     */
    private final MetaProperty<PriceIndexObservation> startObservation = DirectMetaProperty.ofImmutable(
        this, "startObservation", InflationMonthlyRateComputation.class, PriceIndexObservation.class);
    /**
     * The meta-property for the {@code endObservation} property.
     */
    private final MetaProperty<PriceIndexObservation> endObservation = DirectMetaProperty.ofImmutable(
        this, "endObservation", InflationMonthlyRateComputation.class, PriceIndexObservation.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "startObservation",
        "endObservation");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1098347926:  // startObservation
          return startObservation;
        case 82210897:  // endObservation
          return endObservation;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends InflationMonthlyRateComputation> builder() {
      return new InflationMonthlyRateComputation.Builder();
    }

    @Override
    public Class<? extends InflationMonthlyRateComputation> beanType() {
      return InflationMonthlyRateComputation.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code startObservation} property.
     * @return the meta-property, not null
     */
    public MetaProperty<PriceIndexObservation> startObservation() {
      return startObservation;
    }

    /**
     * The meta-property for the {@code endObservation} property.
     * @return the meta-property, not null
     */
    public MetaProperty<PriceIndexObservation> endObservation() {
      return endObservation;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1098347926:  // startObservation
          return ((InflationMonthlyRateComputation) bean).getStartObservation();
        case 82210897:  // endObservation
          return ((InflationMonthlyRateComputation) bean).getEndObservation();
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
   * The bean-builder for {@code InflationMonthlyRateComputation}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<InflationMonthlyRateComputation> {

    private PriceIndexObservation startObservation;
    private PriceIndexObservation endObservation;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1098347926:  // startObservation
          return startObservation;
        case 82210897:  // endObservation
          return endObservation;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1098347926:  // startObservation
          this.startObservation = (PriceIndexObservation) newValue;
          break;
        case 82210897:  // endObservation
          this.endObservation = (PriceIndexObservation) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public InflationMonthlyRateComputation build() {
      return new InflationMonthlyRateComputation(
          startObservation,
          endObservation);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("InflationMonthlyRateComputation.Builder{");
      buf.append("startObservation").append('=').append(JodaBeanUtils.toString(startObservation)).append(',').append(' ');
      buf.append("endObservation").append('=').append(JodaBeanUtils.toString(endObservation));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
