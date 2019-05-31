/*
 * Copyright (C) 2018 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 一组曲线。
 * <p>
 * 这用于保存一组相关曲线，通常形成一个逻辑集。它通常用于保存曲线校准的结果。
 * <p>
 * 也可以从一组现有曲线创建曲线组。
 * <p>
 * In Strata v2, 此类型已转换为接口。如果正在迁移，请将代码更改为{@link RatesCurveGroup}。
 */
public interface CurveGroup {

    /**
     * Gets the name of the curve group.
     *
     * @return the group name
     */
    public abstract CurveGroupName getName();

    /**
     * 查找具有指定名称的曲线。
     * <p>
     * 如果找不到曲线，则返回空。
     *
     * @param name the curve name
     * @return the curve, empty if not found
     */
    public abstract Optional<Curve> findCurve(CurveName name);

    /**
     * Returns a stream of all curves in the group.
     *
     * @return a stream of all curves in the group
     */
    public abstract Stream<Curve> stream();

}
