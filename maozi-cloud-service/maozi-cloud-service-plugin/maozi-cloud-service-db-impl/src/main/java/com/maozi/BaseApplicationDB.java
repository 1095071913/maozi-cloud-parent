/*
 * Copyright 2012-2018 the original author or authors.
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
 *
 */

package com.maozi;

import com.maozi.common.context.ApplicationEnvironmentContext;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

/**
 * 数据库应用启动基类
 * <p>
 * 继承 {@link BaseApplication}，在基础启动配置上增加数据库相关配置。
 * 自动扫描项目 Mapper 接口，并追加数据源和数据库配置文件。
 * </p>
 *
 * @author maozi
 */

@EnableAsync
@SpringBootApplication
@MapperScan(ApplicationEnvironmentContext.PACKAGE_PREFIX + ".*.*.mapper")
public class BaseApplicationDB {

    /**
     * 应用启动入口方法
     * <p>
     * 追加数据库相关的 Nacos 配置文件后调用父类启动方法。
     * </p>
     *
     * @param args 命令行参数
     */
    protected static void ApplicationRun(String[] args) {

        Properties properties = System.getProperties();

        properties.put("application-nacos-config-service","boot-datasource.yml,boot-db.yml");

        BaseApplication.ApplicationRun(args);

    }

}
