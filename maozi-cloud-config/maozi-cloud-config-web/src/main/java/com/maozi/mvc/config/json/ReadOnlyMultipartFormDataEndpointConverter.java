package com.maozi.mvc.config.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Type;

/**
 * 只读 Multipart 表单数据转换器
 * <p>
 * 扩展 {@link MappingJackson2HttpMessageConverter}，仅当控制器方法
 * 的 {@code @RequestMapping} 注解明确声明消费 {@code multipart/form-data} 时，
 * 才使用 Jackson 将请求体反序列化为对象。仅支持读取，不支持写入。
 * </p>
 *
 * @author maozi
 */
public class ReadOnlyMultipartFormDataEndpointConverter extends MappingJackson2HttpMessageConverter {

    /**
     * 构造方法
     *
     * @param objectMapper Jackson ObjectMapper 实例
     */
    public ReadOnlyMultipartFormDataEndpointConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    /**
     * 判断是否可以读取指定的媒体类型
     * <p>
     * 仅在以下条件全部满足时返回 true：
     * <ol>
     *   <li>当前存在 HTTP 请求上下文</li>
     *   <li>能获取到处理方法信息</li>
     *   <li>处理方法标注了 {@code @RequestMapping} 注解</li>
     *   <li>{@code @RequestMapping} 的 consumes 仅声明了 multipart/form-data</li>
     * </ol>
     * </p>
     *
     * @param type 目标类型
     * @param contextClass 上下文类
     * @param mediaType 媒体类型
     * @return 是否可以读取
     */
    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        // When a rest client(e.g. RestTemplate#getForObject) reads a request, 'RequestAttributes' can be null.
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) requestAttributes.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (handlerMethod == null) {
            return false;
        }
        RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return false;
        }
        // This converter reads data only when the mapped controller method consumes just 'MediaType.MULTIPART_FORM_DATA_VALUE'.
        if (requestMapping.consumes().length != 1|| !MediaType.MULTIPART_FORM_DATA_VALUE.equals(requestMapping.consumes()[0])) {
            return false;
        }
        return super.canRead(type, contextClass, mediaType);
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        // 此转换器仅用于请求读取，不支持写入响应
        return false;
    }
}
