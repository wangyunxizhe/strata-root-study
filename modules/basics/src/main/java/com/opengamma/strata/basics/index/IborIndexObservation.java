/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.index;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.Currency;

/**
 * Defines the observation of a rate of interest from a single Ibor index.
 * <p>
 * An interest rate determined directly from an Ibor index.
 * For example, a rate determined from 'GBP-LIBOR-3M' on a single fixing date.
 */
@BeanDefinition(builderScope = "private", constructorScope = "package")
public final class IborIndexObservation
    implements IndexObservation, ImmutableBean, Serializable {

  /**
   * The Ibor index.
   * <p>
   * The rate to be paid is based on this index.
   * It will be a well known market index such as 'GBP-LIBOR-3M'.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final IborIndex index;
  /**
   * The date of the index fixing.
   * <p>
   * This is an adjusted date with any business day rule applied.
   * Valid business days are defined by {@link IborIndex#getFixingCalendar()}.
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate fixingDate;
  /**
   * The effective date of the investment implied by the fixing date.
   * <p>
   * This is an adjusted date with any business day rule applied.
   * This must be equal to {@link IborIndex#calculateEffectiveFromFixing(LocalDate, ReferenceData)}.
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate effectiveDate;
  /**
   * The maturity date of the investment implied by the fixing date.
   * <p>
   * This is an adjusted date with any business day rule applied.
   * This must be equal to {@link IborIndex#calculateMaturityFromEffective(LocalDate, ReferenceData)}.
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate maturityDate;
  /**
   * The year fraction of the investment implied by the fixing date.
   * <p>
   * This is calculated using the day count of the index.
   * It represents the fraction of the year between the effective date and the maturity date.
   * Typically the value will be close to 1 for one year and close to 0.5 for six months.
   */
  @PropertyDefinition(validate = "notNull")
  private final double yearFraction;

  //-------------------------------------------------------------------------
  /**
   * Creates an instance from an index and fixing date.
   * <p>
   * The reference data is used to find the maturity date from the fixing date.
   * 
   * @param index  the index
   * @param fixingDate  the fixing date
   * @param refData  the reference data to use when resolving holiday calendars
   * @return the rate observation
   */
  public static IborIndexObservation of(
      IborIndex index,
      LocalDate fixingDate,
      ReferenceData refData) {

    LocalDate effectiveDate = index.calculateEffectiveFromFixing(fixingDate, refData);
    LocalDate maturityDate = index.calculateMaturityFromEffective(effectiveDate, refData);
    double yearFraction = index.getDayCount().yearFraction(effectiveDate, maturityDate);
    return new IborIndexObservation(index, fixingDate, effectiveDate, maturityDate, yearFraction);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency of the Ibor index.
   * 
   * @return the currency of the index
   */
  public Currency getCurrency() {
    return index.getCurrency();
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code IborIndexObservation}.
   * @return the meta-bean, not null
   */
  public static IborIndexObservation.Meta meta() {
    return IborIndexObservation.Meta.INSTANCE;
  }

  static {
    MetaBean.register(IborIndexObservation.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance.
   * @param index  the value of the property, not null
   * @param fixingDate  the value of the property, not null
   * @param effectiveDate  the value of the property, not null
   * @param maturityDate  the value of the property, not null
   * @param yearFraction  the value of the property, not null
   */
  IborIndexObservation(
      IborIndex index,
      LocalDate fixingDate,
      LocalDate effectiveDate,
      LocalDate maturityDate,
      double yearFraction) {
    JodaBeanUtils.notNull(index, "index");
    JodaBeanUtils.notNull(fixingDate, "fixingDate");
    JodaBeanUtils.notNull(effectiveDate, "effectiveDate");
    JodaBeanUtils.notNull(maturityDate, "maturityDate");
    JodaBeanUtils.notNull(yearFraction, "yearFraction");
    this.index = index;
    this.fixingDate = fixingDate;
    this.effectiveDate = effectiveDate;
    this.maturityDate = maturityDate;
    this.yearFraction = yearFraction;
  }

  @Override
  public IborIndexObservation.Meta metaBean() {
    return IborIndexObservation.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the Ibor index.
   * <p>
   * The rate to be paid is based on this index.
   * It will be a well known market index such as 'GBP-LIBOR-3M'.
   * @return the value of the property, not null
   */
  @Override
  public IborIndex getIndex() {
    return index;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the date of the index fixing.
   * <p>
   * This is an adjusted date with any business day rule applied.
   * Valid business days are defined by {@link IborIndex#getFixingCalendar()}.
   * @return the value of the property, not null
   */
  public LocalDate getFixingDate() {
    return fixingDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the effective date of the investment implied by the fixing date.
   * <p>
   * This is an adjusted date with any business day rule applied.
   * This must be equal to {@link IborIndex#calculateEffectiveFromFixing(LocalDate, ReferenceData)}.
   * @return the value of the property, not null
   */
  public LocalDate getEffectiveDate() {
    return effectiveDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the maturity date of the investment implied by the fixing date.
   * <p>
   * This is an adjusted date with any business day rule applied.
   * This must be equal to {@link IborIndex#calculateMaturityFromEffective(LocalDate, ReferenceData)}.
   * @return the value of the property, not null
   */
  public LocalDate getMaturityDate() {
    return maturityDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the year fraction of the investment implied by the fixing date.
   * <p>
   * This is calculated using the day count of the index.
   * It represents the fraction of the year between the effective date and the maturity date.
   * Typically the value will be close to 1 for one year and close to 0.5 for six months.
   * @return the value of the property, not null
   */
  public double getYearFraction() {
    return yearFraction;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      IborIndexObservation other = (IborIndexObservation) obj;
      return JodaBeanUtils.equal(index, other.index) &&
          JodaBeanUtils.equal(fixingDate, other.fixingDate) &&
          JodaBeanUtils.equal(effectiveDate, other.effectiveDate) &&
          JodaBeanUtils.equal(maturityDate, other.maturityDate) &&
          JodaBeanUtils.equal(yearFraction, other.yearFraction);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(index);
    hash = hash * 31 + JodaBeanUtils.hashCode(fixingDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(effectiveDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(maturityDate);
    hash = hash * 31 + JodaBeanUtils.hashCode(yearFraction);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("IborIndexObservation{");
    buf.append("index").append('=').append(index).append(',').append(' ');
    buf.append("fixingDate").append('=').append(fixingDate).append(',').append(' ');
    buf.append("effectiveDate").append('=').append(effectiveDate).append(',').append(' ');
    buf.append("maturityDate").append('=').append(maturityDate).append(',').append(' ');
    buf.append("yearFraction").append('=').append(JodaBeanUtils.toString(yearFraction));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code IborIndexObservation}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code index} property.
     */
    private final MetaProperty<IborIndex> index = DirectMetaProperty.ofImmutable(
        this, "index", IborIndexObservation.class, IborIndex.class);
    /**
     * The meta-property for the {@code fixingDate} property.
     */
    private final MetaProperty<LocalDate> fixingDate = DirectMetaProperty.ofImmutable(
        this, "fixingDate", IborIndexObservation.class, LocalDate.class);
    /**
     * The meta-property for the {@code effectiveDate} property.
     */
    private final MetaProperty<LocalDate> effectiveDate = DirectMetaProperty.ofImmutable(
        this, "effectiveDate", IborIndexObservation.class, LocalDate.class);
    /**
     * The meta-property for the {@code maturityDate} property.
     */
    private final MetaProperty<LocalDate> maturityDate = DirectMetaProperty.ofImmutable(
        this, "maturityDate", IborIndexObservation.class, LocalDate.class);
    /**
     * The meta-property for the {@code yearFraction} property.
     */
    private final MetaProperty<Double> yearFraction = DirectMetaProperty.ofImmutable(
        this, "yearFraction", IborIndexObservation.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "index",
        "fixingDate",
        "effectiveDate",
        "maturityDate",
        "yearFraction");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return index;
        case 1255202043:  // fixingDate
          return fixingDate;
        case -930389515:  // effectiveDate
          return effectiveDate;
        case -414641441:  // maturityDate
          return maturityDate;
        case -1731780257:  // yearFraction
          return yearFraction;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends IborIndexObservation> builder() {
      return new IborIndexObservation.Builder();
    }

    @Override
    public Class<? extends IborIndexObservation> beanType() {
      return IborIndexObservation.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code index} property.
     * @return the meta-property, not null
     */
    public MetaProperty<IborIndex> index() {
      return index;
    }

    /**
     * The meta-property for the {@code fixingDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> fixingDate() {
      return fixingDate;
    }

    /**
     * The meta-property for the {@code effectiveDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> effectiveDate() {
      return effectiveDate;
    }

    /**
     * The meta-property for the {@code maturityDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> maturityDate() {
      return maturityDate;
    }

    /**
     * The meta-property for the {@code yearFraction} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> yearFraction() {
      return yearFraction;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return ((IborIndexObservation) bean).getIndex();
        case 1255202043:  // fixingDate
          return ((IborIndexObservation) bean).getFixingDate();
        case -930389515:  // effectiveDate
          return ((IborIndexObservation) bean).getEffectiveDate();
        case -414641441:  // maturityDate
          return ((IborIndexObservation) bean).getMaturityDate();
        case -1731780257:  // yearFraction
          return ((IborIndexObservation) bean).getYearFraction();
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
   * The bean-builder for {@code IborIndexObservation}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<IborIndexObservation> {

    private IborIndex index;
    private LocalDate fixingDate;
    private LocalDate effectiveDate;
    private LocalDate maturityDate;
    private double yearFraction;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          return index;
        case 1255202043:  // fixingDate
          return fixingDate;
        case -930389515:  // effectiveDate
          return effectiveDate;
        case -414641441:  // maturityDate
          return maturityDate;
        case -1731780257:  // yearFraction
          return yearFraction;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 100346066:  // index
          this.index = (IborIndex) newValue;
          break;
        case 1255202043:  // fixingDate
          this.fixingDate = (LocalDate) newValue;
          break;
        case -930389515:  // effectiveDate
          this.effectiveDate = (LocalDate) newValue;
          break;
        case -414641441:  // maturityDate
          this.maturityDate = (LocalDate) newValue;
          break;
        case -1731780257:  // yearFraction
          this.yearFraction = (Double) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public IborIndexObservation build() {
      return new IborIndexObservation(
          index,
          fixingDate,
          effectiveDate,
          maturityDate,
          yearFraction);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(192);
      buf.append("IborIndexObservation.Builder{");
      buf.append("index").append('=').append(JodaBeanUtils.toString(index)).append(',').append(' ');
      buf.append("fixingDate").append('=').append(JodaBeanUtils.toString(fixingDate)).append(',').append(' ');
      buf.append("effectiveDate").append('=').append(JodaBeanUtils.toString(effectiveDate)).append(',').append(' ');
      buf.append("maturityDate").append('=').append(JodaBeanUtils.toString(maturityDate)).append(',').append(' ');
      buf.append("yearFraction").append('=').append(JodaBeanUtils.toString(yearFraction));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}