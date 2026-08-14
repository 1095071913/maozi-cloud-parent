package com.maozi.opentelemetry.sampler.handle.filter.impl;

import com.maozi.opentelemetry.sampler.handle.filter.FilterHandle;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * HTTP Span 过滤器
 * <p>
 * 根据 Span 属性中的 {@code url.path} / {@code url.full} 匹配需要排除的 HTTP 路径
 * （如 {@code /actuator}、{@code /v3/api-docs/**}），命中则丢弃对应 Span，
 * 避免监控探活和文档接口产生噪声链路数据。
 * </p>
 *
 * @author pengjinlong
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HttpFilterHandle extends FilterHandle {

    /** 需要排除的 URL 路径模式列表（支持 ** 通配符） */
    private List<String> names = List.of(
            "/actuator",
            "/actuator/**",
            "/v3/api-docs/**"
    );

    /**
     * 根据 Span 属性中的请求路径判断是否过滤该 Span
     * <p>
     * 优先取 {@code url.path}，为空时回退到 {@code url.full}；
     * 完整 URL 会先剥离协议与域名部分并去除查询参数，再进行路径匹配。
     * </p>
     *
     * @param attributes Span 携带的属性集合
     * @return {@code true} 表示路径命中排除规则应丢弃；{@code false} 表示保留
     */
    @Override
    public Boolean filter(Attributes attributes) {

        // 优先取 url.path 属性
        String httpPath = attributes.get(InternalAttributeKeyImpl.create("url.path", AttributeType.STRING));
        if(Objects.isNull(httpPath)){
            // url.path 缺失时回退到 url.full
            httpPath = attributes.get(InternalAttributeKeyImpl.create("url.full", AttributeType.STRING));
        }

        // 无路径信息，不过滤
        if(Objects.isNull(httpPath)){
            return false;
        }

        // 完整 URL：剥离协议与域名，仅保留路径部分
        if(httpPath.contains("http://") || httpPath.contains("https://")){

            httpPath = httpPath.split("://")[1];

            // 定位域名后的首个 "/"，截取路径部分
            int index = httpPath.indexOf("/");

            if(index != -1){

                httpPath = httpPath.substring(index);

                // 去除查询参数
                httpPath = httpPath.split("\\?")[0];

            }

        }

        return matchPath(httpPath);

    }

    /**
     * 检查请求路径是否匹配需要排除的路径模式。
     * <p>
     * 将排除规则中的 {@code **} 通配符转为正则 {@code .*} 后进行匹配。
     * </p>
     *
     * @param path HTTP 请求路径
     * @return {@code true} 命中排除规则；{@code false} 未命中
     */
    public boolean matchPath(String path){

        for(String excludedPath : names){

            // 将 ** 通配符转为正则 .*，与请求路径整串匹配
            String regex = excludedPath.replace("**", ".*");
            if(Pattern.matches(regex, path)){
                return true;
            }

        }

        return false;

    }

}
