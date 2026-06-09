package com.maozi.monitor.config.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.maozi.common.ResultUtil;
import com.maozi.common.WebUtil;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Sentinel 流控拦截处理器
 * <p>
 * 实现 Sentinel 的 {@link BlockExceptionHandler} 接口，
 * 当请求被 Sentinel 流控规则拦截时，返回限流错误的统一响应。
 * 使用 {@link SystemErrorCode#CURRENT_LIMITING_ERROR} 作为错误码。
 * </p>
 *
 * @author maozi
 */
public class IUrlBlockHandler implements BlockExceptionHandler {

	/**
	 * 处理被 Sentinel 拦截的请求，返回限流错误响应
	 *
	 * @param request HTTP 请求
	 * @param response HTTP 响应
	 * @param e Sentinel 阻塞异常
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) {
		ErrorCode errorCode = SystemErrorCode.CURRENT_LIMITING_ERROR;
		WebUtil.writeResponseBody(response, ResultUtil.error(errorCode).autoIdentifyHttpCode(errorCode.getCode()));
	}

}
