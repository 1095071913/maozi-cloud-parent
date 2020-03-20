package com.zhongshi.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import io.netty.buffer.UnpooledByteBufAllocator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * 	功能说明：网关存储Post体
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-08-03 ：1:32:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */


@Component
public class CachePostBodyFilter implements GlobalFilter, Ordered {
 
    @Override 
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest serverHttpRequest = exchange.getRequest();
//        String method = serverHttpRequest.getMethodValue();
//        if("POST".equalsIgnoreCase(method)) {
//            ServerRequest serverRequest = new DefaultServerRequest(exchange);
//            Mono<String> bodyToMono = serverRequest.bodyToMono(String.class);
//            return bodyToMono.flatMap(body -> {
//                exchange.getAttributes().put("cachedRequestBody", body);
//                ServerHttpRequest newRequest = new ServerHttpRequestDecorator(serverHttpRequest) {
//                    @Override
//                    public HttpHeaders getHeaders() {
//                        HttpHeaders httpHeaders = new HttpHeaders();
//                        httpHeaders.putAll(super.getHeaders());
//                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
//                        return httpHeaders;
//                    }
// 
//                    @Override
//                    public Flux<DataBuffer> getBody() {
//                        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
//                        DataBuffer bodyDataBuffer = nettyDataBufferFactory.wrap(body.getBytes());
//                        return Flux.just(bodyDataBuffer);
//                    }
//                };
//                return chain.filter(exchange.mutate().request(newRequest).build());
//            });
//        }
        return chain.filter(exchange);
    }
 
    @Override
    public int getOrder() {
        return -21;
    }
}