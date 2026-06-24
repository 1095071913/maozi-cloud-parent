package com.maozi.feign.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

/**
 * Feign 客户端启用配置
 * <p>
 * 通过 {@link EnableFeignClients} 开启 Feign 客户端能力，
 * 自动扫描 Spring 容器中所有 {@code @FeignClient} 接口并注册为 Bean。
 * </p>
 *
 * @author pengjinlong
 * @since 2026/6/12 18:13
 */
@Component
@EnableFeignClients
public class FeignConfig { }
