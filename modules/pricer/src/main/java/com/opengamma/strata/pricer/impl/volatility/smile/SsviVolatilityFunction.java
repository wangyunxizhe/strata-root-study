/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.impl.volatility.smile;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import org.joda.beans.ImmutableBean;
import org.joda.beans.MetaBean;
import org.joda.beans.TypedMetaBean;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.impl.light.LightMetaBean;

import com.opengamma.strata.basics.value.ValueDerivatives;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.array.DoubleArray;

/**
 * Surface Stochastic Volatility Inspired (SSVI) formula.
 * <p>
 * Reference: Gatheral, Jim and Jacquier, Antoine. Arbitrage-free SVI volatility surfaces. arXiv:1204.0646v4, 2013. Section 4.
 */
@BeanDefinition(style = "light")
public final class SsviVolatilityFunction
    extends VolatilityFunctionProvider<SsviFormulaData> implements ImmutableBean, Serializable {

  /**
   * Default implementation.
   */
  public static final SsviVolatilityFunction DEFAULT = new SsviVolatilityFunction();

  /** SSVI volatility description diverge for theta -> 0. Lower bound for which time to expiry is accepted. */
  public static final double MIN_TIME_TO_EXPIRY = 1.0E-3;

  //-------------------------------------------------------------------------
  @Override
  public double volatility(double forward, double strike, double timeToExpiry, SsviFormulaData data) {
    ArgChecker.isTrue(timeToExpiry > MIN_TIME_TO_EXPIRY, "time to expiry must not be zero to be able to compute volatility");
    double volatilityAtm = data.getSigma();
    double rho = data.getRho();
    double eta = data.getEta();
    double theta = volatilityAtm * volatilityAtm * timeToExpiry;
    double phi = eta / Math.sqrt(theta);
    double k = Math.log(strike / forward);
    double w = 0.5 * theta * (1.0d + rho * phi * k + Math.sqrt(1.0d + 2 * rho * phi * k + phi * k * phi * k));
    return Math.sqrt(w / timeToExpiry);
  }

  /**
   * Computes the implied volatility in the SSVI formula and its derivatives.
   * <p>
   * The derivatives are stored in an array with:
   * <ul>
   * <li>[0] derivative with respect to the forward
   * <li>[1] derivative with respect to the strike
   * <li>[2] derivative with respect to the time to expiry
   * <li>[3] derivative with respect to the sigma (ATM volatility)
   * <li>[4] derivative with respect to the rho
   * <li>[5] derivative with respect to the eta
   * </ul>
   * 
   * @param forward  the forward value of the underlying
   * @param strike  the strike value of the option
   * @param timeToExpiry  the time to expiry of the option
   * @param data  the SSVI data
   * @return the volatility and associated derivatives
   */
  @Override
  public ValueDerivatives volatilityAdjoint(double forward, double strike, double timeToExpiry, SsviFormulaData data) {
    ArgChecker.isTrue(timeToExpiry > MIN_TIME_TO_EXPIRY, "time to expiry must not be zero to be able to compute volatility");
    double volatilityAtm = data.getSigma();
    double rho = data.getRho();
    double eta = data.getEta();
    double theta = volatilityAtm * volatilityAtm * timeToExpiry;
    double stheta = Math.sqrt(theta);
    double phi = eta / stheta;
    double k = Math.log(strike / forward);
    double s = Math.sqrt(1.0d + 2 * rho * phi * k + phi * k * phi * k);
    double w = 0.5 * theta * (1.0d + rho * phi * k + s);
    double volatility = Math.sqrt(w / timeToExpiry);
    // Backward sweep.
    double[] derivatives = new double[6]; // 6 inputs
    double volatilityBar = 1.0;
    double wBar = 0.5 * volatility / w * volatilityBar;
    derivatives[2] += -0.5 * volatility / timeToExpiry * volatilityBar;
    double thetaBar = w / theta * wBar;
    derivatives[4] += 0.5 * theta * phi * k * wBar;
    double phiBar = 0.5 * theta * rho * k * wBar;
    double kBar = 0.5 * theta * rho * phi * wBar;
    double sBar = 0.5 * theta * wBar;
    derivatives[4] += phi * k / s * sBar;
    phiBar += (rho * k + phi * k * k) / s * sBar;
    kBar += (rho * phi + phi * phi * k) / s * sBar;
    derivatives[1] += 1.0d / strike * kBar;
    derivatives[0] += -1.0d / forward * kBar;
    derivatives[5] += phiBar / stheta;
    double sthetaBar = -eta / (stheta * stheta) * phiBar;
    thetaBar += 0.5 / stheta * sthetaBar;
    derivatives[3] += 2 * volatilityAtm * timeToExpiry * thetaBar;
    derivatives[2] += volatilityAtm * volatilityAtm * thetaBar;
    return ValueDerivatives.of(volatility, DoubleArray.ofUnsafe(derivatives));
  }

  @Override
  public double volatilityAdjoint2(double forward, double strike, double timeToExpiry,
      SsviFormulaData data, double[] volatilityD, double[][] volatilityD2) {
    throw new UnsupportedOperationException("Not implemented");
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code SsviVolatilityFunction}.
   */
  private static final TypedMetaBean<SsviVolatilityFunction> META_BEAN =
      LightMetaBean.of(SsviVolatilityFunction.class, MethodHandles.lookup());

  /**
   * The meta-bean for {@code SsviVolatilityFunction}.
   * @return the meta-bean, not null
   */
  public static TypedMetaBean<SsviVolatilityFunction> meta() {
    return META_BEAN;
  }

  static {
    MetaBean.register(META_BEAN);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private SsviVolatilityFunction() {
  }

  @Override
  public TypedMetaBean<SsviVolatilityFunction> metaBean() {
    return META_BEAN;
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
    buf.append("SsviVolatilityFunction{");
    buf.append('}');
    return buf.toString();
  }

  //-------------------------- AUTOGENERATED END --------------------------
}