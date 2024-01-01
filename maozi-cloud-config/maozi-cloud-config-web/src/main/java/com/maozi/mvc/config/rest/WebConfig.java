package com.maozi.mvc.config.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maozi.mvc.config.json.ReadOnlyMultipartFormDataEndpointConverter;
import com.maozi.mvc.filter.ApplicationLinkContextFilter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private ApplicationLinkContextFilter applicationLinkContextFilter;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(applicationLinkContextFilter).addPathPatterns("/**").order(Integer.MIN_VALUE);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

		ReadOnlyMultipartFormDataEndpointConverter converter = new ReadOnlyMultipartFormDataEndpointConverter(objectMapper);

		List<MediaType> supportedMediaTypes = new ArrayList<>();

		supportedMediaTypes.addAll(converter.getSupportedMediaTypes());

		supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);

		converter.setSupportedMediaTypes(supportedMediaTypes);

		converters.add(converter);

	}

}