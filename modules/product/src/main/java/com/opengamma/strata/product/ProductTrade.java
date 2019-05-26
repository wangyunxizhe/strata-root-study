/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product;

/**
 * 为ProductTrade的所有实现类提供通用的方法。子类有SwapTrade（直接），DsfTrade（间接）等等
 * <p>
 * 产品交易是{@link Trade}，它直接包含对{@link Product}的引用。
 * <p>
 * 此接口的实现必须是不可变的bean。
 */
public interface ProductTrade
        extends Trade {

    /**
     * 获取交易发生时协定的基础产品。
     * <p>
     * 该产品捕获交易的合同财务细节。
     *
     * @return the product
     */
    public abstract Product getProduct();

    //-------------------------------------------------------------------------

    /**
     * 返回具有指定信息的ProductTrade实例。
     *
     * @param info the new info
     * @return 具有指定信息的实例
     */
    @Override
    public abstract ProductTrade withInfo(TradeInfo info);

}
