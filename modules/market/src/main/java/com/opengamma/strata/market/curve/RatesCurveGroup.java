/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve;

import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.index.Index;
import org.joda.beans.*;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * 一组曲线。
 * <p>
 * 这用于保存一组相关曲线，通常形成一个逻辑集。它通常用于保存曲线校准的结果。
 * <p>
 * 也可以从一组现有曲线创建曲线组。
 */
@BeanDefinition
public final class RatesCurveGroup
    implements CurveGroup, ImmutableBean, Serializable {

  private static final Logger log = LoggerFactory.getLogger(RatesCurveGroup.class);

  /**
   * 曲线组的名称。
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final CurveGroupName name;
  /**
   * 组中的折扣曲线，由货币键控。
   */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableMap<Currency, Curve> discountCurves;
  /**
   * 组中的正向曲线，key为索引。
   */
  @PropertyDefinition(validate = "notNull", builderType = "Map<? extends Index, ? extends Curve>")
  private final ImmutableMap<Index, Curve> forwardCurves;

  //-------------------------------------------------------------------------
  /**
   * Returns a curve group containing the specified curves.
   *
   * @param name  the name of the curve group
   * @param discountCurves  the discount curves, keyed by currency
   * @param forwardCurves  the forward curves, keyed by index
   * @return a curve group containing the specified curves
   */
  public static RatesCurveGroup of(CurveGroupName name, Map<Currency, Curve> discountCurves, Map<Index, Curve> forwardCurves) {
    return new RatesCurveGroup(name, discountCurves, forwardCurves);
  }

  /**
   * 使用曲线组定义和一些现有曲线创建曲线组。
   * <p>
   * 如果定义中指定的曲线不在曲线中，则使用可用的任何曲线来构建组。
   * <p>
   * 如果曲线中有多条同名曲线，则任意选择其中一条。
   * <p>
   * 允许多个具有相同名称的曲线支持列表多次包含相同曲线的用例。这意味着调用方不必过滤输入曲线来删除重复项。
   *
   * @param curveGroupDefinition  the definition of a curve group
   * @param curves  some curves
   * @return a curve group built from the definition and the list of curves
   */
  public static RatesCurveGroup ofCurves(RatesCurveGroupDefinition curveGroupDefinition, Curve... curves) {
    return ofCurves(curveGroupDefinition, Arrays.asList(curves));
  }

  /**
   * Creates a curve group using a curve group definition and a list of existing curves.
   * <p>
   * If there are curves named in the definition which are not present in the curves the group is built using
   * whatever curves are available.
   * <p>
   * If there are multiple curves with the same name in the curves one of them is arbitrarily chosen.
   * <p>
   * Multiple curves with the same name are allowed to support the use case where the list contains the same
   * curve multiple times. This means the caller doesn't have to filter the input curves to remove duplicates.
   *
   * @param curveGroupDefinition  the definition of a curve group
   * @param curves  some curves
   * @return a curve group built from the definition and the list of curves
   */
  public static RatesCurveGroup ofCurves(RatesCurveGroupDefinition curveGroupDefinition, Collection<? extends Curve> curves) {
    Map<Currency, Curve> discountCurves = new HashMap<>();
    Map<Index, Curve> forwardCurves = new HashMap<>();
    Map<CurveName, Curve> curveMap = curves.stream()
        .collect(toMap(curve -> curve.getMetadata().getCurveName(), curve -> curve, (curve1, curve2) -> curve1));

    for (RatesCurveGroupEntry entry : curveGroupDefinition.getEntries()) {
      CurveName curveName = entry.getCurveName();
      Curve curve = curveMap.get(curveName);

      if (curve == null) {
        log.debug("No curve found named '{}' when building curve group '{}'", curveName, curveGroupDefinition.getName());
        continue;
      }
      for (Currency currency : entry.getDiscountCurrencies()) {
        discountCurves.put(currency, curve);
      }
      for (Index index : entry.getIndices()) {
        forwardCurves.put(index, curve);
      }
    }
    return RatesCurveGroup.of(curveGroupDefinition.getName(), discountCurves, forwardCurves);
  }

  //-------------------------------------------------------------------------
  /**
   * Finds the curve with the specified name.
   * <p>
   * If the curve cannot be found, empty is returned.
   * 
   * @param name  the curve name
   * @return the curve, empty if not found
   */
  @Override
  public Optional<Curve> findCurve(CurveName name) {
    return Stream.concat(discountCurves.values().stream(), forwardCurves.values().stream())
        .filter(c -> c.getName().equals(name))
        .findFirst();
  }

  /**
   * Finds the discount curve for the currency if there is one in the group.
   * <p>
   * If the curve is not found, optional empty is returned.
   *
   * @param currency  the currency for which a discount curve is required
   * @return the discount curve for the currency if there is one in the group
   */
  public Optional<Curve> findDiscountCurve(Currency currency) {
    return Optional.ofNullable(discountCurves.get(currency));
  }

  /**
   * Finds the forward curve for the index if there is one in the group.
   * <p>
   * If the curve is not found, optional empty is returned.
   *
   * @param index  the index for which a forward curve is required
   * @return the forward curve for the index if there is one in the group
   */
  public Optional<Curve> findForwardCurve(Index index) {
    return Optional.ofNullable(forwardCurves.get(index));
  }

  /**
   * Returns a stream of all curves in the group.
   *
   * @return Returns a stream of all curves in the group
   */
  @Override
  public Stream<Curve> stream() {
    return Stream.concat(discountCurves.values().stream(), forwardCurves.values().stream());
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code RatesCurveGroup}.
   * @return the meta-bean, not null
   */
  public static RatesCurveGroup.Meta meta() {
    return RatesCurveGroup.Meta.INSTANCE;
  }

  static {
    MetaBean.register(RatesCurveGroup.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static RatesCurveGroup.Builder builder() {
    return new RatesCurveGroup.Builder();
  }

  private RatesCurveGroup(
      CurveGroupName name,
      Map<Currency, Curve> discountCurves,
      Map<? extends Index, ? extends Curve> forwardCurves) {
    JodaBeanUtils.notNull(name, "name");
    JodaBeanUtils.notNull(discountCurves, "discountCurves");
    JodaBeanUtils.notNull(forwardCurves, "forwardCurves");
    this.name = name;
    this.discountCurves = ImmutableMap.copyOf(discountCurves);
    this.forwardCurves = ImmutableMap.copyOf(forwardCurves);
  }

  @Override
  public RatesCurveGroup.Meta metaBean() {
    return RatesCurveGroup.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the name of the curve group.
   * @return the value of the property, not null
   */
  @Override
  public CurveGroupName getName() {
    return name;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the discount curves in the group, keyed by currency.
   * @return the value of the property, not null
   */
  public ImmutableMap<Currency, Curve> getDiscountCurves() {
    return discountCurves;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the forward curves in the group, keyed by index.
   * @return the value of the property, not null
   */
  public ImmutableMap<Index, Curve> getForwardCurves() {
    return forwardCurves;
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
      RatesCurveGroup other = (RatesCurveGroup) obj;
      return JodaBeanUtils.equal(name, other.name) &&
          JodaBeanUtils.equal(discountCurves, other.discountCurves) &&
          JodaBeanUtils.equal(forwardCurves, other.forwardCurves);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(name);
    hash = hash * 31 + JodaBeanUtils.hashCode(discountCurves);
    hash = hash * 31 + JodaBeanUtils.hashCode(forwardCurves);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("RatesCurveGroup{");
    buf.append("name").append('=').append(name).append(',').append(' ');
    buf.append("discountCurves").append('=').append(discountCurves).append(',').append(' ');
    buf.append("forwardCurves").append('=').append(JodaBeanUtils.toString(forwardCurves));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code RatesCurveGroup}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<CurveGroupName> name = DirectMetaProperty.ofImmutable(
        this, "name", RatesCurveGroup.class, CurveGroupName.class);
    /**
     * The meta-property for the {@code discountCurves} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableMap<Currency, Curve>> discountCurves = DirectMetaProperty.ofImmutable(
        this, "discountCurves", RatesCurveGroup.class, (Class) ImmutableMap.class);
    /**
     * The meta-property for the {@code forwardCurves} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableMap<Index, Curve>> forwardCurves = DirectMetaProperty.ofImmutable(
        this, "forwardCurves", RatesCurveGroup.class, (Class) ImmutableMap.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "name",
        "discountCurves",
        "forwardCurves");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -624113147:  // discountCurves
          return discountCurves;
        case -850086775:  // forwardCurves
          return forwardCurves;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public RatesCurveGroup.Builder builder() {
      return new RatesCurveGroup.Builder();
    }

    @Override
    public Class<? extends RatesCurveGroup> beanType() {
      return RatesCurveGroup.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurveGroupName> name() {
      return name;
    }

    /**
     * The meta-property for the {@code discountCurves} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableMap<Currency, Curve>> discountCurves() {
      return discountCurves;
    }

    /**
     * The meta-property for the {@code forwardCurves} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableMap<Index, Curve>> forwardCurves() {
      return forwardCurves;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((RatesCurveGroup) bean).getName();
        case -624113147:  // discountCurves
          return ((RatesCurveGroup) bean).getDiscountCurves();
        case -850086775:  // forwardCurves
          return ((RatesCurveGroup) bean).getForwardCurves();
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
   * The bean-builder for {@code RatesCurveGroup}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<RatesCurveGroup> {

    private CurveGroupName name;
    private Map<Currency, Curve> discountCurves = ImmutableMap.of();
    private Map<? extends Index, ? extends Curve> forwardCurves = ImmutableMap.of();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(RatesCurveGroup beanToCopy) {
      this.name = beanToCopy.getName();
      this.discountCurves = beanToCopy.getDiscountCurves();
      this.forwardCurves = beanToCopy.getForwardCurves();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -624113147:  // discountCurves
          return discountCurves;
        case -850086775:  // forwardCurves
          return forwardCurves;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          this.name = (CurveGroupName) newValue;
          break;
        case -624113147:  // discountCurves
          this.discountCurves = (Map<Currency, Curve>) newValue;
          break;
        case -850086775:  // forwardCurves
          this.forwardCurves = (Map<? extends Index, ? extends Curve>) newValue;
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
    public RatesCurveGroup build() {
      return new RatesCurveGroup(
          name,
          discountCurves,
          forwardCurves);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the name of the curve group.
     * @param name  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder name(CurveGroupName name) {
      JodaBeanUtils.notNull(name, "name");
      this.name = name;
      return this;
    }

    /**
     * Sets the discount curves in the group, keyed by currency.
     * @param discountCurves  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder discountCurves(Map<Currency, Curve> discountCurves) {
      JodaBeanUtils.notNull(discountCurves, "discountCurves");
      this.discountCurves = discountCurves;
      return this;
    }

    /**
     * Sets the forward curves in the group, keyed by index.
     * @param forwardCurves  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder forwardCurves(Map<? extends Index, ? extends Curve> forwardCurves) {
      JodaBeanUtils.notNull(forwardCurves, "forwardCurves");
      this.forwardCurves = forwardCurves;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("RatesCurveGroup.Builder{");
      buf.append("name").append('=').append(JodaBeanUtils.toString(name)).append(',').append(' ');
      buf.append("discountCurves").append('=').append(JodaBeanUtils.toString(discountCurves)).append(',').append(' ');
      buf.append("forwardCurves").append('=').append(JodaBeanUtils.toString(forwardCurves));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
