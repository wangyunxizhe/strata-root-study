/*
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.collect;

import com.google.common.base.CharMatcher;
import com.google.common.math.DoubleMath;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * 工具类：方法入参校验
 * <p>
 * 此实用程序在整个系统中用于验证方法的输入。大多数方法返回其验证的输入，允许如下模式：
 * <pre>
 *  // constructor
 *  public Person(String name, int age) {
 *    this.name = ArgChecker.notBlank(name, "name");
 *    this.age = ArgChecker.notNegative(age, "age");
 *  }
 * </pre>
 */
public final class ArgChecker {

    /**
     * Restricted constructor.
     */
    private ArgChecker() {
    }

    //-------------------------------------------------------------------------

    /**
     * 检查指定的布尔值是否为true。
     * <p>
     * 给定输入参数，只有当它为true时，才会正常返回。
     * For example:
     * <pre>
     *  ArgChecker.isTrue(collection.contains("value"));
     * </pre>
     * <p>
     * 方法入参可传{@link #isTrue(boolean, String)}.
     *
     * @param validIfTrue a boolean resulting from testing an argument
     * @throws IllegalArgumentException 入参为false时
     */
    public static void isTrue(boolean validIfTrue) {
        // return void, not the argument, as no need to check a boolean method argument
        if (!validIfTrue) {
            throw new IllegalArgumentException("Invalid argument, expression must be true");
        }
    }

