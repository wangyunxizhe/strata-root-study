/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.calc.CalculationRunner;
import com.opengamma.strata.calc.marketdata.MarketDataFactory;
import com.opengamma.strata.calc.marketdata.MarketDataFunction;
import com.opengamma.strata.calc.marketdata.ObservableDataProvider;
import com.opengamma.strata.calc.marketdata.TimeSeriesProvider;
import com.opengamma.strata.calc.runner.CalculationFunctions;
import com.opengamma.strata.measure.bond.*;
import com.opengamma.strata.measure.capfloor.IborCapFloorTradeCalculationFunction;
import com.opengamma.strata.measure.credit.CdsIndexTradeCalculationFunction;
import com.opengamma.strata.measure.credit.CdsTradeCalculationFunction;
import com.opengamma.strata.measure.curve.CurveMarketDataFunction;
import com.opengamma.strata.measure.deposit.TermDepositTradeCalculationFunction;
import com.opengamma.strata.measure.dsf.DsfTradeCalculationFunction;
import com.opengamma.strata.measure.fra.FraTradeCalculationFunction;
import com.opengamma.strata.measure.fx.FxNdfTradeCalculationFunction;
import com.opengamma.strata.measure.fx.FxRateMarketDataFunction;
import com.opengamma.strata.measure.fx.FxSingleTradeCalculationFunction;
import com.opengamma.strata.measure.fx.FxSwapTradeCalculationFunction;
import com.opengamma.strata.measure.fxopt.FxOptionVolatilitiesMarketDataFunction;
import com.opengamma.strata.measure.fxopt.FxSingleBarrierOptionTradeCalculationFunction;
import com.opengamma.strata.measure.fxopt.FxVanillaOptionTradeCalculationFunction;
import com.opengamma.strata.measure.index.IborFutureOptionTradeCalculationFunction;
import com.opengamma.strata.measure.index.IborFutureTradeCalculationFunction;
import com.opengamma.strata.measure.payment.BulletPaymentTradeCalculationFunction;
import com.opengamma.strata.measure.rate.RatesCurveGroupMarketDataFunction;
import com.opengamma.strata.measure.rate.RatesCurveInputsMarketDataFunction;
import com.opengamma.strata.measure.security.GenericSecurityPositionCalculationFunction;
import com.opengamma.strata.measure.security.GenericSecurityTradeCalculationFunction;
import com.opengamma.strata.measure.security.SecurityPositionCalculationFunction;
import com.opengamma.strata.measure.security.SecurityTradeCalculationFunction;
import com.opengamma.strata.measure.swap.SwapTradeCalculationFunction;
import com.opengamma.strata.measure.swaption.SwaptionTradeCalculationFunction;
import com.opengamma.strata.product.GenericSecurityPosition;
import com.opengamma.strata.product.GenericSecurityTrade;
import com.opengamma.strata.product.SecurityPosition;
import com.opengamma.strata.product.SecurityTrade;
import com.opengamma.strata.product.bond.*;
import com.opengamma.strata.product.capfloor.IborCapFloorTrade;
import com.opengamma.strata.product.credit.CdsIndexTrade;
import com.opengamma.strata.product.credit.CdsTrade;
import com.opengamma.strata.product.deposit.TermDepositTrade;
import com.opengamma.strata.product.dsf.DsfPosition;
import com.opengamma.strata.product.dsf.DsfTrade;
import com.opengamma.strata.product.fra.FraTrade;
import com.opengamma.strata.product.fx.FxNdfTrade;
import com.opengamma.strata.product.fx.FxSingleTrade;
import com.opengamma.strata.product.fx.FxSwapTrade;
import com.opengamma.strata.product.fxopt.FxSingleBarrierOptionTrade;
import com.opengamma.strata.product.fxopt.FxVanillaOptionTrade;
import com.opengamma.strata.product.index.IborFutureOptionPosition;
import com.opengamma.strata.product.index.IborFutureOptionTrade;
import com.opengamma.strata.product.index.IborFuturePosition;
import com.opengamma.strata.product.index.IborFutureTrade;
import com.opengamma.strata.product.payment.BulletPaymentTrade;
import com.opengamma.strata.product.swap.SwapTrade;
import com.opengamma.strata.product.swaption.SwaptionTrade;

import java.util.List;

/**
 * 创建标准Strata组件的工厂方法。
 * <p>
 * 这些组件适用于使用内置资产类别、市场数据类型和价格进行计算。
 * <p>
 * 市场数据工厂可以创建从其他值派生的市场数据值。例如，它可以创建给定市场报价的校准曲线。
 * 但是，它不能从外部提供者（如Bloomberg）请求市场数据，也不能从数据存储中查找数据，例如时间序列数据库。
 * {@link CalculationRunner}的实例直接使用接口上的静态方法创建。
 */
public final class StandardComponents {

