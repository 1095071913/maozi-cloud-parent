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

/**
 * 数据库应用启动基类
 * <p>
 * 继承 {@link BaseApplication}，在基础启动配置上增加数据库相关配置。
 * 自动扫描项目 Mapper 接口，并迭代加载 classpath 下 {@code run/config/} 目录中的所有配置文件到系统属性。
 * </p>
 *
 * @author maozi
 */

@EnableAsync
@SpringBootApplication
@MapperScan(ApplicationEnvironmentContext.PACKAGE_PREFIX + ".${application-project-abbreviation}.*.mapper")
public class BaseApplicationDB {

    /**
     * 应用启动入口方法
     * @param args 命令行参数
     */
    protected static void ApplicationRun(String[] args) {
        BaseApplication.ApplicationRun(args);
    }

}