    /**
     * 检查指定的布尔值是否为true。
     * <p>
     * 给定输入参数，只有当它为true时，才会正常返回。
     * For example:
     * <pre>
     *  ArgChecker.isTrue(collection.contains("value"), "Collection must contain 'value'");
     * </pre>
     *
     * @param validIfTrue a boolean resulting from testing an argument
     * @param message     错误原因提示
     * @throws IllegalArgumentException 入参为false时
     */
    public static void isTrue(boolean validIfTrue, String message) {
        // return void, not the argument, as no need to check a boolean method argument
        if (!validIfTrue) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 检查指定的布尔值是否为true。
     * <p>
     * 给定输入参数，只有当它为true时，才会正常返回。
     * For example:
     * <pre>
     *  ArgChecker.isTrue(collection.contains("value"), "Collection must contain 'value': {}", collection);
     * </pre>
     * <p>
     * This returns {@code void}, and not the value being checked, as there is
     * never a good reason to validate a boolean argument value.
     * <p>
     * 使用包含零到多个“{}”占位符的模板生成消息。
     * 每个占位符都被下一个可用参数替换。如果参数太少，那么消息将保留占位符。
     * 如果参数太多，那么多余的参数将附加到消息的末尾。
     * 不尝试格式化参数。
     * See {@link Messages#format(String, Object...)} for more details.
     *
     * @param validIfTrue a boolean resulting from testing an argument
     * @param message     带有{}的错误消息，不为空
     * @param arg         the message arguments
     * @throws IllegalArgumentException if the test value is false
     */
    public static void isTrue(boolean validIfTrue, String message, Object... arg) {
        // return void, not the argument, as no need to check a boolean method argument
        if (!validIfTrue) {
            throw new IllegalArgumentException(Messages.format(message, arg));
        }
    }

    /**
     * 检查指定的布尔值是否为true。
     * <p>
     * 给定输入参数，只有当它为true时，才会正常返回。
     * For example:
     * <pre>
     *  ArgChecker.isTrue(value &gt; check, "Value must be greater than check: {}", value);
     * </pre>
     * <p>
     * This returns {@code void}, and not the value being checked, as there is
     * never a good reason to validate a boolean argument value.
     * <p>
     * 使用包含零到多个“{}”占位符的模板生成消息。
     * 每个占位符都被下一个可用参数替换。如果参数太少，那么消息将保留占位符。
     * 如果参数太多，那么多余的参数将附加到消息的末尾。
     *
     * @param validIfTrue a boolean resulting from testing an argument
     * @param message     带有{}的错误消息，不为空
     * @param arg         the message argument
     * @throws IllegalArgumentException if the test value is false
     */
    public static void isTrue(boolean validIfTrue, String message, long arg) {
        // return void, not the argument, as no need to check a boolean method argument
        if (!validIfTrue) {
            throw new IllegalArgumentException(Messages.format(message, arg));
        }
    }

    /**
     * 检查指定的布尔值是否为true。
     * <p>
     * 给定输入参数，只有当它为true时，才会正常返回。
     * For example:
     * <pre>
     *  ArgChecker.isTrue(value &gt; check, "Value must be greater than check: {}", value);
     * </pre>
     * <p>
     * This returns {@code void}, and not the value being checked, as there is
     * never a good reason to validate a boolean argument value.
     * <p>
     * 使用包含零到多个“{}”占位符的模板生成消息。
     * 每个占位符都被下一个可用参数替换。如果参数太少，那么消息将保留占位符。
     * 如果参数太多，那么多余的参数将附加到消息的末尾。
     *
     * @param validIfTrue a boolean resulting from testing an argument
     * @param message     带有{}的错误消息，不为空
     * @param arg         the message argument
     * @throws IllegalArgumentException if the test value is false
     */
    public static void isTrue(boolean validIfTrue, String message, double arg) {
        // return void, not the argument, as no need to check a boolean method argument
        if (!validIfTrue) {
            throw new IllegalArgumentException(Messages.format(message, arg));
        }
    }

    /**
     * 检查指定的布尔值是否为false。
     * <p>
     * 给定输入参数，只有当它为false时，才会正常返回。
     * For example:
     * <pre>
     *  ArgChecker.isFalse(collection.contains("value"), "Collection must not contain 'value'");
     * </pre>
     * <p>
     * This returns {@code void}, and not the value being checked, as there is
     * never a good reason to validate a boolean argument value.
     *
     * @param validIfFalse a boolean resulting from testing an argument
     * @param message      错误原因提示, 不为空
     * @throws IllegalArgumentException if the test value is true
     */
    public static void isFalse(boolean validIfFalse, String message) {
        // return void, not the argument, as no need to check a boolean method argument
        if (validIfFalse) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 检查指定的布尔值是否为false。
     * <p>
     * 给定输入参数，只有当它为false时，才会正常返回。
     * For example:
     * <pre>
     *  ArgChecker.isFalse(collection.contains("value"), "Collection must not contain 'value': {}", collection);
     * </pre>
     * <p>
     * This returns {@code void}, and not the value being checked, as there is
     * never a good reason to validate a boolean argument value.
     * <p>
     * 使用包含零到多个“{}”占位符的模板生成消息。
     * 每个占位符都被下一个可用参数替换。如果参数太少，那么消息将保留占位符。
     * 如果参数太多，那么多余的参数将附加到消息的末尾。
     * 不尝试格式化参数。
     * See {@link Messages#format(String, Object...)} for more details.
     *
     * @param validIfFalse a boolean resulting from testing an argument
     * @param message      带有{}的错误消息，不为空
     * @param arg          the message arguments, not null
     * @throws IllegalArgumentException if the test value is true
     */
    public static void isFalse(boolean validIfFalse, String message, Object... arg) {
        // return void, not the argument, as no need to check a boolean method argument
        if (validIfFalse) {
            throw new IllegalArgumentException(Messages.format(message, arg));
        }
    }

    //-------------------------------------------------------------------------

    /**
     * 检查指定的参数是否为null。
     * <p>
     * 给定输入参数，不为null时正常返回。
     * For example, in a constructor:
     * <pre>
     *  this.name = ArgChecker.notNull(name, "name");
     * </pre>
     *
     * @param <T>      the type of the input argument reflected in the result
     * @param argument 需要被校验的参数值，为null时抛出异常
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException 为空时抛出异常
     */
    public static <T> T notNull(T argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(notNullMsg(name));
        }
        return argument;
    }

    // 组装异常错误提示
    private static String notNullMsg(String name) {
        return "Argument '" + name + "' must not be null";
    }

    /**
     * 检查指定的集合是否为null。
     * <p>
     * 给定输入参数，不为null时正常返回。
     * 此方法的一个用途是在流中：
     * <pre>
     *  ArgChecker.notNull(coll, "coll")
     *  coll.stream()
     *    .map(ArgChecker::notNullItem)
     *    ...
     * </pre>
     *
     * @param <T>      the type of the input argument reflected in the result
     * @param argument 需要被校验的参数值，为null时抛出异常
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null
     */
    public static <T> T notNullItem(T argument) {
        if (argument == null) {
            throw new IllegalArgumentException("Argument array/collection/map must not contain null");
        }
        return argument;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查指定的参数是否为null并与指定的模式匹配。
     * <p>
     * 仅当该参数不为null且与指定的正则表达式模式匹配时才返回。
     * For example, in a constructor:
     * <pre>
     *  this.name = ArgChecker.matches(REGEX_NAME, name, "name");
     * </pre>
     *
     * @param pattern  匹配规则
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static String matches(Pattern pattern, String argument, String name) {
        notNull(pattern, "pattern");
        notNull(argument, name);
        if (!pattern.matcher(argument).matches()) {
            throw new IllegalArgumentException(matchesMsg(pattern, name, argument));
        }
        return argument;
    }

    // 组装错误提示
    private static String matchesMsg(Pattern pattern, String name, String value) {
        return "Argument '" + name + "' with value '" + value + "' must match pattern: " + pattern;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查指定的参数是否为null且与指定字符串是否相匹配。
     * <p>
     * 不为null且与指定字符串{@link CharMatcher} 相匹配时才返回。
     * For example, in a constructor:
     * <pre>
     *  this.name = ArgChecker.matches(REGEX_NAME, 1, Integer.MAX_VALUE, name, "name", "[A-Z]+");
     * </pre>
     *
     * @param matcher         要匹配的范本，不为null
     * @param minLength       the minimum length to allow
     * @param maxLength       the minimum length to allow
     * @param argument        需要被校验的参数值
     * @param name            参数名，不为null
     * @param equivalentRegex 等价于正则表达式模式
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static String matches(
            CharMatcher matcher,
            int minLength,
            int maxLength,
            String argument,
            String name,
            String equivalentRegex) {

        notNull(matcher, "pattern");
        notNull(argument, name);
        if (argument.length() < minLength || argument.length() > maxLength || !matcher.matchesAllOf(argument)) {
            throw new IllegalArgumentException(matchesMsg(matcher, name, argument, equivalentRegex));
        }
        return argument;
    }

    // 组装错误提示
    private static String matchesMsg(CharMatcher matcher, String name, String value, String equivalentRegex) {
        return "Argument '" + name + "' with value '" + value + "' must match pattern: " + equivalentRegex;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查指定的参数是否为null且是否为空白字符。
     * <p>
     * 对于输入参数，只有当输入不为null且至少包含一个非空白字符时，才会正常返回。
     * 这通常与{@code trim()}联合使用。
     * For example, in a constructor:
     * <pre>
     *  this.name = ArgChecker.notBlank(name, "name").trim();
     * </pre>
     * <p>
     * 先使用notBlank，再使用trim
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or blank
     */
    public static String notBlank(String argument, String name) {
        notNull(argument, name);
        if (argument.trim().isEmpty()) {
            throw new IllegalArgumentException(notBlankMsg(name));
        }
        return argument;
    }

    // 组装错误提示
    private static String notBlankMsg(String name) {
        return "Argument '" + name + "' must not be blank";
    }

    //-------------------------------------------------------------------------

    /**
     * 检查指定的参数是否为空。
     * <p>
     * 入参是不为null并且包含至少一个字符（包含" "）时才会返回。
     * See also {@link #notBlank(String, String)}.
     * For example, in a constructor:
     * <pre>
     *  this.name = ArgChecker.notEmpty(name, "name");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static String notEmpty(String argument, String name) {
        notNull(argument, name);
        if (argument.isEmpty()) {
            throw new IllegalArgumentException(notEmptyMsg(name));
        }
        return argument;
    }

    // 组装错误提示
    private static String notEmptyMsg(String name) {
        return "Argument '" + name + "' must not be empty";
    }

    /**
     * 检查指定的参数数组是否为空。
     * <p>
     * 入参数组不为null且包含至少一个元素时返回。注意：它里面的元素可能为null。
     * For example, in a constructor:
     * <pre>
     *  this.names = ArgChecker.notEmpty(names, "names");
     * </pre>
     *
     * @param <T>      具体数组的类型（String，int。。。）
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static <T> T[] notEmpty(T[] argument, String name) {
        notNull(argument, name);
        if (argument.length == 0) {
            throw new IllegalArgumentException(notEmptyArrayMsg(name));
        }
        return argument;
    }

    // 组装错误提示
    private static String notEmptyArrayMsg(String name) {
        return "Argument array '" + name + "' must not be empty";
    }

    /**
     * 检查指定的参数数组是否为空。
     * <p>
     * 给定输入参数，仅当它为非空且包含至少一个元素时返回。
     * For example, in a constructor:
     * <pre>
     *  this.values = ArgChecker.notEmpty(values, "values");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static int[] notEmpty(int[] argument, String name) {
        notNull(argument, name);
        if (argument.length == 0) {
            throw new IllegalArgumentException(notEmptyArrayMsg(name));
        }
        return argument;
    }

    /**
     * 检查指定的参数数组是否为空。
     * <p>
     * 给定输入参数，仅当它为非空且包含至少一个元素时返回。
     * For example, in a constructor:
     * <pre>
     *  this.values = ArgChecker.notEmpty(values, "values");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static long[] notEmpty(long[] argument, String name) {
        notNull(argument, name);
        if (argument.length == 0) {
            throw new IllegalArgumentException(notEmptyArrayMsg(name));
        }
        return argument;
    }

    /**
     * 检查指定的参数数组是否为空。
     * <p>
     * 给定输入参数，仅当它为非空且包含至少一个元素时返回。
     * For example, in a constructor:
     * <pre>
     *  this.values = ArgChecker.notEmpty(values, "values");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static double[] notEmpty(double[] argument, String name) {
        notNull(argument, name);
        if (argument.length == 0) {
            throw new IllegalArgumentException(notEmptyArrayMsg(name));
        }
        return argument;
    }

    /**
     * 检查指定的参数集合是否为空。
     * <p>
     * 入参数组不为null且包含至少一个元素时返回。注意：它里面的元素可能为null。
     * For example, in a constructor:
     * <pre>
     *  this.values = ArgChecker.notEmpty(values, "values");
     * </pre>
     *
     * @param <T>      结果中反映的入参集合的元素类型
     * @param <I>      入参集合的类型
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static <T, I extends Iterable<T>> I notEmpty(I argument, String name) {
        notNull(argument, name);
        if (!argument.iterator().hasNext()) {
            throw new IllegalArgumentException(notEmptyIterableMsg(name));
        }
        return argument;
    }

    // 组装错误提示
    private static String notEmptyIterableMsg(String name) {
        return "Argument iterable '" + name + "' must not be empty";
    }

    /**
     * 检查指定的参数集合是否为空。
     * <p>
     * 给定输入参数，仅当它为非空且包含至少一个元素时返回。如果集合允许空值，则该元素可能包含空值。
     * For example, in a constructor:
     * <pre>
     *  this.values = ArgChecker.notEmpty(values, "values");
     * </pre>
     *
     * @param <T>      结果中反映的入参集合的元素类型
     * @param <C>      入参集合的类型
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static <T, C extends Collection<T>> C notEmpty(C argument, String name) {
        notNull(argument, name);
        if (argument.isEmpty()) {
            throw new IllegalArgumentException(notEmptyCollectionMsg(name));
        }
        return argument;
    }

    // 组装错误提示
    private static String notEmptyCollectionMsg(String name) {
        return "Argument collection '" + name + "' must not be empty";
    }

    /**
     * 检查指定的参数map是否为非空。
     * <p>
     * 给定输入参数，仅当它为非空且包含至少一个键值对时返回。如果集合允许空值，则该元素可能包含空值。
     * For example, in a constructor:
     * <pre>
     *  this.keyValues = ArgChecker.notEmpty(keyValues, "keyValues");
     * </pre>
     *
     * @param <K>      结果中反映的入参的key类型
     * @param <V>      结果中反映的入参的value类型
     * @param <M>      入参的map类型
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or empty
     */
    public static <K, V, M extends Map<K, V>> M notEmpty(M argument, String name) {
        notNull(argument, name);
        if (argument.isEmpty()) {
            throw new IllegalArgumentException(notEmptyMapMsg(name));
        }
        return argument;
    }

    // 组装错误提示
    private static String notEmptyMapMsg(String name) {
        return "Argument map '" + name + "' must not be empty";
    }

    //-------------------------------------------------------------------------

    /**
     * 检查指定的参数数组是否为null且数组内部是否包含null值。
     * <p>
     * 数组不为null且数组内部不包含null值时返回。
     * For example, in a constructor:
     * <pre>
     *  this.values = ArgChecker.noNulls(values, "values");
     * </pre>
     *
     * @param <T>      数组类型
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or contains nulls
     */
    public static <T> T[] noNulls(T[] argument, String name) {
        notNull(argument, name);
        for (int i = 0; i < argument.length; i++) {
            if (argument[i] == null) {
                throw new IllegalArgumentException("Argument array '" + name + "' must not contain null at index " + i);
            }
        }
        return argument;
    }

    /**
     * 检查指定的参数集合是否为null且集合内部是否包含null值。
     * <p>
     * 集合不为null且集合内部不包含null值时返回。
     * For example, in a constructor:
     * <pre>
     *  this.values = ArgChecker.noNulls(values, "values");
     * </pre>
     *
     * @param <T>      结果中反应的入参集合中的元素类型
     * @param <I>      入参集合类型
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or contains nulls
     */
    public static <T, I extends Iterable<T>> I noNulls(I argument, String name) {
        notNull(argument, name);
        for (Object obj : argument) {
            if (obj == null) {
                throw new IllegalArgumentException("Argument iterable '" + name + "' must not contain null");
            }
        }
        return argument;
    }

    /**
     * 检查指定的参数map是否为null且map内部是否包含null。
     * <p>
     * map不为null且map内部不包含null时返回。
     * For example, in a constructor:
     * <pre>
     *  this.keyValues = ArgChecker.noNulls(keyValues, "keyValues");
     * </pre>
     *
     * @param <K>      结果中反映的入参的key类型
     * @param <V>      结果中反映的入参的value类型
     * @param <M>      入参的map类型
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}, not null
     * @throws IllegalArgumentException if the input is null or contains nulls
     */
    public static <K, V, M extends Map<K, V>> M noNulls(M argument, String name) {
        notNull(argument, name);
        for (Entry<K, V> entry : argument.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Argument map '" + name + "' must not contain a null key");
            }
            if (entry.getValue() == null) {
                throw new IllegalArgumentException("Argument map '" + name + "' must not contain a null value");
            }
        }
        return argument;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查参数是否为负数。
     * <p>
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notNegative(amount, "amount");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the input is negative
     */
    public static int notNegative(int argument, String name) {
        if (argument < 0) {
            throw new IllegalArgumentException(notNegativeMsg(name));
        }
        return argument;
    }

    // 组装错误提示
    private static String notNegativeMsg(String name) {
        return "Argument '" + name + "' must not be negative";
    }

    /**
     * 检查参数是否为负数。
     * <p>
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notNegative(amount, "amount");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the input is negative
     */
    public static long notNegative(long argument, String name) {
        if (argument < 0) {
            throw new IllegalArgumentException(notNegativeMsg(name));
        }
        return argument;
    }

    /**
     * 检查参数是否为负数。
     * <p>
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notNegative(amount, "amount");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the input is negative
     */
    public static double notNegative(double argument, String name) {
        if (argument < 0) {
            throw new IllegalArgumentException(notNegativeMsg(name));
        }
        return argument;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查参数是否为负数或0。
     * <p>
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notNegative(amount, "amount");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the input is negative
     */
    public static int notNegativeOrZero(int argument, String name) {
        if (argument <= 0) {
            throw new IllegalArgumentException(notNegativeOrZeroMsg(name, argument));
        }
        return argument;
    }

    // 组装错误提示
    private static String notNegativeOrZeroMsg(String name, double argument) {
        return "Argument '" + name + "' must not be negative or zero but has value " + argument;
    }

    /**
     * 检查参数是否为负数或0。
     * <p>
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notNegative(amount, "amount");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the input is negative
     */
    public static long notNegativeOrZero(long argument, String name) {
        if (argument <= 0) {
            throw new IllegalArgumentException(notNegativeOrZeroMsg(name, argument));
        }
        return argument;
    }

    /**
     * 检查参数是否为负数或0。
     * <p>
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notNegative(amount, "amount");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the input is negative
     */
    public static double notNegativeOrZero(double argument, String name) {
        if (argument <= 0) {
            throw new IllegalArgumentException(notNegativeOrZeroMsg(name, argument));
        }
        return argument;
    }

    /**
     * 检查参数是否在给定精度内大于0。
     * <p>
     * argument和tolerance都要大于0，才能用该方法计算
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notNegativeOrZero(amount, 0.0001d, "amount");
     * </pre>
     *
     * @param argument  需要被校验的参数值
     * @param tolerance 公差
     * @param name      参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException 参数的绝对值小于eps时抛出（eps：每股盈余）
     */
    public static double notNegativeOrZero(double argument, double tolerance, String name) {
        if (DoubleMath.fuzzyEquals(argument, 0, tolerance)) {
            throw new IllegalArgumentException("Argument '" + name + "' must not be zero");
        }
        if (argument < 0) {
            throw new IllegalArgumentException("Argument '" + name + "' must be greater than zero but has value " + argument);
        }
        return argument;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查参数是否等于零。
     * <p>
     * 不等于0时返回
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notZero(amount, "amount");
     * </pre>
     *
     * @param argument 需要被校验的参数值
     * @param name     参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the argument is zero
     */
    public static double notZero(double argument, String name) {
        if (argument == 0d || argument == -0d) {
            throw new IllegalArgumentException("Argument '" + name + "' must not be zero");
        }
        return argument;
    }

    /**
     * 检查参数在给定精度内不等于0。
     * <p>
     * argument要不等于0，tolerance要大于0，才能用该方法计算
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.notZero(amount, 0.0001d, "amount");
     * </pre>
     *
     * @param argument  需要被校验的参数值
     * @param tolerance 公差
     * @param name      参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException 参数的绝对值小于公差时抛出
     */
    public static double notZero(double argument, double tolerance, String name) {
        if (DoubleMath.fuzzyEquals(argument, 0d, tolerance)) {
            throw new IllegalArgumentException("Argument '" + name + "' must not be zero");
        }
        return argument;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查参数是否在指定的范围内。{@code low <= x < high}.
     * <p>
     * 入参在指定范围内（包括最小边界），则返回true。
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.inRange(amount, 0d, 1d, "amount");
     * </pre>
     *
     * @param argument      需要被校验的参数值
     * @param lowInclusive  最小边界
     * @param highExclusive 最大边界
     * @param name          参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the argument is outside the valid range
     */
    public static double inRange(double argument, double lowInclusive, double highExclusive, String name) {
        if (argument < lowInclusive || argument >= highExclusive) {
            throw new IllegalArgumentException(
                    Messages.format("Expected {} <= '{}' < {}, but found {}",
                            lowInclusive, name, highExclusive, argument));
        }
        return argument;
    }

    /**
     * 检查参数是否在指定的范围内。{@code low <= x <= high}.
     * <p>
     * 入参在指定范围内（包括两个边界），则返回true。
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.inRangeInclusive(amount, 0d, 1d, "amount");
     * </pre>
     *
     * @param argument      需要被校验的参数值
     * @param lowInclusive  最小边界
     * @param highInclusive 最大边界
     * @param name          参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the argument is outside the valid range
     */
    public static double inRangeInclusive(double argument, double lowInclusive, double highInclusive, String name) {
        if (argument < lowInclusive || argument > highInclusive) {
            throw new IllegalArgumentException(
                    Messages.format("Expected {} <= '{}' <= {}, but found {}",
                            lowInclusive, name, highInclusive, argument));
        }
        return argument;
    }

    /**
     * 检查参数是否在指定的范围内。{@code low < x < high}.
     * <p>
     * 入参在指定范围内（不包括两个边界），则返回true。
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.inRangeExclusive(amount, 0d, 1d, "amount");
     * </pre>
     *
     * @param argument      需要被校验的参数值
     * @param lowExclusive  最小边界
     * @param highExclusive 最大边界
     * @param name          参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the argument is outside the valid range
     */
    public static double inRangeExclusive(double argument, double lowExclusive, double highExclusive, String name) {
        if (argument <= lowExclusive || argument >= highExclusive) {
            throw new IllegalArgumentException(
                    Messages.format("Expected {} < '{}' < {}, but found {}",
                            lowExclusive, name, highExclusive, argument));
        }
        return argument;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查参数是否在指定的范围内。 {@code low <= x < high}.
     * <p>
     * 入参在指定范围内（包括下边界，但不包括上边界），则返回true。
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.inRange(amount, 0d, 1d, "amount");
     * </pre>
     *
     * @param argument      需要被校验的参数值
     * @param lowInclusive  最小边界
     * @param highExclusive 最大边界
     * @param name          参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the argument is outside the valid range
     */
    public static int inRange(int argument, int lowInclusive, int highExclusive, String name) {
        if (argument < lowInclusive || argument >= highExclusive) {
            throw new IllegalArgumentException(
                    Messages.format("Expected {} <= '{}' < {}, but found {}",
                            lowInclusive, name, highExclusive, argument));
        }
        return argument;
    }

    /**
     * 检查参数是否在指定的范围内。{@code low <= x <= high}.
     * <p>
     * 入参在指定范围内（包括两个边界），则返回true。
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.inRangeInclusive(amount, 0d, 1d, "amount");
     * </pre>
     *
     * @param argument      需要被校验的参数值
     * @param lowInclusive  最小边界
     * @param highInclusive 最大边界
     * @param name          参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the argument is outside the valid range
     */
    public static int inRangeInclusive(int argument, int lowInclusive, int highInclusive, String name) {
        if (argument < lowInclusive || argument > highInclusive) {
            throw new IllegalArgumentException(
                    Messages.format("Expected {} <= '{}' <= {}, but found {}",
                            lowInclusive, name, highInclusive, argument));
        }
        return argument;
    }

    /**
     * 检查参数是否在指定的范围内。{@code low < x < high}.
     * <p>
     * 入参在指定范围内（不包括两个边界），则返回true。
     * For example, in a constructor:
     * <pre>
     *  this.amount = ArgChecker.inRangeExclusive(amount, 0d, 1d, "amount");
     * </pre>
     *
     * @param argument      需要被校验的参数值
     * @param lowExclusive  最小边界
     * @param highExclusive 最大边界
     * @param name          参数名，不为null
     * @return the input {@code argument}
     * @throws IllegalArgumentException if the argument is outside the valid range
     */
    public static int inRangeExclusive(int argument, int lowExclusive, int highExclusive, String name) {
        if (argument <= lowExclusive || argument >= highExclusive) {
            throw new IllegalArgumentException(
                    Messages.format("Expected {} < '{}' < {}, but found {}",
                            lowExclusive, name, highExclusive, argument));
        }
        return argument;
    }

    //-------------------------------------------------------------------------

    /**
     * 对两个参数进行比较
     * <p>
     * compareTo结果大于等于0，报错
     *
     * @param <T>   the type
     * @param obj1  the first object, null throws an exception
     * @param obj2  the second object, null throws an exception
     * @param name1 the first argument name, not null
     * @param name2 the second argument name, not null
     * @throws IllegalArgumentException if either input is null or they are not in order
     */
    public static <T> void inOrderNotEqual(Comparable<? super T> obj1, T obj2, String name1, String name2) {
        notNull(obj1, name1);
        notNull(obj2, name2);
        if (obj1.compareTo(obj2) >= 0) {
            throw new IllegalArgumentException(
                    Messages.format("Invalid order: Expected '{}' < '{}', but found: '{}' >= '{}",
                            name1, name2, obj1, obj2));
        }
    }

    /**
     * 对两个参数进行比较
     * <p>
     * compareTo结果大于0，报错
     *
     * @param <T>   the type
     * @param obj1  the first object, null throws an exception
     * @param obj2  the second object, null throws an exception
     * @param name1 the first argument name, not null
     * @param name2 the second argument name, not null
     * @throws IllegalArgumentException if either input is null or they are not in order
     */
    public static <T> void inOrderOrEqual(Comparable<? super T> obj1, T obj2, String name1, String name2) {
        notNull(obj1, name1);
        notNull(obj2, name2);
        if (obj1.compareTo(obj2) > 0) {
            throw new IllegalArgumentException(
                    Messages.format("Invalid order: Expected '{}' <= '{}', but found: '{}' > '{}",
                            name1, name2, obj1, obj2));
        }
    }

}
