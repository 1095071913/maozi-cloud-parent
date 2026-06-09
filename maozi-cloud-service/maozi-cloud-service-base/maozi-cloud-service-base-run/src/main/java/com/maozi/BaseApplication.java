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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.LinkedHashMap;
import java.util.Map;
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
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@Import({SpringUtil.class})
@DependsOn({"applicationEnvironmentContext"})
public class BaseApplication {

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

        long begin = System.currentTimeMillis();

        SpringApplicationBuilder builder = new SpringApplicationBuilder(BaseApplication.class);

        Map<String, String> logs = new LinkedHashMap<>();

        try {

            builder.bannerMode(Mode.OFF).run(args);
            logs.put(LogTag.INIT_TIME, (System.currentTimeMillis() - begin) + " ms");

            logs.put(LogTag.SERVICE_PORT, ApplicationEnvironmentContext.SERVICE_PORT);
            logs.put(LogTag.NACOS, ApplicationEnvironmentContext.CONFIG_ADDR);
            logs.put(LogTag.CONFIG, ApplicationEnvironmentContext.LOAD_CONFIG);

            LogUtil.info(log,logs);

        } catch (Exception e) {

            LogUtil.error(log,e);

            StackTraceElement stackTraceElement = e.getStackTrace()[0];

            logs.put(LogTag.NACOS, ApplicationEnvironmentContext.CONFIG_ADDR);
            logs.put(LogTag.CONFIG, ApplicationEnvironmentContext.LOAD_CONFIG);
            logs.put(LogTag.ERROR_DESC, e.getLocalizedMessage());
            logs.put(LogTag.ERROR_LINE, stackTraceElement.toString());

            LogUtil.error(log,logs);
            System.exit(0);

        }

        ApplicationEnvironmentContext.IS_RUNNING = true;

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

        properties.put("spring.main.log-startup-info",false);
        properties.put("spring.main.allow-circular-references",true);
        properties.put("spring.application.name", "maozi-cloud-${application-project-abbreviation}");
        properties.put("spring.cloud.nacos.config.file-extension", "yml");
        properties.put("spring.cloud.nacos.config.server-addr", "${NACOS_CONFIG_SERVER:maozi-cloud-nacos:8848}");
        properties.put("application-nacos-config-basics","cloud-nacos.yml,boot-monitor.yml,boot-arthas.yml,cloud-default.yml");

        properties.put("logging.level.root", "ERROR");
        properties.put("logging.level.com.maozi", "INFO");
        properties.put("logging.file.name","log/log.log");

        properties.compute("application-nacos-config-service", (k, serviceConfig) -> "cloud-nacos.yml,cloud-dubbo.yml,cloud-sentinel.yml,boot-monitor.yml,api-whitelist.yml,cloud-oauth.yml,boot-redis.yml,boot-swagger.yml,boot-lock.yml,boot-arthas.yml,cloud-default.yml" + (ObjectUtil.isNotNullEmpty(serviceConfig) ? "," + serviceConfig : ""));

    }

}
