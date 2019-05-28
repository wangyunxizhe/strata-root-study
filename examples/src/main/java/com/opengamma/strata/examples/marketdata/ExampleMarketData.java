/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.examples.marketdata;

/**
 * 示例市场数据的类
 */
public final class ExampleMarketData {

  /**
   * 内置示例市场数据的根资源目录
   */
  private static final String EXAMPLE_MARKET_DATA_ROOT = "example-marketdata";

  /**
   * Restricted constructor.
   */
  private ExampleMarketData() {
  }

  //-------------------------------------------------------------------------
  /**
   * 获取内置示例市场数据的市场数据生成器。
   * 
   * @return the market data builder
   */
  public static ExampleMarketDataBuilder builder() {
    return ExampleMarketDataBuilder.ofResource(EXAMPLE_MARKET_DATA_ROOT);
  }

}
