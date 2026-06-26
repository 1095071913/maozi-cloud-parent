/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.spring.boot.context.event;

import jakarta.annotation.Nonnull;
import org.apache.dubbo.common.Version;


import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_GITHUB_URL;
import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_MAILING_LIST;
import static org.apache.dubbo.spring.boot.util.DubboUtils.LINE_SEPARATOR;

/**
 * Dubbo 欢迎日志监听器
 * <p>
 * 监听 Spring Boot 的 {@link ApplicationEnvironmentPreparedEvent} 事件，
 * 原本用于在应用启动时打印 Dubbo 的版本信息和相关链接（GitHub 地址、邮件讨论组），
 * 并通过 {@link AtomicBoolean} 保证 Banner 只输出一次，防止在层级 ApplicationContext 中重复执行。
 * </p>
 * <p>
 * <b>当前实际行为：</b>字段 {@code processed} 的初始值为 {@code true}，
 * 使得 {@code onApplicationEvent} 在入口处即 {@code return}，<b>Banner 实际不会被输出</b>。
 * 这与原 Dubbo 源码「初始值 {@code false} + compareAndSet 保证只输出一次」的语义不一致，
 * 推测为复制源码后引入的回归；如需恢复 Banner 输出，可将 {@code processed} 初始值改回 {@code false}。
 * </p>
 * <p>
 * 执行顺序设置为 {@link Ordered#HIGHEST_PRECEDENCE} + 21，确保在
 * {@link LoggingApplicationListener} 完成日志系统初始化之后执行，
 * 以便能够正确获取 Logger 实例。
 * </p>
 *
 * @see ApplicationListener
 * @see ApplicationEnvironmentPreparedEvent
 * @since 2.7.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20 + 1)  // 在 LoggingApplicationListener#DEFAULT_ORDER 之后执行
public class WelcomeLogoApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    /**
     * 标识 Banner 是否已经输出过
     * <p>
     * 当前初始值为 {@code true}，配合 {@code onApplicationEvent} 开头的
     * {@code if (processed.get()) return;} 直接跳过，会导致 Banner 永不输出。
     * 同时末尾的 {@code compareAndSet(false, true)} 期望从 {@code false} 改为 {@code true}，
     * 与初始值 {@code true} 语义冲突，疑似复制 Dubbo 源码后引入的回归。
     * </p>
     * <p>
     * <b>待确认：</b> 是否需要将初始值改为 {@code false} 以恢复「只输出一次 Banner」的预期行为，
     * 还是刻意保留 {@code true} 以静默 Dubbo 启动 Banner。
     * </p>
     */
    private static final AtomicBoolean processed = new AtomicBoolean(true);

    /**
     * 处理应用环境准备完成事件
     * <p>
     * 当 Spring Boot 应用环境准备完成时触发此方法。主要逻辑：
     * <ol>
     *   <li>检查是否已经处理过，若已处理则直接跳过，防止重复输出</li>
     *   <li>获取 Logger 实例（此时日志系统已初始化完毕）</li>
     *   <li>构建并输出 Dubbo Banner 文本</li>
     *   <li>标记为已处理状态</li>
     * </ol>
     * </p>
     *
     * @param event Spring Boot 应用环境准备完成事件
     */
    @Override
    public void onApplicationEvent(@Nonnull ApplicationEnvironmentPreparedEvent event) {

        // 当前 processed 初始值为 true，此处恒为真直接返回，Banner 实际不会输出（见字段注释中的待确认说明）
        if (processed.get()) {
            return;
        }

        /*
          在日志系统配置就绪后获取 Logger
          @see LoggingApplicationListener
         */
        final Logger logger = LoggerFactory.getLogger(getClass());

        // 构建包含版本号和链接信息的 Banner 文本
        String bannerText = buildBannerText();

        // 优先使用日志系统输出，若 info 级别未启用则降级到控制台输出
        if (logger.isInfoEnabled()) {
            logger.info(bannerText);
        } else {
            System.out.print(bannerText);
        }

        // 期望从 false 改为 true；但因初始值为 true，此处永远不会成功（见字段注释中的待确认说明）
        processed.compareAndSet(false, true);
    }

    /**
     * 构建 Dubbo Banner 文本
     * <p>
     * 生成包含以下信息的 Banner 文本：
     * <ul>
     *   <li>Dubbo 框架版本号</li>
     *   <li>Dubbo GitHub 项目地址</li>
     *   <li>Dubbo 邮件讨论组地址</li>
     * </ul>
     * </p>
     *
     * @return 格式化后的 Banner 文本字符串
     */
    String buildBannerText() {

        return LINE_SEPARATOR +
                LINE_SEPARATOR +
                " :: Dubbo (v" + Version.getVersion() + ") : " +
                DUBBO_GITHUB_URL +
                LINE_SEPARATOR +
                " :: Discuss group : " +
                DUBBO_MAILING_LIST +
                LINE_SEPARATOR;

    }

}