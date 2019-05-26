/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product;

import com.opengamma.strata.product.common.SummarizerUtils;

/**
 * 附加结构化信息的交易。
 * <p>
 * Trade是在两个交易对手之间的特定日期发生的交易。例如，在未来现金流的特定日期商定的利率互换交易。
 * <p>
 * 对{@link TradeInfo}的引用捕获了不同类型交易常见的结构化信息。
 * <p>
 * 此接口的实现必须是不可变的bean。
 */
public interface Trade
        extends PortfolioItem {

    @Override
    public default PortfolioItemSummary summarize() {
        return SummarizerUtils.summary(this, ProductType.OTHER, "Unknown: " + getClass().getSimpleName());
    }

    /**
     * 获取标准交易信息。
     * <p>
     * 所有交易都包含这套标准信息。
     *
     * @return the trade information
     */
    @Override
    public abstract TradeInfo getInfo();

    //-------------------------------------------------------------------------

    /**
     * 根据传入的TradeInfo返回具有指定Trade实例。
     *
     * @param info the new info
     * @return the instance with the specified info
     */
    public abstract Trade withInfo(TradeInfo info);

}
