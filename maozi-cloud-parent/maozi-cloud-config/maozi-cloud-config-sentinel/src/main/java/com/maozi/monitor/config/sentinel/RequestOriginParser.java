package com.maozi.monitor.config.sentinel;

import com.maozi.common.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 请求来源解析器
 * <p>
 * 实现 Sentinel 的 {@link com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser} 接口，
 * 从 HTTP 请求中解析来源标识（主机地址），用于授权规则的来源判断。
 * </p>
 *
 * @author maozi
 */
@Component
public class RequestOriginParser implements com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser {

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
