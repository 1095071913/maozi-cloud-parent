/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.mountain.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.collect.Maps;
import com.mountain.factory.BaseResultFactory;
import com.mountain.factory.result.AbstractBaseResult;

import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 
 * 	功能说明：网关错误映射器
 * 
 *	功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 *
 *	创建日期：2019-10-31 ：20:16:00
 *
 *	版权归属：蓝河团队
 *
 *	协议说明：Apache2.0（ 文件顶端 ）
 *
 */



public class JsonExceptionHandler extends BaseResultFactory implements ErrorWebExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(JsonExceptionHandler.class);
	private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();
	private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();
	private List<ViewResolver> viewResolvers = Collections.emptyList();
	private ThreadLocal<AbstractBaseResult> exceptionHandlerResult = new ThreadLocal<>();

	public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
		Assert.notNull(messageReaders, "'messageReaders' must not be null");
		this.messageReaders = messageReaders;
	}

	public void setViewResolvers(List<ViewResolver> viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

	public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
		Assert.notNull(messageWriters, "'messageWriters' must not be null");
		this.messageWriters = messageWriters;
	}

	protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		AbstractBaseResult result = exceptionHandlerResult.get();
		return ServerResponse.status(result.getCode()).contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(BodyInserters.fromObject(result));
	}

	private Mono<? extends Void> write(ServerWebExchange exchange, ServerResponse response) {
		exchange.getResponse().getHeaders().setContentType(response.headers().getContentType());
		return response.writeTo(exchange, new ResponseContext());
	}

	private class ResponseContext implements ServerResponse.Context {
		@Override
		public List<HttpMessageWriter<?>> messageWriters() {
			return JsonExceptionHandler.this.messageWriters;
		}

		@Override
		public List<ViewResolver> viewResolvers() {
			return JsonExceptionHandler.this.viewResolvers;
		}
	}

	@Override
	public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
		AbstractBaseResult result =null;
		if (throwable instanceof NotFoundException) {
			result=error(code(404)); 
		} else if (throwable instanceof ResponseStatusException) {
			ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
			result=error(code(1).setMessage(responseStatusException.getMessage()));
		} else {
			result=error(code(500));
		}
		ServerHttpRequest request = serverWebExchange.getRequest();
		log.error("[全局系统异常]\r\n请求路径：{}\r\n异常记录：{}", request.getPath(), throwable.getMessage());
		if (serverWebExchange.getResponse().isCommitted()) {
			return Mono.error(throwable);
		}
		exceptionHandlerResult.set(result);
		ServerRequest newRequest = ServerRequest.create(serverWebExchange, this.messageReaders);
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse).route(newRequest)
				.switchIfEmpty(Mono.error(throwable)).flatMap((handler) -> handler.handle(newRequest))
				.flatMap((response) -> write(serverWebExchange, response));
	}
}