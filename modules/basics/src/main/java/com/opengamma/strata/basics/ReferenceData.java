/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics;

import com.opengamma.strata.basics.date.HolidayCalendar;
import com.opengamma.strata.basics.date.HolidayCalendarId;
import com.opengamma.strata.basics.date.HolidayCalendars;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 提供对参考数据的访问，如假日日历和证券。
 * <p>
 * 使用{@link ReferenceDataId}的实现查找引用数据。
 * 标识符是用要返回的引用数据类型参数化的。
 * <p>
 * 该接口的实现类是 {@link ImmutableReferenceData}.
 */
public interface ReferenceData {

    /**
     * 从引用数据的map中获取实例。
     * <p>
     * map中的每个键值对都是一段参考数据，key的类型是ReferenceDataId实现类。
     * 例如，可以使用 {@link HolidayCalendarId}查找 {@link HolidayCalendar}。
     * 调用方必须确保map中的每个键值对与标识符上的参数化类型对应。
     * <p>
     * 返回的{@code ReferenceData}将包括{@linkplain #minimal() minimal}（最小一组引用数据），
     * 包括定价所必需的无争议标识符。
     * 要排除最小标识符键值对，请使用{@link ImmutableReferenceData#of(Map)}。
     *
     * @param values 参考数据map
     * @return 返回参考数据对象
     * @throws ClassCastException 如果值与标识符关联的参数化类型不匹配抛出异常
     */
    public static ReferenceData of(Map<? extends ReferenceDataId<?>, ?> values) {
        // hash map so that keys can overlap, with this instance taking priority
        Map<ReferenceDataId<?>, Object> combined = new HashMap<>();
        combined.putAll(StandardReferenceData.MINIMAL.getValues());
        combined.putAll(values);
        return ImmutableReferenceData.of(combined);
    }

    /**
     * 获取标准参考数据的对象。
     * <p>
     * 标准参考数据内置于Strata和provides中，
     * 并提供常用的holidayCalendars和索引。在大多数情况下，Strata的生产使用将不依赖于此参考数据源。
     *
     * @return 标准参考数据
     */
    public static ReferenceData standard() {
        return StandardReferenceData.STANDARD;
    }

    /**
     * 获取最小参考数据的对象。
     * <p>
     * {@linkplain #standard() standard} 方法返回的是标准参考数据，
     * 包含常见的HolidayCalendar实例和索引，但可能不适合生产使用。
     * 最小参考数据只包含Strata所需的、无争议的标识符（map的key）。
     * 返回HolidayCalendar实例（一个不可变的map） ，{id=对应的HolidayCalendar实例}
     * 内部元素是{@link HolidayCalendars#NO_HOLIDAYS}, {@link HolidayCalendars#SAT_SUN},
     * {@link HolidayCalendars#FRI_SAT} and {@link HolidayCalendars#THU_FRI}.
     *
     * @return minimal reference data
     */
    public static ReferenceData minimal() {
        return StandardReferenceData.MINIMAL;
    }

    /**
     * 返回一个不包含引用数据的实例。
     *
     * @return empty reference data
     */
    public static ReferenceData empty() {
        return ImmutableReferenceData.empty();
    }

    //-------------------------------------------------------------------------

    /**
     * 检查此引用数据是否包含指定的id。
     *
     * @param id the identifier to find
     * @return true if the reference data contains a value for the identifier
     */
    public default boolean containsValue(ReferenceDataId<?> id) {
        return id.queryValueOrNull(this) != null;
    }

    /**
     * 获取与指定标识符关联的引用数据值（通过ReferenceDataId获取对应的实例对象）。
     * 注意：该方法中获取到的val不可为null
     * <p>
     * 如果此id可以获取到对应val，则返回该val。否则，将引发异常
     *
     * @param <T> the type of the reference data value
     * @param id  the identifier to find
     * @return the reference data value
     * @throws ReferenceDataNotFoundException if the identifier is not found
     */
    public default <T> T getValue(ReferenceDataId<T> id) {
        T value = id.queryValueOrNull(this);
        if (value == null) {
            throw new ReferenceDataNotFoundException(ImmutableReferenceData.msgValueNotFound(id));
        }
        return value;
    }

    /**
     * java8：Optional 类是一个可以为null的容器对象，语法和数组型集合类似，用于更优雅的避免NullPointException
     * <p>
     * 获取与指定标识符关联的引用数据值（通过ReferenceDataId获取对应的实例对象）。
     * 与上面方法不同之处在于可以接收val为null的值
     * <p>
     * 如果此id可以获取到对应val，则返回该val。否则，返回空的可选值
     *
     * @param <T> the type of the reference data value
     * @param id  the identifier to find
     * @return 返回引用数据值的Optional容器，如果未找到则为空
     */
    public default <T> Optional<T> findValue(ReferenceDataId<T> id) {
        T value = id.queryValueOrNull(this);
        return Optional.ofNullable(value);
    }

    /**
     * 用于查询与指定id关联的引用数据值的低级方法，如果找不到，则返回空值。
     * <p>
     * 这是获取引用数据值的低级方法，返回空值而不是错误。
     * 程序应优先使用{@link #getValue(ReferenceDataId)}方法，而不是此方法。
     *
     * @param <T> the type of the reference data value
     * @param id  the identifier to find
     * @return the reference data value, null if not found
     */
    public abstract <T> T queryValueOrNull(ReferenceDataId<T> id);

    //-------------------------------------------------------------------------

    /**
     * 将两个ReferenceData实例结合
     * <p>
     * 结果结合了两组ReferenceData实例。如果可用，则返回两个ReferenceData的结合，否则从入参ReferenceData获取值。
     *
     * @param other the other reference data
     * @return the combined reference data
     */
    public default ReferenceData combinedWith(ReferenceData other) {
        return new CombinedReferenceData(this, other);
    }

}
