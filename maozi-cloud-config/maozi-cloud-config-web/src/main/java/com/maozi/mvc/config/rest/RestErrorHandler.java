package com.maozi.mvc.config.rest;

import com.maozi.common.CollectionUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.ResultUtil;
import com.maozi.common.result.error.code.ErrorCode;
import com.maozi.common.result.error.code.SystemErrorCode;
import com.maozi.mvc.config.error.ErrorParamTranslation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

/**
 * 全局 REST 异常处理器
 * <p>
 * 使用 {@code @RestControllerAdvice} 统一拦截 Controller 层抛出的各类异常，
 * 将异常转换为标准错误响应格式（通过 {@link ResultUtil#error}），
 * 避免将原始异常堆栈直接暴露给前端。
 * </p>
 * <p>
 * 处理的异常类型包括：
 * <ul>
 *   <li>404 接口不存在（NoHandlerFoundException）</li>
 *   <li>参数校验失败（MethodArgumentNotValidException）</li>
 *   <li>缺少请求参数（MissingServletRequestParameterException）</li>
 *   <li>请求体为空或不可读（HttpMessageNotReadableException）</li>
 *   <li>参数类型转换失败（MethodArgumentTypeMismatchException）</li>
 *   <li>权限不足（AccessDeniedException）—— 交由 Spring Security 处理</li>
 *   <li>全局兜底异常（Exception）</li>
 * </ul>
 * </p>
 *
 * @author maozi
 */
@Slf4j
@RestControllerAdvice
public class RestErrorHandler {

	/**
	 * 处理接口不存在异常（404）
	 * <p>
	 * 当请求的 URL 没有匹配到任何 Controller 处理方法时触发，
	 * 返回 404 状态码和"资源不存在"错误信息。
	 * </p>
	 *
	 * @param e NoHandlerFoundException 异常对象
	 * @return 标准错误响应，包含 NOT_RESOURCE_ERROR 错误码
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Object handle404(NoHandlerFoundException e) {
		return ResultUtil.error(SystemErrorCode.NOT_RESOURCE_ERROR);
	}

	/**
	 * 处理参数校验失败异常
	 * <p>
	 * 当使用 {@code @Valid} 或 {@code @Validated} 注解校验请求参数失败时触发，
	 * 收集所有字段校验错误信息（字段名 -> 错误消息），以 Map 形式返回。
	 * </p>
	 *
	 * @param e MethodArgumentNotValidException 参数校验异常
	 * @return 标准错误响应，包含 PARAM_ERROR 错误码和字段级别的错误详情映射
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Object handleValidException(MethodArgumentNotValidException e) {
		// 构建字段错误映射表，key 为字段名，value 为校验失败消息
		Map<String, String> errorMap = CollectionUtil.newHashMap();
		e.getFieldErrors().forEach(error ->
			errorMap.put(error.getField(), error.getDefaultMessage())
		);
		return ResultUtil.error(SystemErrorCode.PARAM_ERROR, errorMap);
	}

	/**
	 * 处理缺少请求参数异常
	 * <p>
	 * 当请求中缺少必须的查询参数时触发。会尝试从 {@link ErrorParamTranslation} 中
	 * 获取参数的中文翻译名称，拼接成更友好的错误提示信息。
	 * </p>
	 *
	 * @param e MissingServletRequestParameterException 缺少参数异常
	 * @return 标准错误响应，包含 PARAM_ERROR 错误码和"参数不能为空"提示信息
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Object handleMissingParam(MissingServletRequestParameterException e) {
		String paramName = e.getParameterName();
		// 优先使用翻译后的中文名称，若不存在则使用原始参数名
		String errorMsg = ObjectUtil.isNotNullEmpty(ErrorParamTranslation.errorParams.get(paramName)) ? ErrorParamTranslation.errorParams.get(paramName) + "不能为空" : paramName + "不能为空";

		return ResultUtil.error(SystemErrorCode.PARAM_ERROR).setMessage(errorMsg);
	}

	/**
	 * 处理请求体不可读异常
	 * <p>
	 * 当请求体为空、格式错误或无法解析时触发，返回参数错误响应。
	 * </p>
	 *
	 * @param e HttpMessageNotReadableException 请求体不可读异常
	 * @return 标准错误响应，包含 PARAM_ERROR 错误码
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Object handleAccessDeniedException(HttpMessageNotReadableException e) {
		return ResultUtil.error(SystemErrorCode.PARAM_ERROR);
	}

	/**
	 * 处理参数类型转换失败异常
	 * <p>
	 * 当请求参数的类型与目标方法参数类型不匹配时触发（如将字符串传入 Integer 参数），
	 * 返回参数错误响应。
	 * </p>
	 *
	 * @param e MethodArgumentTypeMismatchException 类型转换异常
	 * @return 标准错误响应，包含 PARAM_ERROR 错误码
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Object handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		return ResultUtil.error(SystemErrorCode.PARAM_ERROR);
	}

	/**
	 * 处理权限不足异常
	 * <p>
	 * 当 Spring Security 判定当前用户无权访问时抛出 AccessDeniedException，
	 * 此处不做处理，直接重新抛出，交由 Spring Security 的异常处理机制
	 * （如返回 403 或重定向到登录页）统一处理。
	 * </p>
	 *
	 * @param e AccessDeniedException 权限不足异常
	 * @return 不会返回，直接抛出异常
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public Object handleAccessDeniedException(AccessDeniedException e) {
		throw e;
	}

	/**
	 * 全局兜底异常处理
	 * <p>
	 * 捕获所有未被上述方法处理的异常，记录错误日志并返回 500 状态码。
	 * 这是一切异常的最终兜底，确保不会将原始异常堆栈暴露给客户端。
	 * </p>
	 *
	 * @param e 未被其他处理器捕获的异常
	 * @return 标准错误响应，包含 SYSTEM_ERROR 错误码
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Object handleGlobalException(Exception e) {
		ErrorCode errorCode = SystemErrorCode.SYSTEM_ERROR;
		// 记录完整的异常堆栈日志，方便排查问题
		log.error(errorCode.getMessage(), e);
		return ResultUtil.error(errorCode);
	}

}