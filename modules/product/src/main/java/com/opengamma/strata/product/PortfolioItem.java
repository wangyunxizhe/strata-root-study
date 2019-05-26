/*
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product;

import com.opengamma.strata.basics.CalculationTarget;
import com.opengamma.strata.basics.StandardId;

import java.util.Optional;

/**
 * 该接口表示投资组合中的项目。
 * <p>
 * This represents a single item in a portfolio.
 * 这表示投资组合中的单个项目。通常，投资组合由{@linkplain Trade trades}和{@linkplain Position positions}组成。
 * <p>
 * 此接口的实现必须是不可变的bean。
 */
public interface PortfolioItem extends CalculationTarget {

  /**
   * 获取有关项目组合项的其他信息。
   * 
   * @return the additional information
   */
  public abstract PortfolioItemInfo getInfo();

  /**
   * 获取项目组合项的主标识符（可选）。
   * <p>
   * 标识符用于标识项目组合项。它通常是外部数据系统中的标识符。
   * <p>
   * 一个项目组合项可以有多个活动标识符。可以在此处选择任何标识符。
   * 标识符的某些用法（如数据库中的存储）要求标识符不随时间变化，这应被视为最佳做法。
   * 
   * @return the identifier, optional
   */
  public default Optional<StandardId> getId() {
    return getInfo().getId();
  }

  /**
   * 总结项目组合项。
   * <p>
   * 这提供了一个摘要，包括一个可读的描述。
   * 
   * @return the summary of the item
   */
  public abstract PortfolioItemSummary summarize();

}
