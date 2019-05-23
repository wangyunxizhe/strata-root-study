/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.swaption;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import com.opengamma.strata.product.common.SettlementType;

/**
 * Defines the physical settlement type for the payoff of a swaption.
 * <p>
 * The settlement type is {@link SettlementType#PHYSICAL}. This means that the two
 * parties enter into the actual interest rate swap at the expiry date of the option.
 */
@BeanDefinition(builderScope = "private")
public final class PhysicalSwaptionSettlement
    implements SwaptionSettlement, ImmutableBean, Serializable {

  /**
   * Default instance.
   */
  public static final PhysicalSwaptionSettlement DEFAULT = new PhysicalSwaptionSettlement();

  @Override
  public SettlementType getSettlementType() {
    return SettlementType.PHYSICAL;
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code PhysicalSwaptionSettlement}.
   * @return the meta-bean, not null
   */
  public static PhysicalSwaptionSettlement.Meta meta() {
    return PhysicalSwaptionSettlement.Meta.INSTANCE;
  }

  static {
    MetaBean.register(PhysicalSwaptionSettlement.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private PhysicalSwaptionSettlement() {
  }

  @Override
  public PhysicalSwaptionSettlement.Meta metaBean() {
    return PhysicalSwaptionSettlement.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(32);
    buf.append("PhysicalSwaptionSettlement{");
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code PhysicalSwaptionSettlement}.
   */
  public static final class Meta extends DirectMetaBean {
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
    public BeanBuilder<? extends PhysicalSwaptionSettlement> builder() {
      return new PhysicalSwaptionSettlement.Builder();
    }

    @Override
    public Class<? extends PhysicalSwaptionSettlement> beanType() {
      return PhysicalSwaptionSettlement.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code PhysicalSwaptionSettlement}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<PhysicalSwaptionSettlement> {

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
    public PhysicalSwaptionSettlement build() {
      return new PhysicalSwaptionSettlement();
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      return "PhysicalSwaptionSettlement.Builder{}";
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}