package com.maozi.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author pengjinlong
 * @date 2026/4/24 14:11
 */
@Slf4j
public class WebUtil {

    /**
     * 获取当前请求
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isNullEmpty(servletRequestAttributes)) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取当前响应
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isNullEmpty(servletRequestAttributes)) {
            return null;
        }
        return servletRequestAttributes.getResponse();
    }


    /**
     * 写请求体
     */
    public static void writeResponseBody(HttpServletResponse response,Object data){

        if(ObjectUtil.isNullEmpty(response) || ObjectUtil.isNullEmpty(data)){
            return;
        }

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        try{JacksonUtil.getObjectMapper().writeValue(response.getOutputStream(),data);}catch (Exception e){
            log.error(e.getLocalizedMessage());
        }

    }

    /**
     * 获取请求真实地址
     */
    public static String getRequestHost(HttpServletRequest request) {
        // 优先从反向代理header中获取
        String ip = request.getHeader("x-forwarded-for");

        // 依次检查常用代理IP头
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isBlankOrUnknown(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        // 兜底：直接获取远程地址
        if (isBlankOrUnknown(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多IP情况（x-forwarded-for 会返回 客户端IP,代理1,代理2...）
        if (ip != null && ip.contains(",")) {
            // 取第一个非空的真实IP
            ip = ip.split(",")[0].trim();
        }

        // 处理 IPv6 本地回环地址
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * 统一判断：IP是否为 null、空字符串、unknown（忽略大小写）
     */
    private static boolean isBlankOrUnknown(String ip) {
        return ObjectUtil.isNullEmpty(ip) || ip.isBlank() || "unknown".equalsIgnoreCase(ip);
    }

}