  /**
   * 标准计算功能。
   */
  private static final CalculationFunctions STANDARD = CalculationFunctions.of(
      new BulletPaymentTradeCalculationFunction(),
      new CdsTradeCalculationFunction(),
      new CdsIndexTradeCalculationFunction(),
      new FraTradeCalculationFunction(),
      new FxNdfTradeCalculationFunction(),
      new FxSingleBarrierOptionTradeCalculationFunction(),
      new FxSingleTradeCalculationFunction(),
      new FxSwapTradeCalculationFunction(),
      new FxVanillaOptionTradeCalculationFunction(),
      new IborCapFloorTradeCalculationFunction(),
      new SecurityPositionCalculationFunction(),
      new SecurityTradeCalculationFunction(),
      new SwapTradeCalculationFunction(),
      new SwaptionTradeCalculationFunction(),
      new TermDepositTradeCalculationFunction(),
      new GenericSecurityPositionCalculationFunction(),
      new GenericSecurityTradeCalculationFunction(),
      BondFutureTradeCalculationFunction.TRADE,
      BondFutureTradeCalculationFunction.POSITION,
      BondFutureOptionTradeCalculationFunction.TRADE,
      BondFutureOptionTradeCalculationFunction.POSITION,
      CapitalIndexedBondTradeCalculationFunction.TRADE,
      CapitalIndexedBondTradeCalculationFunction.POSITION,
      DsfTradeCalculationFunction.TRADE,
      DsfTradeCalculationFunction.POSITION,
      FixedCouponBondTradeCalculationFunction.TRADE,
      FixedCouponBondTradeCalculationFunction.POSITION,
      BillTradeCalculationFunction.TRADE,
      BillTradeCalculationFunction.POSITION,
      IborFutureTradeCalculationFunction.TRADE,
      IborFutureTradeCalculationFunction.POSITION,
      IborFutureOptionTradeCalculationFunction.TRADE,
      IborFutureOptionTradeCalculationFunction.POSITION);

  /**
   * Restricted constructor.
   */
  private StandardComponents() {
  }

  //-------------------------------------------------------------------------
  /**
   * Returns a market data factory containing the standard set of market data functions.
   * <p>
   * This factory can create market data values from other market data. For example it
   * can create calibrated curves given a set of market quotes for the points on the curve.
   * <p>
   * The set of functions are the ones provided by {@link #marketDataFunctions()}.
   *
   * @return a market data factory containing the standard set of market data functions
   */
  public static MarketDataFactory marketDataFactory() {
    return marketDataFactory(ObservableDataProvider.none());
  }

  /**
   * Returns a market data factory containing the standard set of market data functions.
   * <p>
   * This factory can create market data values from other market data. For example it
   * can create calibrated curves given a set of market quotes for the points on the curve.
   * <p>
   * The set of functions are the ones provided by {@link #marketDataFunctions()}.
   *
   * @param observableDataProvider  the provider of observable data
   * @return a market data factory containing the standard set of market data functions
   */
  public static MarketDataFactory marketDataFactory(ObservableDataProvider observableDataProvider) {
    return MarketDataFactory.of(observableDataProvider, TimeSeriesProvider.none(), marketDataFunctions());
  }

  /**
   * Returns the standard market data functions used to build market data values from other market data.
   * <p>
   * These include functions to build:
   * <ul>
   *  <li>Par rates from quotes
   *  <li>Curve groups from par rates
   *  <li>Curves from curve groups
   *  <li>Discount factors and index rates from curves
   *  <li>FX rates from quotes
   *  <li>FX option volatilities from quotes
   * </ul>
   *
   * @return the standard market data functions
   */
  public static List<MarketDataFunction<?, ?>> marketDataFunctions() {
    return ImmutableList.of(
        new CurveMarketDataFunction(),
        new RatesCurveGroupMarketDataFunction(),
        new RatesCurveInputsMarketDataFunction(),
        new FxRateMarketDataFunction(),
        new FxOptionVolatilitiesMarketDataFunction());
  }

  /**
   * 返回标准计算函数。
   * <p>
   * 定义了如何计算标准资产类的标准度量。
   * <p>
   * 标准的计算功能不需要进一步的配置，其设计允许轻松访问所有内置资产类别覆盖范围。
   * 支持的资产类别包括：
   * <ul>
   *  <li>Bond future - {@link BondFutureTrade} and {@link BondFuturePosition}
   *  <li>Bond future option - {@link BondFutureOptionTrade} and {@link BondFutureOptionPosition}
   *  <li>Bullet Payment - {@link BulletPaymentTrade}
   *  <li>Cap/floor (Ibor) - {@link IborCapFloorTrade}
   *  <li>Capital Indexed bond - {@link CapitalIndexedBondTrade} and {@link CapitalIndexedBondPosition}
   *  <li>Credit Default Swap - {@link CdsTrade}
   *  <li>CDS Index - {@link CdsIndexTrade}
   *  <li>Deliverable Swap Future - {@link DsfTrade} and {@link DsfPosition}
   *  <li>Forward Rate Agreement - {@link FraTrade}
   *  <li>Fixed coupon bond - {@link FixedCouponBondTrade} and {@link FixedCouponBondPosition}
   *  <li>FX spot and FX forward - {@link FxSingleTrade}
   *  <li>FX NDF - {@link FxNdfTrade}
   *  <li>FX swap - {@link FxSwapTrade}
   *  <li>FX vanilla option - {@link FxVanillaOptionTrade}
   *  <li>FX single barrier option - {@link FxSingleBarrierOptionTrade}
   *  <li>Generic Security - {@link GenericSecurityTrade} and {@link GenericSecurityPosition}
   *  <li>Rate Swap - {@link SwapTrade}
   *  <li>Swaption - {@link SwaptionTrade}
   *  <li>Security - {@link SecurityTrade} and {@link SecurityPosition}
   *  <li>STIR Future (Ibor) - {@link IborFutureTrade} and {@link IborFuturePosition}
   *  <li>STIR Future Option (Ibor) - {@link IborFutureOptionTrade} and {@link IborFutureOptionPosition}
   *  <li>Term Deposit - {@link TermDepositTrade}
   * </ul>
   *
   * @return calculation functions used to perform calculations
   */
  public static CalculationFunctions calculationFunctions() {
    return STANDARD;
  }

}
