/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maozi;

import cn.hutool.extra.spring.SpringUtil;
import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.constant.LogTag;
import com.maozi.common.context.ApplicationEnvironmentContext;
import com.maozi.common.enums.EnvironmentType;
import com.maozi.common.spi.ConfigInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 应用启动基类
 * <p>
 * 所有微服务应用的启动入口均需继承此类。统一配置 Spring Boot 应用启动参数，
 * 包括 Nacos 配置中心连接、日志级别、异步任务、缓存、定时任务、
 * 服务发现和 Feign 客户端等功能的自动开启。
 * </p>
 *
 * @author maozi
 */
@Slf4j
@SpringBootApplication
@Import({SpringUtil.class})
@DependsOn({ApplicationEnvironmentContext.CLASS_NAME})
public class BaseApplication {

    /** 运行时配置初始化器扫描模式，匹配 classpath（含 JAR 内）下 META-INF/run/config 目录中的所有 .properties 文件，文件内容为实现类全路径 */
    private static final String CONFIG_LOCATION_PATTERN = "classpath*:META-INF/run/config/*.properties";

    /**
     * 应用启动入口方法
     * <p>
     * 初始化系统属性、启动 Spring Boot 应用，记录启动日志（包括初始化时间、
     * 服务端口、Nacos 地址和加载的配置文件）。启动失败时记录错误日志并退出。
     * </p>
     *
     * @param args 命令行参数
     */
    protected static void ApplicationRun(String[] args) {

        initProperties();

        List<ConfigInitializer> initializers = initFileProperties();

        Map<String, String> logs = new LinkedHashMap<>();

        long begin = System.currentTimeMillis();

        SpringApplicationBuilder builder = new SpringApplicationBuilder(BaseApplication.class);

        try {

            ConfigurableApplicationContext context = builder.bannerMode(Mode.OFF).run(args);
            logs.put(LogTag.INIT_TIME, (System.currentTimeMillis() - begin) + " ms");

            if (context instanceof WebServerApplicationContext webServerContext) {
                int actualPort = webServerContext.getWebServer().getPort();
                logs.put(LogTag.SERVICE_PORT, Integer.toString(actualPort));
            }

            appendInitializerLogs(initializers, logs);
            LogUtil.info(log,logs);

        } catch (Exception e) {

            LogUtil.error(log,e);

            appendInitializerLogs(initializers, logs);
            logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());

            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            logs.put(LogTag.ERROR_LINE, stackTraceElement.toString());

            LogUtil.error(log,logs);
            System.exit(0);

        }

    }

    /**
     * 初始化系统属性
     * <p>
     * 设置 Spring Boot 启动参数，包括：应用名称、Nacos 配置中心地址、
     * 基础配置文件列表、日志级别和输出路径等。服务级别的额外配置文件
     * 通过 {@code application-nacos-config-service} 系统属性追加。
     * </p>
     */
    private static void initProperties() {

        Properties properties = System.getProperties();

        String environment = (String) properties.get("environment");
        if(ObjectUtil.isNullEmpty(environment)){
            environment = System.getenv("ENVIRONMENT");
            if(ObjectUtil.isNullEmpty(environment)){
                environment = EnvironmentType.LOCAL.getDesc();
            }
            properties.put("environment",environment);
        }

        properties.put("spring.main.log-startup-info",false);
        properties.put("spring.main.allow-circular-references",true);
        properties.put("spring.application.name", "maozi-cloud-${application-project-abbreviation}-service");

        properties.put("logging.level.root", "ERROR");
        properties.put("logging.level.com.maozi", "INFO");
        if(!Objects.equals(environment, EnvironmentType.LOCAL.getDesc())){
            properties.put("logging.appender","ASYNC_FILE");
            properties.put("logging.file.name","logs/${spring.application.name}.log");
        }else{
            properties.put("logging.appender","ASYNC_CONSOLE");
        }

    }

    private static List<ConfigInitializer> initFileProperties() {

        Properties properties = System.getProperties();

        List<ConfigInitializer> initializers = new ArrayList<>();

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(CONFIG_LOCATION_PATTERN);
            for (Resource resource : resources) {
                Properties registrations = new Properties();
                try (InputStream inputStream = resource.getInputStream()) {
                    registrations.load(inputStream);
                }
                for (String className : registrations.stringPropertyNames()) {
                    ConfigInitializer initializer = instantiateInitializer(className);
                    initializer.initialize(properties);
                    initializers.add(initializer);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("加载运行时配置初始化器失败：" + CONFIG_LOCATION_PATTERN, e);
        } catch (Exception e) {
            throw new RuntimeException("执行运行时配置初始化器失败", e);
        }

        return initializers;

    }

    /**
     * 依次追加各初始化器的启动日志
     * <p>
     * 在 Spring Boot 应用启动（成功或失败）之后调用，将各 {@link ConfigInitializer}
     * 的诊断信息写入 logs 以便统一输出。
     * </p>
     *
     * @param initializers 已加载的配置初始化器列表
     * @param logs 启动日志容器
     */
    private static void appendInitializerLogs(List<ConfigInitializer> initializers, Map<String, String> logs) {
        for (ConfigInitializer initializer : initializers) {
            initializer.appendLogs(logs);
        }
    }

    /**
     * 实例化配置初始化器
     *
     * @param className 实现类全路径
     * @return 配置初始化器实例
     */
    private static ConfigInitializer instantiateInitializer(String className) {
        try {
            Class<?> clazz = Class.forName(className.trim());
            return (ConfigInitializer) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("实例化配置初始化器失败：" + className, e);
        }
    }

}
