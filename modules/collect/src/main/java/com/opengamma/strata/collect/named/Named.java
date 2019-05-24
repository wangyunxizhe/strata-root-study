/*
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.collect.named;

import com.opengamma.strata.collect.Unchecked;

import java.lang.reflect.Method;

/**
 * A named instance.
 * <p>
 * 该接口用于定义可由唯一名称标识的对象。该名称包含足够的信息，可以重新创建实例。
 * <p>
 * 实现类应该提供一个静态方法{@code of(String)}，允许通过名称创建实例（反射）。
 */
public interface Named {

    /**
     * 通过名称获取指定命名类型的实例。
     * <p>
     * 此方法通过反射操作。
     * 要求在传入的type中存在静态方法{@code of(String)}。如果不存在，则引发异常。
     *
     * @param <T>  clazz
     * @param type the named type with the {@code of(String)} method
     * @param name the name to find
     * @return the instance of the named type
     * @throws IllegalArgumentException if the specified name could not be found
     */
    public static <T extends Named> T of(Class<T> type, String name) {
        return Unchecked.wrap(() -> {
            Method method = type.getMethod("of", String.class);
            return type.cast(method.invoke(null, name));
        });
    }

    //-------------------------------------------------------------------------

    /**
     * Gets the unique name of the instance.
     * <p>
     * The name contains enough information to be able to recreate the instance.
     *
     * @return the unique name
     */
    public abstract String getName();

}
