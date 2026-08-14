package com.maozi.gateway.utils;

import com.maozi.common.ObjectUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Objects;

/**
 * Web 工具类。
 * <p>
 * 提供从 HTTP 请求中获取客户端真实 IP 地址的能力，
 * 支持多种反向代理场景（Nginx、Apache、Squid 等）。
 * </p>
 *
 * @author maozi
 */
public class WebUtil {

	/**
	 * 获取客户端真实 IP 地址。
	 * <p>
	 * 按优先级依次从以下 HTTP 头中获取客户端 IP：
	 * 1. X-Forwarded-For —— 最常用的代理头（Nginx 等）；
	 * 2. Proxy-Client-IP —— Apache 服务器代理头；
	 * 3. WL-Proxy-Client-IP —— WebLogic 代理头；
	 * 4. HTTP_CLIENT_IP —— 部分代理服务器使用；
	 * 5. HTTP_X_FORWARDED_FOR —— 部分代理服务器使用；
	 * 6. X-Real-IP —— Nginx 代理头；
	 * 7. 如果以上头均无效，则从远程地址直接获取。
	 * </p>
	 * <p>
	 * 处理逻辑：
	 * - 对于 X-Forwarded-For 中的多 IP 格式（客户端IP, 代理1, 代理2...），取第一个 IP；
	 * - 将 IPv6 本地回环地址（0:0:0:0:0:0:0:1）转换为 IPv4 的 127.0.0.1。
	 * </p>
	 *
	 * @param request 服务端 HTTP 请求对象
	 * @return 客户端真实 IP 地址
	 */
	public static String getRequestHost(ServerHttpRequest request) {

		HttpHeaders headers = request.getHeaders();

		// 优先从反向代理 header 中获取
		String requestHost = headers.getFirst("x-forwarded-for");

		// 依次检查常用代理 IP 头
		if (isBlankOrUnknown(requestHost)) {
			requestHost = headers.getFirst("Proxy-Client-IP");
		}
		if (isBlankOrUnknown(requestHost)) {
			requestHost = headers.getFirst("WL-Proxy-Client-IP");
		}
		if (isBlankOrUnknown(requestHost)) {
			requestHost = headers.getFirst("HTTP_CLIENT_IP");
		}
		if (isBlankOrUnknown(requestHost)) {
			requestHost = headers.getFirst("HTTP_X_FORWARDED_FOR");
		}

		if (isBlankOrUnknown(requestHost)) {
			requestHost = headers.getFirst("X-Real-IP");
		}

		// 兜底：直接获取远程地址
		if (isBlankOrUnknown(requestHost)) {
			requestHost = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
		}

		// 处理多 IP 情况（x-forwarded-for 会返回 客户端IP,代理1,代理2...）
		if (requestHost != null && requestHost.contains(",")) {
			// 取第一个非空的真实 IP
			requestHost = requestHost.split(",")[0].trim();
		}

		// 处理 IPv6 本地回环地址，转换为 IPv4 格式
		if (com.maozi.common.WebUtil.LOCAL_LOOPBACK_IP.equals(requestHost)) {
			requestHost = com.maozi.common.WebUtil.LOCAL_IP;
		}

		return requestHost;

	}

	/**
	 * 统一判断：IP 是否为 null、空字符串、unknown（忽略大小写）。
	 *
	 * @param ip 待检查的 IP 字符串
	 * @return 如果 IP 为空、空白或 "unknown" 则返回 true
	 */
	private static boolean isBlankOrUnknown(String ip) {
		return ObjectUtil.isNullEmpty(ip) || ip.isBlank() || "unknown".equalsIgnoreCase(ip);
	}

}
