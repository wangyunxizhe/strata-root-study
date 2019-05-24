/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics;

/**
 * 唯一引用数据的标识符。
 * <p>
 * 使用此标识符从{@link ReferenceData} 的实例中获取引用数据。
 *
 * @param <T> 参考数据的数据类型
 */
public interface ReferenceDataId<T> {

    /**
     * 获取参考数据的类型。
     *
     * @return 参考数据的数据类型
     */
    public abstract Class<T> getReferenceDataType();

    /**
     * 用于查询与此标识符关联的引用数据值的低级方法，如果未找到，则返回空值。
     * <p>
     * 这是一个获取引用数据值的低级方法，返回空值而不是错误。
     * 应用程序应优先使用{@link ReferenceData#getValue(ReferenceDataId)} 而不是此方法。
     *
     * @param refData 参考数据
     * @return 未找到数据返回null
     */
    public default T queryValueOrNull(ReferenceData refData) {
        return refData.queryValueOrNull(this);
    }

}
