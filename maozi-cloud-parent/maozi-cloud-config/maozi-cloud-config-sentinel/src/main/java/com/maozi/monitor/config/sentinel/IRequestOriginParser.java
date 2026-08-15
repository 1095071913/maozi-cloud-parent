package com.maozi.monitor.config.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.maozi.common.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 请求来源解析器
 * <p>
 * 实现 Sentinel 的 {@link RequestOriginParser} 接口，
 * 从 HTTP 请求中解析来源标识（主机地址），用于授权规则的来源判断。
 * </p>
 *
 * @author maozi
 */
@Component
public class IRequestOriginParser implements RequestOriginParser {

	/**
	 * 解析请求来源
	 *
	 * @param request HTTP 请求
	 * @return 请求来源标识（主机地址）
	 */
	@Override
	public String parseOrigin(HttpServletRequest request) {
		return WebUtil.getRequestHost(request);
	}

}
