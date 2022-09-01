package com.jiumao.mvc.config.rest;

import java.io.IOException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {

	@Bean
	// @SentinelRestTemplate(fallback = "fallback", fallbackClass =
	// BaseResultFactory.class,
	// blockHandler="handleException",blockHandlerClass=BaseResultFactory.class)
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		com.jiumao.mvc.config.rest.RestTemplate restTemplate = restTemplateBuilder.build(com.jiumao.mvc.config.rest.RestTemplate.class);
		restTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory());
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				if (response.getRawStatusCode() != 400) {
					super.handleError(response);
				}
			}
		});
		return restTemplate;
	}

	@Bean
	public AsyncRestTemplate asyncRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return new AsyncRestTemplate(new OkHttp3ClientHttpRequestFactory(), restTemplate(restTemplateBuilder));
	}
	
}
