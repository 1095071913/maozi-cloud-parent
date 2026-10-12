package com.maozi.service.config;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.maozi.common.JacksonUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.mvc.config.rest.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate HTTP 客户端配置
 * <p>
 * 通过 {@link org.springframework.boot.web.client.RestTemplateBuilder} 构建全局共用的 {@link RestTemplate} 实例，
 * 并自定义错误处理器：当响应状态码为 400 时不抛出异常，交由调用方自行处理响应内容，
 * 其余错误状态码仍按 Spring 默认行为处理。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class RestTemplateConfig {

	@Bean
	@SentinelRestTemplate(blockHandler = "handleException", blockHandlerClass = RestTemplateConfig.class)
	public RestTemplate restTemplate(org.springframework.boot.web.client.RestTemplateBuilder restTemplateBuilder) {
		return RestTemplateBuilder.buildRestTemplate(restTemplateBuilder);
	}

	public static ClientHttpResponse handleException(HttpRequest request, byte[] body, ClientHttpRequestExecution execution, BlockException exception) {
		ErrorCode errorCode = exception instanceof AuthorityException ?
				SystemErrorCode.BLACKLIST_ERROR
				:
				SystemErrorCode.CURRENT_LIMITING_ERROR;
		return new SentinelClientHttpResponse(JacksonUtil.objectToJson(ResultUtil.error(errorCode)));
	}

}