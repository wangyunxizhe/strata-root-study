/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.collect.io;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.Unchecked;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * 配置文件访问类
 * <p>
 * 这个类提供了配置的标准方法。开发者程序可以覆盖或添加此库提供的任何配置信息。
 * <p>
 * 默认情况下，有三组可识别的配置目录：
 * <ul>
 * <li>base
 * <li>library
 * <li>application
 * </ul>
 * <p>
 * 每组由十个目录组成，使用数字后缀：
 * <ul>
 * <li>{@code com/opengamma/strata/config/base}
 * <li>{@code com/opengamma/strata/config/base1}
 * <li>{@code com/opengamma/strata/config/base2}
 * <li>...
 * <li>{@code com/opengamma/strata/config/base9}
 * <li>{@code com/opengamma/strata/config/library}
 * <li>{@code com/opengamma/strata/config/library1}
 * <li>...
 * <li>{@code com/opengamma/strata/config/library9}
 * <li>{@code com/opengamma/strata/config/application}
 * <li>{@code com/opengamma/strata/config/application1}
 * <li>...
 * <li>{@code com/opengamma/strata/config/application9}
 * </ul>
 * 以上这些组成了一组完整的30个目录，用于搜索配置。
 * <p>
 * 搜索策略是在30个目录中的每个目录中查找相同的文件名。
 * 然后合并找到的所有文件，目录在列表的下方优先。
 * 因此，“application9”目录中的任何配置文件都将覆盖“appication1”目录中的同一文件，
 * 这将覆盖“library”组中的同一文件，这将进一步覆盖“base”组中的同一文件。
 * 总结：同名覆盖，后来居上
 * <p>
 * “base”是为Strata保留的。“library”是为直接建立在Strata上的库保留的。
 * <p>
 * 可以使用系统属性更改配置目录集“com.opengamma.strata.config.directories”。
 * 这必须是逗号分隔的列表，如“base，base1，base2，override，application”。
 * <p>
 * 总的来说，这类由配置管理要INI格式。
 * {@link #combinedIniFile(String)}方法是主入口点，返回从所有可用配置文件合并的单个ini文件。
 */
public final class ResourceConfig {

    /**
     * The logger.
     */
    private static final Logger log = Logger.getLogger(ResourceConfig.class.getName());
    /**
     * 配置文件的包/文件夹位置。
     */
    private static final String CONFIG_PACKAGE = "META-INF/com/opengamma/strata/config/";
    /**
     * 查询配置文件的默认目录集。
     */
    private static final ImmutableList<String> DEFAULT_DIRS = ImmutableList.of(
            "base",
            "base1",
            "base2",
            "base3",
            "base4",
            "base5",
            "base6",
            "base7",
            "base8",
            "base9",
            "library",
            "library1",
            "library2",
            "library3",
            "library4",
            "library5",
            "library6",
            "library7",
            "library8",
            "library9",
            "application",
            "application1",
            "application2",
            "application3",
            "application4",
            "application5",
            "application6",
            "application7",
            "application8",
            "application9");
    /**
     * 定义以逗号分隔的组列表的系统属性。
     */
    public static final String RESOURCE_DIRS_PROPERTY = "com.opengamma.strata.config.directories";
    /**
     * 资源组。如果出现错误，返回到已知的集合。
     */
    private static final ImmutableList<String> RESOURCE_DIRS;

    static {
        List<String> dirs = DEFAULT_DIRS;
        String property = null;
        try {
            property = System.getProperty(RESOURCE_DIRS_PROPERTY);
        } catch (Exception ex) {
            log.warning("Unable to access system property: " + ex.toString());
        }
        if (property != null && !property.isEmpty()) {
            try {
                dirs = Splitter.on(',').trimResults().splitToList(property);
            } catch (Exception ex) {
                log.warning("Invalid system property: " + property + ": " + ex.toString());
            }
            for (String dir : dirs) {
                if (!dir.matches("[A-Za-z0-9-]+")) {
                    log.warning("Invalid system property directory, must match regex [A-Za-z0-9-]+: " + dir);
                }
            }
        }
        log.config("Using directories: " + dirs);
        RESOURCE_DIRS = ImmutableList.copyOf(dirs);
    }

    /**
     * 用于链接的ini名称。
     */
    private static final String CHAIN_SECTION = "chain";
    /**
     * 用于链接的ini属性名。
     */
    private static final String CHAIN_NEXT = "chainNextFile";
    /**
     * 用于删除的ini属性名。
     */
    private static final String CHAIN_REMOVE = "chainRemoveSections";

    //-------------------------------------------------------------------------

    /**
     * 返回通过合并，指定名称的ini文件而形成的组合ini文件。
     * <p>
     * 这将在配置目录中查找具有指定名称的所有文件。
     * 每个文件都会被加载，结果是通过将文件合并为一个文件而形成的。
     * 有关合并过程的详细信息，请参阅{@link #combinedIniFile(List)}
     * <p>
     *
     * @param resourceName 资源文件名
     * @return the resource locators
     * @throws UncheckedIOException  if an IO exception occurs
     * @throws IllegalStateException if there is a configuration error
     */
    public static IniFile combinedIniFile(String resourceName) {
        ArgChecker.notNull(resourceName, "resourceName");
        return ResourceConfig.combinedIniFile(ResourceConfig.orderedResources(resourceName));
    }

    /**
     * 返回合并，指定的ini 文件形成 的组合ini文件。
     * <p>
     * 此方法的结果是通过将指定的文件合并在一起形成的。
     * 这些文件按顺序组合成一个链。
     * 列表中的第一个文件的优先级最低。
     * 列表中的最后一个文件具有最高优先级。
     * <p>
     * 算法从最高优先级文件的所有部分和属性开始。然后，它会添加后续文件中尚未存在的任何节或属性。
     * <p>
     * 可以通过提供'[chain]'节来控制算法。
     * 在“chain”部分中，如果“chainNextFile”为“false”，则停止处理，并忽略较低优先级的文件。
     * 如果指定了“chainRemoveSections”属性，则从链中较低的文件中忽略列出的节。
     *
     * @param resources 要读取的资源文件集合
     * @return 合并后的INI文件
     * @throws UncheckedIOException     if an IO error occurs
     * @throws IllegalArgumentException if the configuration is invalid
     */
    public static IniFile combinedIniFile(List<ResourceLocator> resources) {
        ArgChecker.notNull(resources, "resources");
        Map<String, PropertySet> sectionMap = new LinkedHashMap<>();
        for (ResourceLocator resource : resources) {
            IniFile file = IniFile.of(resource.getCharSource());
            if (file.contains(CHAIN_SECTION)) {
                PropertySet chainSection = file.section(CHAIN_SECTION);
                // remove everything from lower priority files if not chaining
                if (chainSection.contains(CHAIN_NEXT) && Boolean.parseBoolean(chainSection.value(CHAIN_NEXT)) == false) {
                    sectionMap.clear();
                } else {
                    // remove sections from lower priority files
                    sectionMap.keySet().removeAll(chainSection.valueList(CHAIN_REMOVE));
                }
            }
            // add entries, replacing existing data
            for (String sectionName : file.asMap().keySet()) {
                if (!sectionName.equals(CHAIN_SECTION)) {
                    sectionMap.merge(sectionName, file.section(sectionName), PropertySet::overrideWith);
                }
            }
        }
        return IniFile.of(sectionMap);
    }

    //-------------------------------------------------------------------------

    /**
     * 获取资源定位器的有序列表。
     * <p>
     * 通过指定名称在配置目录中查找所有匹配文件。
     * 结果从最低优先级（base）文件排序到最高优先级（application）文件。
     * 结果将始终至少包含一个文件，但可能包含多个文件。
     *
     * @param resourceName the resource name
     * @return the resource locators
     * @throws UncheckedIOException  if an IO exception occurs
     * @throws IllegalStateException if there is a configuration error
     */
    public static List<ResourceLocator> orderedResources(String resourceName) {
        ArgChecker.notNull(resourceName, "resourceName");
        return Unchecked.wrap(() -> orderedResources0(resourceName));
    }

    // find the list of resources
    private static List<ResourceLocator> orderedResources0(String classpathResourceName) throws IOException {
        ClassLoader classLoader = ResourceLocator.classLoader();
        List<String> names = new ArrayList<>();
        List<ResourceLocator> result = new ArrayList<>();
        for (String dir : RESOURCE_DIRS) {
            String name = CONFIG_PACKAGE + dir + "/" + classpathResourceName;
            names.add(name);
            List<URL> urls = Collections.list(classLoader.getResources(name));
            switch (urls.size()) {
                case 0:
                    continue;
                case 1:
                    result.add(ResourceLocator.ofClasspathUrl(urls.get(0)));
                    break;
                default:
                    // handle case where Strata is on the classpath more than once
                    // only accept this if the data being read is the same in all URLs
                    ResourceLocator baseResource = ResourceLocator.ofClasspathUrl(urls.get(0));
                    for (int i = 1; i < urls.size(); i++) {
                        ResourceLocator otherResource = ResourceLocator.ofClasspathUrl(urls.get(i));
                        if (!baseResource.getByteSource().contentEquals(otherResource.getByteSource())) {
                            log.severe("More than one file found on the classpath: " + name + ": " + urls);
                            throw new IllegalStateException("More than one file found on the classpath: " + name + ": " + urls);
                        }
                    }
                    result.add(baseResource);
                    break;
            }
        }
        if (result.isEmpty()) {
            log.severe("No resource files found on the classpath: " + names);
            throw new IllegalStateException("No files found on the classpath: " + names);
        }
        log.config(() -> "Resources found: " + result);
        return result;
    }

    //-------------------------------------------------------------------------
    private ResourceConfig() {
    }

}
