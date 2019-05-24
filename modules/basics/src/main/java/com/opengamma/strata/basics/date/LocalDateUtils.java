/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics.date;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 日期推算工具类
 */
final class LocalDateUtils {

    // 每月的第一天减去标准年的一天（译）
    // 数组长度13，忽略元素0，因此可以直接查询1到12月（译）
    // （标准年）忽略元素0的话，31是1月的总天数，59是1月+2月的总天数。。。
    private static final int[] STANDARD = {0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    // 每月的第一天减去闰年的一天（译）
    // 数组长度13，忽略元素0，因此可以直接查询1到12月（译）
    private static final int[] LEAP = {0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    /**
     * Restricted constructor.
     */
    private LocalDateUtils() {
    }

    //-------------------------------------------------------------------------

    /**
     * 查看入参日期在该年中是第几天了
     * <p>
     * Faster than the JDK method.
     *
     * @param date 入参日期
     * @return 在该年中是第几天
     */
    static int doy(LocalDate date) {
        int[] lookup = (date.isLeapYear() ? LEAP : STANDARD);//查看该年份是闰年还是标准年
        return lookup[date.getMonthValue()] + date.getDayOfMonth();
    }

    /**
     * 向入参日期添加指定天数。
     * <p>
     * Faster than the JDK method.
     *
     * @param date      需要被增加的入参日期
     * @param daysToAdd 需要增加的天数
     * @return 增加后的日期
     */
    static LocalDate plusDays(LocalDate date, int daysToAdd) {
        if (daysToAdd == 0) {
            return date;
        }
        // 按指定增加的天数，推算日期
        // 注意跨年跨月时的进位计算
        long dom = date.getDayOfMonth() + daysToAdd;//添加完成后的天数
        //if语句中只判断加完之后 小于59的（1月59日相当于2月28日，2月59日相当于3月31日）
        if (dom > 0 && dom <= 59) {
            int monthLen = date.lengthOfMonth();//当月的天数
            int month = date.getMonthValue();
            int year = date.getYear();
            if (dom <= monthLen) {
                return LocalDate.of(year, month, (int) dom);
            } else if (month < 12) {//加完的日期，大于当月天数，转下一月，但并未跨年
                return LocalDate.of(year, month + 1, (int) (dom - monthLen));
            } else {//加完的日期，大于当月天数，转下一月，且已跨年
                return LocalDate.of(year + 1, 1, (int) (dom - monthLen));
            }
        }
        long mjDay = Math.addExact(date.toEpochDay(), daysToAdd);
        return LocalDate.ofEpochDay(mjDay);
    }

    /**
     * 返回两个日期之间的天数。
     * <p>
     * Faster than the JDK method.
     *
     * @param firstDate  第一个日期
     * @param secondDate 第二个日期（小于第一个日期）
     * @return the new date
     */
    static long daysBetween(LocalDate firstDate, LocalDate secondDate) {
        int firstYear = firstDate.getYear();
        int secondYear = secondDate.getYear();
        if (firstYear == secondYear) {
            return doy(secondDate) - doy(firstDate);
        }
        if ((firstYear + 1) == secondYear) {
            return (firstDate.lengthOfYear() - doy(firstDate)) + doy(secondDate);
        }
        return secondDate.toEpochDay() - firstDate.toEpochDay();
    }

    //-------------------------------------------------------------------------

    /**
     * 给定范围内包含的日期集。
     * <p>
     * 这将返回一个由范围中的每个日期组成的集合。集合是有序的。
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return the stream of dates from the start to the end
     */
    static Stream<LocalDate> stream(LocalDate startInclusive, LocalDate endExclusive) {
        Iterator<LocalDate> it = new Iterator<LocalDate>() {
            private LocalDate current = startInclusive;

            @Override
            public LocalDate next() {
                LocalDate result = current;
                current = plusDays(current, 1);
                return result;
            }

            @Override
            public boolean hasNext() {
                return current.isBefore(endExclusive);
            }
        };
        long count = endExclusive.toEpochDay() - startInclusive.toEpochDay() + 1;
        Spliterator<LocalDate> spliterator = Spliterators.spliterator(it, count,
                Spliterator.IMMUTABLE | Spliterator.NONNULL |
                        Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.SORTED |
                        Spliterator.SIZED | Spliterator.SUBSIZED);
        return StreamSupport.stream(spliterator, false);
    }

}
