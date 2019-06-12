/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics;

/**
 * 可以根据引用数据解析的对象。
 * <p>
 * 接口标记那些可以使用{@link ReferenceData}解析的对象。
 * 这个接口的实现将使用{@linkplain ReferenceDataId identifiers}来引用关键概念，比如holiday calendars和securities。
 * <p>
 * 当调用{@code resolve(ReferenceData)}方法时，将解析标识符。
 * 解析过程将接受每个identifier（标识符），使用{@code ReferenceData}查找它，并返回一个新的“已解析”实例。
 * 通常，结果是为定价优化的类型。
 * <p>
 * 已解析的对象可能绑定到随时间变化的数据，比如holiday calendars。
 * 如果数据发生更改，例如添加了新的假期，则不会更新已解析的表单。
 * 在将解析后的表单放入缓存或持久层时必须小心。
 * <p>
 * 实现必须是不可变的、线程安全的bean。
 *
 * @param <T> the type of the resolved result
 */
public interface Resolvable<T> {

    /**
     * 使用指定的ReferenceData解析此对象。
     * <p>
     * 将实现此接口的对象转换为等效的已解析表单。
     * 将解析此实例中的所有{@link ReferenceDataId}标识符。解析后的表单通常是针对定价进行优化的类型。
     * <p>
     * 已解析的对象可能绑定到随时间变化的数据，比如holiday calendars。
     * 如果数据发生更改，例如添加了新的holiday，则不会更新已解析的表单。在将解析后的表单放入缓存或持久层时必须小心。
     *
     * @param refData the reference data to use when resolving
     * @return the resolved instance
     * @throws ReferenceDataNotFoundException if an identifier cannot be resolved in the reference data
     * @throws RuntimeException               if unable to resolve due to an invalid definition
     */
    public abstract T resolve(ReferenceData refData);

}
