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