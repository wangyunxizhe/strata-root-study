/*
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.collect.io;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharSource;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.MapStream;
import com.opengamma.strata.collect.Unchecked;

import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An INI file.
 * <p>
 * 该类表示一个.ini文件，以及从{@link CharSource}解析该文件的功能。
 * <p>
 * 这里使用的ini文件格式非常简单。
 * 有2个元素：sectionMap和
 * There are two elements - key-value pairs and sections.
 * <p>
 * 基本元素是一个键值对。
 * 使用“=”符号将键与值分隔开。
 * 如“key=value”。等号和值可以省略，在这种情况下，值是空字符串。
 * <p>
 * 所有属性都被分组到“sections”中。section名称出现在由方括号包围的行上。不允许重复的section名称。
 * For example '[section]'.
 * <p>
 * Keys, values 和 section names 去空格，忽略空白行。
 * '#' 或者 ';'开头的是注释
 * No escape format is available.
 * 查找区分大小写。
 * <p>
 * 示例：
 * <pre>
 *  # line comment
 *  [foo]
 *  key = value
 *
 *  [bar]
 *  key = value
 *  month = January
 * </pre>
 * <p>
 * 此类的目的是分析基本格式。不支持变量的插值。
 */
public final class IniFile {

    /**
     * The INI sections.
     */
    private final ImmutableMap<String, PropertySet> sectionMap;

    //-------------------------------------------------------------------------

    /**
     * 将指定源解析为ini文件类。
     * <p>
     * 根据传入字符源（一般是.ini的文件名），要求使用.ini文件格式。可以为文件中的每行类容返回对应实例。
     * <p>
     * ini文件有时包含unicode。调用方负责处理此问题，例如使用{@link UnicodeBom}。
     *
     * @param source the INI file resource
     * @return the INI file
     * @throws UncheckedIOException     if an IO exception occurs
     * @throws IllegalArgumentException if the file cannot be parsed
     */
    public static IniFile of(CharSource source) {
        ArgChecker.notNull(source, "source");
        ImmutableList<String> lines = Unchecked.wrap(() -> source.readLines());
        ImmutableMap<String, ImmutableListMultimap<String, String>> parsedIni = parse(lines);
        ImmutableMap.Builder<String, PropertySet> builder = ImmutableMap.builder();
        parsedIni.forEach((sectionName, sectionData) -> builder.put(sectionName, PropertySet.of(sectionData)));
        return new IniFile(builder.build());
    }

    //-------------------------------------------------------------------------
    // parses the INI file format
    private static ImmutableMap<String, ImmutableListMultimap<String, String>> parse(ImmutableList<String> lines) {
        // 无法使用ArrayListMultiMap，因为它不保留键的顺序
        // 而ImmutableListMultimap（不可变）则保留键的顺序
        Map<String, ImmutableListMultimap.Builder<String, String>> ini = new LinkedHashMap<>();
        ImmutableListMultimap.Builder<String, String> currentSection = null;
        int lineNum = 0;
        for (String line : lines) {
            lineNum++;
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#") || line.startsWith(";")) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                String sectionName = line.substring(1, line.length() - 1).trim();
                if (ini.containsKey(sectionName)) {
                    throw new IllegalArgumentException("Invalid INI file, duplicate section not allowed, line " + lineNum);
                }
                currentSection = ImmutableListMultimap.builder();
                ini.put(sectionName, currentSection);

            } else if (currentSection == null) {
                throw new IllegalArgumentException("Invalid INI file, properties must be within a [section], line " + lineNum);

            } else {
                int equalsPosition = line.indexOf(" = ");
                equalsPosition = equalsPosition < 0 ? line.indexOf('=') : equalsPosition + 1;
                String key = (equalsPosition < 0 ? line.trim() : line.substring(0, equalsPosition).trim());
                String value = (equalsPosition < 0 ? "" : line.substring(equalsPosition + 1).trim());
                if (key.length() == 0) {
                    throw new IllegalArgumentException("Invalid INI file, empty key, line " + lineNum);
                }
                currentSection.put(key, value);
            }
        }
        return MapStream.of(ini).mapValues(b -> b.build()).toMap();
    }

    //-------------------------------------------------------------------------

    /**
     * 该工程中普遍使用的实例化对象的方法
     *
     * @param sectionMap the map of sections
     * @return the INI file
     */
    public static IniFile of(Map<String, PropertySet> sectionMap) {
        return new IniFile(ImmutableMap.copyOf(sectionMap));
    }

    //-------------------------------------------------------------------------

    /**
     * Restricted constructor.
     *
     * @param sectionMap the sections
     */
    private IniFile(ImmutableMap<String, PropertySet> sectionMap) {
        this.sectionMap = sectionMap;
    }

    //-------------------------------------------------------------------------

    /**
     * Returns the set of sections of this INI file.
     *
     * @return the set of sections
     */
    public ImmutableSet<String> sections() {
        return sectionMap.keySet();
    }

    /**
     * Returns the INI file as a map.
     * <p>
     * The iteration order of the map matches that of the original file.
     *
     * @return the INI file sections
     */
    public ImmutableMap<String, PropertySet> asMap() {
        return sectionMap;
    }

    //-------------------------------------------------------------------------

    /**
     * 检查此ini文件是否包含指定的section。
     *
     * @param name the section name
     * @return true if the section exists
     */
    public boolean contains(String name) {
        ArgChecker.notNull(name, "name");
        return sectionMap.containsKey(name);
    }

    /**
     * 获取此ini文件的单个section。
     * <p>
     * 这将返回与指定名称关联的section。如果该section不存在，则引发异常。
     *
     * @param name the section name
     * @return the INI file section
     * @throws IllegalArgumentException if the section does not exist
     */
    public PropertySet section(String name) {
        ArgChecker.notNull(name, "name");
        if (contains(name) == false) {
            throw new IllegalArgumentException("Unknown INI file section: " + name);
        }
        return sectionMap.get(name);
    }

    //-------------------------------------------------------------------------

    /**
     * Checks if this INI file equals another.
     * <p>
     * The comparison checks the content.
     *
     * @param obj the other file, null returns false
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IniFile) {
            return sectionMap.equals(((IniFile) obj).sectionMap);
        }
        return false;
    }

    /**
     * Returns a suitable hash code for the INI file.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return sectionMap.hashCode();
    }

    /**
     * Returns a string describing the INI file.
     *
     * @return the descriptive string
     */
    @Override
    public String toString() {
        return sectionMap.toString();
    }

}
