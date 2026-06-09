
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

package com.maozi.mvc.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置
 * <p>
 * 配置应用级异步任务线程池，用于 {@code @Async} 注解标注的异步方法执行。
 * 线程池参数：核心线程数 16、最大线程数 256、空闲存活时间 10 秒、队列容量 1024。
 * 拒绝策略为 CallerRunsPolicy（由调用线程执行），确保任务不丢失。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class AsyncConfig {

	/** 核心线程数 */
	private static final int CORE_POOL_SIZE = 16;

	/** 最大线程数 */
	private static final int MAX_POOL_SIZE = 256;

	/** 允许线程空闲时间（单位为秒） */
	private static final int KEEP_ALIVE_TIME = 10;

	/** 缓冲队列数 */
	private static final int QUEUE_CAPACITY = 1024;

	/** 线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁 */
	private static final int AWAIT_TERMINATION = 60;

	/** 用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean */
	private static final Boolean WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN = true;

	/** 线程池名前缀 */
	private static final String THREAD_NAME_PREFIX = "Async-Service-";

	/**
	 * 创建异步任务线程池
	 *
	 * @return 配置好的线程池任务执行器
	 */
	@Bean("applicationTaskExecutor")
	public ThreadPoolTaskExecutor asyncTaskExecutor() {

		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

		taskExecutor.setCorePoolSize(CORE_POOL_SIZE);

		taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);

		taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_TIME);

		taskExecutor.setQueueCapacity(QUEUE_CAPACITY);

		taskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);

		taskExecutor.setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN);

		taskExecutor.setAwaitTerminationSeconds(AWAIT_TERMINATION);

		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		taskExecutor.initialize();

		return taskExecutor;

	}

}
