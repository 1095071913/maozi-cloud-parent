package com.maozi.mvc.config.rest;

import jakarta.annotation.Nonnull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

/**
 * RestTemplate HTTP 客户端配置
 * <p>
 * 通过 {@link RestTemplateBuilder} 构建全局共用的 {@link RestTemplate} 实例，
 * 并自定义错误处理器：当响应状态码为 400 时不抛出异常，交由调用方自行处理响应内容，
 * 其余错误状态码仍按 Spring 默认行为处理。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class RestTemplateConfig {

	/**
	 * 创建全局 RestTemplate 实例
	 * <p>
	 * 错误处理器对 400 状态码不抛异常，其余错误状态码（4xx/5xx）抛出
	 * {@link org.springframework.web.client.RestClientResponseException}。
	 * </p>
	 * <p>
	 * 未导入 {@code org.springframework.web.client.RestTemplate}，简单名 {@code RestTemplate}
	 * 指向同包下的日志增强版 {@link RestTemplate}，因此构建并注册的是增强版实例。
	 * </p>
	 *
	 * @param restTemplateBuilder RestTemplate 构建器
	 * @return 配置好的 RestTemplate 实例
	 */
	@Bean
	// @SentinelRestTemplate(fallback = "fallback", fallbackClass =
	// BaseResultFactory.class,
	// blockHandler="handleException",blockHandlerClass=BaseResultFactory.class)
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		RestTemplate restTemplate = restTemplateBuilder.build(RestTemplate.class);
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

			/**
			 * 重写错误处理逻辑，400 状态码不抛出异常
			 *
			 * @param response HTTP 客户端响应
			 * @throws IOException 读取响应体失败时抛出
			 */
			@Override
			public void handleError(@Nonnull ClientHttpResponse response) throws IOException {
				if (response.getStatusCode().value() != 400) {
					super.handleError(response);
				}
			}
		});
		return restTemplate;
	}

}