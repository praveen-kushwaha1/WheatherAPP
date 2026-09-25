package com.ncr.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AppConfig {

    /*
     * Creates an in-memory cache manager.
     *
     * No Redis is required.
     *
     * Cache data is stored inside the application's JVM memory.
     */
//    @Bean
//    public CacheManager cacheManager() {
//
//        return new ConcurrentMapCacheManager("weather");
//    }
}