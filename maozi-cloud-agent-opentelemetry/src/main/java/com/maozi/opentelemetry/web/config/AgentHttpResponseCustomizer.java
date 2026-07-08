package com.maozi.opentelemetry.web.config;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseCustomizer;
import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseMutator;
import io.opentelemetry.javaagent.shaded.io.opentelemetry.api.trace.Span;
import io.opentelemetry.javaagent.shaded.io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.javaagent.shaded.io.opentelemetry.context.Context;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * HTTP 响应追踪 ID 注入定制器
 * <p>
 * 通过 OpenTelemetry SPI（{@link AutoService}）自动注册，在网关响应中注入
 * {@code X-TraceId} 响应头，便于客户端通过响应头获取链路追踪 ID。
 * 仅对 Netty 的 {@code DefaultHttpResponse} / {@code DefaultFullHttpResponse}
 * 类型生效（即网关转发场景），Service 层的 Undertow 响应不在此处理范围。
 * </p>
 *
 * @author pengjinlong
 */
@AutoService(HttpServerResponseCustomizer.class)
public class AgentHttpResponseCustomizer implements HttpServerResponseCustomizer {

    private static final String TRACE_ID_HEADER_KEY = "X-TraceId";

    private final List<String> ADD_TRACEID_CLASS = Collections.unmodifiableList(
        Arrays.asList("io.netty.handler.codec.http.DefaultHttpResponse","io.netty.handler.codec.http.DefaultFullHttpResponse")
    );

    @Override
    public <RESPONSE> void customize(Context context, RESPONSE response, HttpServerResponseMutator<RESPONSE> responseMutator) {

        SpanContext spanContext = Span.fromContext(context).getSpanContext();

        //Gateway未找到服务转发 = io.netty.handler.codec.http.DefaultFullHttpResponse
        //Gateway找到服务转发 = io.netty.handler.codec.http.DefaultHttpResponse
        //Service = io.undertow.server.HttpServerExchange
        if(ADD_TRACEID_CLASS.contains(response.getClass().getName())){
            responseMutator.appendHeader(response, TRACE_ID_HEADER_KEY, spanContext.getTraceId());
        }

    }

}