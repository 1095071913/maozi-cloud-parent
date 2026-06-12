package com.maozi.mvc.config.rest;

import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationLinkContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Tags;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义 RestTemplate
 * <p>
 * 扩展 Spring 的 {@link org.springframework.web.client.RestTemplate}，
 * 在 {@link #doExecute} 方法中集成请求日志记录、响应时间统计、SkyWalking 链路追踪、
 * SQL 日志收集和异常处理。所有 HTTP 请求（GET/POST/HEAD/EXCHANGE）均通过
 * 重写方法统一走增强后的 {@link #doExecute} 流程。
 * </p>
 *
 * @author maozi
 */
@Slf4j
public class RestTemplate extends org.springframework.web.client.RestTemplate {

	/**
	 * 执行 HTTP 请求的核心方法（增强版）
	 * <p>
	 * 在原始执行逻辑基础上添加了请求日志、响应日志、异常日志、
	 * SkyWalking 链路标签和 SQL 日志收集。
	 * </p>
	 *
	 * @param url 请求 URL
	 * @param method HTTP 方法
	 * @param requestCallback 请求回调
	 * @param responseExtractor 响应提取器
	 * @return 响应数据
	 * @throws RestClientException REST 请求异常
	 */
	@Nullable
	@Override
	protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,@Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {

		// 标记请求是否发生异常，用于最终日志级别判断
		boolean error = false;

		// 记录请求开始时间，用于计算响应耗时
		long startTime = System.currentTimeMillis();

		// 使用有序 Map 收集请求全链路日志信息
		Map<String,String> logs = new LinkedHashMap<>();

		ClientHttpResponse response = null;

		try {

			// 校验 URL 和 HTTP 方法不能为空
			Assert.notNull(url, "URI is required");
			Assert.notNull(method, "HttpMethod is required");

			// 创建 HTTP 请求对象
			ClientHttpRequest request = createRequest(url, method);

			// 记录请求类型、URI 和 HTTP 方法到日志
            logs.put("Type", "RestTemplate");
            logs.put("URI", request.getURI().toString());
            logs.put("Method", request.getMethod().toString());

			// 执行请求回调，设置请求头和请求体
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}

			// 从请求输出流中读取请求参数内容，用于日志记录
			InputStream paramData = parse(request.getBody());
			String requestParam = new String(readStream(paramData));

			// 通过 SkyWalking 记录请求入参到链路追踪
			functionParam(requestParam);

			logs.put("Param", requestParam);

			// 执行 HTTP 请求并获取响应
			response = request.execute();

			// 处理响应状态码，非 2xx 时抛出异常
			handleResponse(url, method, response);

			// 使用响应提取器将响应体反序列化为目标类型
			T t=(responseExtractor != null ? responseExtractor.extractData(response) : null);

			// 记录响应状态码、响应耗时和响应数据到日志
			logs.put("Code", response.getStatusCode().value()+"");
        	logs.put("RT", (System.currentTimeMillis() - startTime) + " ms");
            logs.put("Data", t.toString());

			return t;
		}
		catch (Exception e) {

			// 标记为异常状态
			error = true;

			// 从 URL 中移除查询参数，仅保留资源路径用于日志记录
			String resource = url.toString();
			String query = url.getRawQuery();
			resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);

			log.error("",e);

			// 通过 SkyWalking 记录错误信息到链路追踪
			functionError(LogUtil.getStackTraceLog(e));

			// 记录异常相关的用户、描述和堆栈位置到日志
            logs.put("ErrorUser", ApplicationLinkContext.USERNAMES.get());
            logs.put("ErrorDesc", e.getLocalizedMessage());
            logs.put("ErrorLine", e.getStackTrace()[0].toString());

			return null;

		}finally {

			// 确保响应流被关闭，防止资源泄漏
			if (response != null) {
				response.close();
			}

			// 收集本次请求链路中累积的 SQL 日志
			StringBuilder sqlLog = LogUtil.sqlLog.get();
			if (ObjectUtil.isNotNullEmpty(sqlLog)) {
				logs.put("SQL", sqlLog.toString());
			}

			// 最终记录响应耗时（包含异常情况下的耗时）
			logs.put("RT", (System.currentTimeMillis() - startTime) + " ms");

			// 根据是否有异常，以对应日志级别输出完整的请求日志
			LogUtil.log(log,error,logs);

		}

	}



	/**
	 * 发送 GET 请求并返回包含响应体的 ResponseEntity（可变参数形式）
	 *
	 * @param url 请求 URL，支持占位符
	 * @param responseType 响应体目标类型
	 * @param uriVariables URL 中占位符对应的变量值
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 发送 GET 请求并返回包含响应体的 ResponseEntity（Map 形式 URI 变量）
	 *
	 * @param url 请求 URL，支持命名占位符
	 * @param responseType 响应体目标类型
	 * @param uriVariables URL 中命名占位符对应的键值对
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 发送 GET 请求并返回包含响应体的 ResponseEntity（URI 形式）
	 *
	 * @param url 完整的请求 URI
	 * @param responseType 响应体目标类型
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
	}


	// ==================== HEAD 请求 ====================

	/**
	 * 发送 HEAD 请求并获取响应头（可变参数形式）
	 *
	 * @param url 请求 URL
	 * @param uriVariables URL 占位符变量值
	 * @return 响应头信息
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor(), uriVariables);
	}

	/**
	 * 发送 HEAD 请求并获取响应头（Map 形式 URI 变量）
	 *
	 * @param url 请求 URL
	 * @param uriVariables URL 命名占位符键值对
	 * @return 响应头信息
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor(), uriVariables);
	}

	/**
	 * 发送 HEAD 请求并获取响应头（URI 形式）
	 *
	 * @param url 完整的请求 URI
	 * @return 响应头信息
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public HttpHeaders headForHeaders(URI url) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor());
	}

	/**
	 * 发送 POST 请求并返回包含响应体的 ResponseEntity（可变参数形式）
	 *
	 * @param url 请求 URL
	 * @param request 请求体对象，可为 null
	 * @param responseType 响应体目标类型
	 * @param uriVariables URL 占位符变量值
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request,
			Class<T> responseType, Object... uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 发送 POST 请求并返回包含响应体的 ResponseEntity（Map 形式 URI 变量）
	 *
	 * @param url 请求 URL
	 * @param request 请求体对象，可为 null
	 * @param responseType 响应体目标类型
	 * @param uriVariables URL 命名占位符键值对
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 发送 POST 请求并返回包含响应体的 ResponseEntity（URI 形式）
	 *
	 * @param url 完整的请求 URI
	 * @param request 请求体对象，可为 null
	 * @param responseType 响应体目标类型
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}

	// ==================== EXCHANGE 请求 ====================

	/**
	 * 使用指定的 HTTP 方法发送请求（可变参数形式）
	 *
	 * @param url 请求 URL
	 * @param method HTTP 方法（GET/POST/PUT/DELETE 等）
	 * @param requestEntity 请求实体（包含请求头和请求体），可为 null
	 * @param responseType 响应体目标类型
	 * @param uriVariables URL 占位符变量值
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			@Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 使用指定的 HTTP 方法发送请求（Map 形式 URI 变量）
	 *
	 * @param url 请求 URL
	 * @param method HTTP 方法
	 * @param requestEntity 请求实体，可为 null
	 * @param responseType 响应体目标类型
	 * @param uriVariables URL 命名占位符键值对
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			@Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 使用指定的 HTTP 方法发送请求（URI 形式）
	 *
	 * @param url 完整的请求 URI
	 * @param method HTTP 方法
	 * @param requestEntity 请求实体，可为 null
	 * @param responseType 响应体目标类型
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor);
	}

	/**
	 * 使用参数化类型引用发送请求（可变参数形式），支持泛型响应类型
	 *
	 * @param url 请求 URL
	 * @param method HTTP 方法
	 * @param requestEntity 请求实体，可为 null
	 * @param responseType 参数化响应类型引用，支持泛型
	 * @param uriVariables URL 占位符变量值
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 使用参数化类型引用发送请求（Map 形式 URI 变量），支持泛型响应类型
	 *
	 * @param url 请求 URL
	 * @param method HTTP 方法
	 * @param requestEntity 请求实体，可为 null
	 * @param responseType 参数化响应类型引用，支持泛型
	 * @param uriVariables URL 命名占位符键值对
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	/**
	 * 使用参数化类型引用发送请求（URI 形式），支持泛型响应类型
	 *
	 * @param url 完整的请求 URI
	 * @param method HTTP 方法
	 * @param requestEntity 请求实体，可为 null
	 * @param responseType 参数化响应类型引用，支持泛型
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor);
	}

	/**
	 * 使用 RequestEntity 发送请求，直接调用增强的 {@link #doExecute} 方法
	 *
	 * @param requestEntity 请求实体（包含 URL、方法、请求头和请求体）
	 * @param responseType 响应体目标类型
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor);
	}

	/**
	 * 使用 RequestEntity 和参数化类型引用发送请求，直接调用增强的 {@link #doExecute} 方法
	 *
	 * @param requestEntity 请求实体（包含 URL、方法、请求头和请求体）
	 * @param responseType 参数化响应类型引用，支持泛型
	 * @return 包含响应体的 ResponseEntity
	 * @throws RestClientException REST 请求异常
	 */
	@Override
	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
			throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor);
	}

	/**
	 * 将输出流转换为输入流（用于读取请求体内容）
	 * <p>
	 * 将 ByteArrayOutputStream 中的字节数据包装为 ByteArrayInputStream，
	 * 以便在日志中记录请求参数。
	 * </p>
	 *
	 * @param out 输出流（实际类型为 ByteArrayOutputStream）
	 * @return 包含相同字节数据的输入流
	 * @throws Exception 流转换异常
	 */
	public ByteArrayInputStream parse(final OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        final ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }

	/**
	 * 读取输入流中的全部字节数据
	 * <p>
	 * 使用 1024 字节的缓冲区逐块读取输入流，直到流结束，
	 * 将所有数据合并为字节数组返回。用于从请求体流中提取参数内容。
	 * </p>
	 *
	 * @param inStream 输入流
	 * @return 包含流中全部数据的字节数组
	 * @throws Exception 流读取异常
	 */
	public byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024]; // 1KB 缓冲区
		int len = -1;
		// 循环读取直到流结束（read 返回 -1）
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	/**
	 * SkyWalking 链路追踪：记录错误信息标签
	 *
	 * @param errorMessage 错误信息
	 */
	@Trace
    @Tags({@Tag(key = "错误值", value = "arg[0]")})
    public void functionError(Object errorMessage) {}

	/**
	 * SkyWalking 链路追踪：记录请求入参标签
	 *
	 * @param param 请求参数
	 */
	@Trace(operationName = "入参值")
    @Tags({@Tag(key = "入参值", value = "arg[0]")})
    public void functionParam(Object param) { }

}
