/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product;

import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.currency.Currency;

/**
 * 金融工具的产品细节。
 * <p>
 * Product接口是适用于许多不同类型的高级抽象。
 * 例如，利率互换（Swap）是一种产品，远期利率协议（FRA）也是如此。
 * 用代码说就是该接口是Swap，也是Dsf的超类
 * <p>
 * product相关的类独立于{@link Trade}存在。
 * 它代表金融工具的经济性，而不考虑交易日期或交易对手（Trade相关类中的属性）。
 * <p>
 * 实现必须是不可变的和线程安全的bean。
 */
public interface Product {

    /**
     * 检查此产品是否为交叉货币。
     * <p>
     * 跨货币产品定义为指两种或两种以上货币的产品。任何有直接或间接外汇风险的产品都将是交叉货币。
     * <p>
     * 例如，fixed/Ibor swap in USD observing USD-LIBOR不是交叉货币，
     * 但observes EURIBOR的swap是交叉货币。
     *
     * @return 是交叉货币，返回true
     */
    public default boolean isCrossCurrency() {
        return allCurrencies().size() > 1;
    }

    /**
     * 返回产品支付的货币集。
     * <p>
     * 这将返回一整套付款货币（Currency集合）。这通常会返回一种或两种货币。
     *
     * @return the set of payment currencies
     */
    public default ImmutableSet<Currency> allPaymentCurrencies() {
        return allCurrencies();
    }

    /**
     * 返回产品引用的Currency集合。
     * <p>
     * 这将返回完整的Currency集合，而不仅仅是支付货币。例如，当其中一种货币不可交付时，这些货币组将有所不同。
     *
     * @return the set of currencies the product refers to
     */
    public abstract ImmutableSet<Currency> allCurrencies();

}
