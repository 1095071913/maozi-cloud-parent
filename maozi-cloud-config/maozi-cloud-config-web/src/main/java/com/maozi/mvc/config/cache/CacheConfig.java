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

		// 构建 Caffeine 缓存实例：初始容量 100、写入后 600 秒过期、开启统计记录
		Caffeine<Object, Object> caffeine = Caffeine.newBuilder().initialCapacity(100).expireAfterWrite(600, TimeUnit.SECONDS).recordStats();

		// 创建 Caffeine 缓存管理器
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();

		// 允许缓存 null 值，防止缓存穿透
		caffeineCacheManager.setAllowNullValues(true);

		// 应用自定义的 Caffeine 配置
		caffeineCacheManager.setCaffeine(caffeine);

		// 预定义两个缓存区域：GlobeCache（全局缓存）和 CacheAll（全量缓存）
		caffeineCacheManager.setCacheNames(Arrays.asList("GlobeCache", "CacheAll"));

		return caffeineCacheManager;

	}

}
