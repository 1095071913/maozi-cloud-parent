package com.maozi.gateway.config;

import com.maozi.common.JacksonUtil;
import com.maozi.common.LogUtil;
import com.maozi.common.ObjectUtil;
import com.maozi.common.context.ApplicationEnvironmentContext;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 服务端响应代理装饰器。
 * <p>
 * 继承 ServerHttpResponseDecorator，拦截响应体的写入过程，实现以下功能：
 * 1. 读取并缓存响应体内容；
 * 2. 如果响应体是自定义格式（包含 code 和 success 字段），则注入链路追踪 ID（TID）；
 * 3. 记录请求耗时（RT）和响应数据到日志；
 * 4. 根据响应状态码和业务码决定日志级别（ERROR 或 INFO）；
 * 5. 更新响应头中的 Content-Length。
 * </p>
 */
@Slf4j
public class ServerHttpResponseAgent extends ServerHttpResponseDecorator {

	/** 请求开始时间戳（毫秒），用于计算响应耗时 */
	private Long requestTime;

	/** Exchange 上下文属性，用于获取 TID 等信息 */
	private Map<String,Object> attributes;

	/** 日志信息收集 Map，由 RequestLogFilter 传入并在此处补充响应数据 */
	private Map<String,String> logs;

	/**
	 * 构造响应代理装饰器。
	 *
	 * @param requestTime 请求开始时间戳（毫秒）
	 * @param logs        日志信息收集 Map
	 * @param response    原始服务端响应对象
	 * @param attributes  Exchange 上下文属性 Map
	 */
	public ServerHttpResponseAgent(Long requestTime,Map<String,String> logs,ServerHttpResponse response,Map<String,Object> attributes) {

		super(response);

		this.requestTime=requestTime;

		this.logs=logs;

		this.attributes=attributes;

	}

	/**
	 * 拦截响应体写入。
	 * <p>
	 * 当响应体类型为 Flux 时，缓冲所有数据块并执行以下操作：
	 * 1. 合并所有 DataBuffer 为字节数组；
	 * 2. 解析 JSON 响应体，判断是否为自定义格式（含 code 和 success 字段）；
	 * 3. 如果是自定义格式，将 TID 注入到响应体的 id 字段；
	 * 4. 设置 MDC 上下文（TID 和服务名）；
	 * 5. 记录响应耗时和响应数据；
	 * 6. 根据响应状态决定日志级别；
	 * 7. 更新 Content-Length 并返回包装后的响应数据。
	 * </p>
	 *
	 * @param body 响应体数据流
	 * @return 写入完成的 Void Mono
	 */
	@Override
	public Mono<Void> writeWith(@Nonnull Publisher<? extends DataBuffer> body) {

		if (body instanceof Flux<? extends DataBuffer> fluxBody) {

			DataBufferFactory bufferFactory = getDelegate().bufferFactory();

            return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

				// 合并所有数据缓冲区的字节数据
	            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	            dataBuffers.forEach(i -> {

	              byte[] array = new byte[i.readableByteCount()];

	              i.read(array);

	              try {outputStream.write(array);} catch (IOException ignored) {}

	              // 释放 DataBuffer 资源，防止内存泄漏
	              DataBufferUtils.release(i);

	            });


				// 尝试将响应体解析为 Map，判断是否为自定义响应格式
	            Map result = JacksonUtil.jsonToObject(outputStream.toString(), Map.class);

				// 标记是否为自定义响应格式（包含 code 和 success 字段）
				boolean customResultBoo = ObjectUtil.isNotNullEmpty(result) && result.containsKey("code") && result.containsKey("success");

				// 如果是自定义响应格式，注入 TID 到响应体的 id 字段

                // 根据是否为自定义格式，选择序列化后的字节或原始字节
				byte[] resultByte = (customResultBoo ? Objects.requireNonNull(JacksonUtil.objectToJson(result)).getBytes() : outputStream.toByteArray());

				MDC.put("serviceName", ApplicationEnvironmentContext.SERVICE_NAME);

				// 记录响应耗时（RT）
				logs.put("RT", System.currentTimeMillis() - requestTime+" ms");
				// 记录响应数据
				logs.put("Data", new String(resultByte));

				// 根据响应状态和业务码决定日志级别
				if(Objects.requireNonNull(getDelegate().getStatusCode()).value() != 200) {
					// HTTP 状态码非 200，记录错误日志
					LogUtil.error(log,logs);
				}else if(ObjectUtil.isNotNullEmpty(result) && ObjectUtil.isNotNullEmpty(result.get("code")) && !"200".equals(result.get("code").toString())){
					// 业务码非 200，记录错误日志
					LogUtil.error(log,logs);
				}else{
					// 正常响应，记录信息日志
					LogUtil.info(log,logs);
				}

				// 更新响应头中的 Content-Length
				getDelegate().getHeaders().setContentLength(resultByte.length);

				// 包装结果字节并返回，同时在 finally 中关闭流和清理 MDC
				try {return bufferFactory.wrap(resultByte); } catch (Exception e) {return null;}finally {try {outputStream.close();} catch (IOException ignored) {}MDC.clear();}

	          }));

		}

		// 非 Flux 类型直接透传
		return super.writeWith(body);

	}

}
