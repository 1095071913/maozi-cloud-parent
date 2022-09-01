package com.jiumao.mvc.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

	@Bean
	public CacheManager globalCacheManager() {
		Caffeine<Object, Object> caffeine = Caffeine.newBuilder().initialCapacity(100)
				// .refreshAfterWrite(60, TimeUnit.SECONDS)
				.expireAfterWrite(600, TimeUnit.SECONDS).recordStats();

		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setAllowNullValues(true);
		caffeineCacheManager.setCaffeine(caffeine);
		caffeineCacheManager.setCacheNames(Arrays.asList("GlobeCache", "CacheAll"));

		return caffeineCacheManager;
	}

}