/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.log;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;

import static com.alibaba.csp.sentinel.util.ConfigUtil.addSeparator;

/**
 * Sentinel 日志基础配置类
 * <p>
 * 该类是 Sentinel 日志模块的核心配置类，负责管理和初始化日志相关的所有配置项。
 * </p>
 * <p>
 * 默认的日志根目录为 {@code ${user.home}/logs/csp/}，可通过 {@link #LOG_DIR} 属性覆盖。
 * 默认日志文件名不包含进程 ID，但当同一台机器上运行同一服务的多个实例时，
 * 可通过 {@link #LOG_NAME_USE_PID} 属性设置为 "true" 来启用进程 ID 区分日志文件。
 * </p>
 * <p>
 * <b>注：</b>本类为复制自 sentinel-core 1.8.6 的 {@code com.alibaba.csp.sentinel.log.LogBase}
 * 的本地覆盖副本（同包同名类在 classpath 上优先于 jar 内类加载），
 * 修改目的：注释掉类加载时的多处 {@code System.out/err} 控制台输出
 * （日志输出类型、字符集、目录、PID 开关、日志级别等 INFO 打印及初始化失败提示），减少启动日志噪音；
 * 升级 sentinel-core 版本时需同步比对原生类变更。
 * </p>
 *
 * @author Carpenter Lee
 * @author Eric Zhao
 */
public class LogBase {

    /** 日志目录的配置属性键，用于指定日志文件的存储目录 */
    public static final String LOG_DIR = "csp.sentinel.log.dir";
    /** 日志文件名是否包含进程 ID 的配置属性键 */
    public static final String LOG_NAME_USE_PID = "csp.sentinel.log.use.pid";
    /** 日志输出类型的配置属性键，可选值为 "file" 或 "console" */
    public static final String LOG_OUTPUT_TYPE = "csp.sentinel.log.output.type";
    /** 日志字符集的配置属性键 */
    public static final String LOG_CHARSET = "csp.sentinel.log.charset";
    /** 日志级别的配置属性键 */
    public static final String LOG_LEVEL = "csp.sentinel.log.level";

    /**
     * 将业务日志（如 RecordLog 和 CommandCenterLog）输出到文件
     */
    public static final String LOG_OUTPUT_TYPE_FILE = "file";
    /**
     * 将业务日志（如 RecordLog 和 CommandCenterLog）输出到控制台
     */
    public static final String LOG_OUTPUT_TYPE_CONSOLE = "console";
    /** 默认日志字符集：UTF-8 */
    public static final String LOG_CHARSET_UTF8 = "utf-8";

    /** 默认的日志子目录名称 */
    private static final String DIR_NAME = "logs" + File.separator + "csp";
    /** 用户主目录的系统属性键 */
    private static final String USER_HOME = "user.home";
    /** 默认日志级别：INFO */
    private static final Level LOG_DEFAULT_LEVEL = Level.INFO;


    /** 是否在日志文件名中使用进程 ID */
    private static boolean logNameUsePid;
    /** 日志输出类型（file 或 console） */
    private static String logOutputType;
    /** 日志文件的基础目录路径 */
    private static String logBaseDir;
    /** 日志字符集编码 */
    private static String logCharSet;
    /** 日志级别 */
    private static Level logLevel;

    /**
     * 静态初始化块，在类加载时执行日志配置的初始化
     * 先初始化默认值，再加载外部配置属性覆盖默认值
     */
    static {
        try {
            initializeDefault();
            loadProperties();
        } catch (Throwable t) {
//            System.err.println("[LogBase] FATAL ERROR when initializing logging config");
            t.printStackTrace();
        }
    }

    /**
     * 初始化日志配置的默认值
     * 默认不使用进程 ID、输出到文件、目录为用户主目录下 logs/csp、字符集 UTF-8、日志级别 INFO
     */
    private static void initializeDefault() {
        logNameUsePid = false;
        logOutputType = LOG_OUTPUT_TYPE_FILE;
        logBaseDir = addSeparator(System.getProperty(USER_HOME)) + DIR_NAME + File.separator;
        logCharSet = LOG_CHARSET_UTF8;
        logLevel = LOG_DEFAULT_LEVEL;
    }

    /**
     * 从外部配置中加载日志属性并覆盖默认值
     * 依次加载日志输出类型、字符集、目录、进程 ID 开关、日志级别
     */
    private static void loadProperties() {
        // 获取日志配置属性
        Properties properties = LogConfigLoader.getProperties();

        // 加载日志输出类型，仅支持 "file" 和 "console"，其他值会被重置为 "file"
        logOutputType = properties.get(LOG_OUTPUT_TYPE) == null ? logOutputType : properties.getProperty(LOG_OUTPUT_TYPE);
        if (!LOG_OUTPUT_TYPE_FILE.equalsIgnoreCase(logOutputType) && !LOG_OUTPUT_TYPE_CONSOLE.equalsIgnoreCase(logOutputType)) {
            logOutputType = LOG_OUTPUT_TYPE_FILE;
        }
//        System.out.println("INFO: Sentinel log output type is: " + logOutputType);

        // 加载日志字符集
        logCharSet = properties.getProperty(LOG_CHARSET) == null ? logCharSet : properties.getProperty(LOG_CHARSET);
//        System.out.println("INFO: Sentinel log charset is: " + logCharSet);

        // 加载日志目录，如果目录不存在则自动创建
        logBaseDir = properties.getProperty(LOG_DIR) == null ? logBaseDir : properties.getProperty(LOG_DIR);
        logBaseDir = addSeparator(logBaseDir);
        File dir = new File(logBaseDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("ERROR: create Sentinel log base directory error: " + logBaseDir);
            }
        }
//        System.out.println("INFO: Sentinel log base directory is: " + logBaseDir);

        // 加载是否在日志文件名中使用进程 ID 的开关
        String usePid = properties.getProperty(LOG_NAME_USE_PID);
        logNameUsePid = "true".equalsIgnoreCase(usePid);
//        System.out.println("INFO: Sentinel log name use pid is: " + logNameUsePid);

        // 加载日志级别，如果配置的级别无效则保持默认值
        String logLevelString = properties.getProperty(LOG_LEVEL);
        if (logLevelString != null && (logLevelString = logLevelString.trim()).length() > 0) {
            try {
                logLevel = Level.parse(logLevelString);
            } catch (IllegalArgumentException e) {
                System.out.println("Log level : " + logLevel + " is invalid. Use default : " + LOG_DEFAULT_LEVEL.toString());
            }
        }
//        System.out.println("INFO: Sentinel log level is: " + logLevel);
    }


    /**
     * 判断日志文件名是否应包含进程 ID
     * 该开关通过 {@link #LOG_NAME_USE_PID} 系统属性进行配置
     *
     * @return 如果日志文件名应包含进程 ID 则返回 true，否则返回 false
     */
    public static boolean isLogNameUsePid() {
        return logNameUsePid;
    }

    /**
     * 获取日志文件的基础目录路径，保证路径以文件分隔符结尾
     *
     * @return 日志文件的基础目录路径
     */
    public static String getLogBaseDir() {
        return logBaseDir;
    }

    /**
     * 获取日志文件的输出类型
     *
     * @return 日志输出类型，默认为 "file"
     */
    public static String getLogOutputType() {
        return logOutputType;
    }

    /**
     * 获取日志文件的字符集编码
     *
     * @return 日志字符集，默认为 "utf-8"
     */
    public static String getLogCharset() {
        return logCharSet;
    }

    /**
     * 获取日志级别
     *
     * @return 日志级别，默认为 INFO
     */
    public static Level getLogLevel() {
        return logLevel;
    }
}
