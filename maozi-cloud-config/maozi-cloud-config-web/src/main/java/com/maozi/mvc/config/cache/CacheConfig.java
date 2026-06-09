package com.maozi.mvc.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 * <p>
 * 基于 Caffeine 创建全局缓存管理器，配置初始容量 100、写入后过期时间 600 秒，
 * 并启用统计信息记录。预定义了 GlobeCache 和 CacheAll 两个缓存区域。
 * </p>
 *
 * @author maozi
 */
@Configuration
public class CacheConfig {

	/**
	 * 创建全局缓存管理器
	 *
	 * @return Caffeine 缓存管理器实例
	 */
	@Bean
	public CacheManager globalCacheManager() {

		Caffeine<Object, Object> caffeine = Caffeine.newBuilder().initialCapacity(100).expireAfterWrite(600, TimeUnit.SECONDS).recordStats();

		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

		caffeineCacheManager.setAllowNullValues(true);

		caffeineCacheManager.setCaffeine(caffeine);

		caffeineCacheManager.setCacheNames(Arrays.asList("GlobeCache", "CacheAll"));

		return caffeineCacheManager;

	}

}
