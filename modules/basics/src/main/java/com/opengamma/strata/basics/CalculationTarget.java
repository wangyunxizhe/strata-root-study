/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics;

/**
 * The target of calculation within a system.
 * <p>
 * 所有可以作为计算目标的金融工具都实现了这个标记接口。
 * 例如，trade或position
 * <p>
 * 此接口的所有实现都必须是不可变的且线程安全的。
 */
public interface CalculationTarget {

}
