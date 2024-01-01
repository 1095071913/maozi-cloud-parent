package com.maozi.mvc.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

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