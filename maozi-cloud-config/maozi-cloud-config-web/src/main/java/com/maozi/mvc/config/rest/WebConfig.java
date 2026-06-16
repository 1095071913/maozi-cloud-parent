package com.maozi.mvc.config.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maozi.mvc.config.json.ReadOnlyMultipartFormDataEndpointConverter;
import com.maozi.mvc.filter.ApplicationLinkContextAuthAfterFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Web MVC 配置
 * <p>
 * 注册应用链路上下文拦截器和自定义消息转换器。
 * 配置 {@link ApplicationLinkContextAuthAfterFilter} 拦截所有请求路径，
 * 并添加 {@link ReadOnlyMultipartFormDataEndpointConverter} 支持 multipart/form-data 请求体读取。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Jackson ObjectMapper 实例 */
    @Resource
    private ObjectMapper objectMapper;

    /** 应用链路上下文过滤器 */
    @Resource
    private ApplicationLinkContextAuthAfterFilter applicationLinkContextAuthAfterFilter;

    /**
     * 注册拦截器
     * <p>
     * 添加应用链路上下文过滤器，拦截所有路径，优先级设为最高。
     * </p>
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册链路上下文过滤器，拦截所有路径，并将优先级设为最高（确保最先执行）
        registry.addInterceptor(applicationLinkContextAuthAfterFilter).addPathPatterns("/**").order(Integer.MIN_VALUE);
    }

    /**
     * 扩展消息转换器列表
     * <p>
     * 添加支持 {@code multipart/form-data} 和 {@code application/octet-stream} 的
     * 只读 Jackson 消息转换器。
     * </p>
     *
     * @param converters 消息转换器列表
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        // 创建支持 multipart/form-data 的只读 Jackson 消息转换器
        ReadOnlyMultipartFormDataEndpointConverter converter = new ReadOnlyMultipartFormDataEndpointConverter(objectMapper);

        // 在默认支持的媒体类型基础上，额外添加 application/octet-stream 支持
        List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        converter.setSupportedMediaTypes(supportedMediaTypes);

        // 将自定义转换器添加到转换器列表中
        converters.add(converter);

    }

}
