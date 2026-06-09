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

		boolean error = false;

		long startTime = System.currentTimeMillis();

		Map<String,String> logs = new LinkedHashMap<>();

		ClientHttpResponse response = null;

		try {

			Assert.notNull(url, "URI is required");

			Assert.notNull(method, "HttpMethod is required");

			ClientHttpRequest request = createRequest(url, method);

            logs.put("Type", "RestTemplate");

            logs.put("URI", request.getURI().toString());

            logs.put("Method", request.getMethod().toString());

			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}

			InputStream paramData = parse(request.getBody());

			String requestParam = new String(readStream(paramData));

			functionParam(requestParam);

			logs.put("Param", requestParam);

			response = request.execute();

			handleResponse(url, method, response);

			T t=(responseExtractor != null ? responseExtractor.extractData(response) : null);

			logs.put("Code", response.getStatusCode().value()+"");

        	logs.put("RT", (System.currentTimeMillis() - startTime) + " ms");

            logs.put("Data", t.toString());

			return t;
		}
		catch (Exception e) {

			error = true;

			String resource = url.toString();

			String query = url.getRawQuery();

			resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);

			log.error("",e);

			functionError(LogUtil.getStackTraceLog(e));

            logs.put("ErrorUser", ApplicationLinkContext.USERNAMES.get());
            logs.put("ErrorDesc", e.getLocalizedMessage());
            logs.put("ErrorLine", e.getStackTrace()[0].toString());

			return null;

		}finally {

			if (response != null) {
				response.close();
			}

			StringBuilder sqlLog = LogUtil.sqlLog.get();
			if (ObjectUtil.isNotNullEmpty(sqlLog)) {
				logs.put("SQL", sqlLog.toString());
			}

			logs.put("RT", (System.currentTimeMillis() - startTime) + " ms");

			LogUtil.log(log,error,logs);

		}

	}



	@Override
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
	}


	// HEAD

	@Override
	public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor(), uriVariables);
	}

	@Override
	public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor(), uriVariables);
	}

	@Override
	public HttpHeaders headForHeaders(URI url) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor());
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request,
			Class<T> responseType, Object... uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(String url, @Nullable Object request,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(URI url, @Nullable Object request, Class<T> responseType)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			@Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			@Nullable HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor);
	}

	@Override
	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
			throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return doExecute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor);
	}

	/**
	 * 将输出流转换为输入流
	 *
	 * @param out 输出流
	 * @return 字节数组输入流
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
	 *
	 * @param inStream 输入流
	 * @return 字节数组
	 * @throws Exception 流读取异常
	 */
	public byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
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
