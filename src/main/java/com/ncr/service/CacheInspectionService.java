package com.ncr.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CacheInspectionService {

    private final CacheManager cacheManager;

    public CacheInspectionService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void printCacheContents(String cacheName) {

        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {

            System.out.println(
                    "No such cache: " + cacheName
            );

            return;
        }

        System.out.println("========== CACHE CONTENTS ==========");

        /*
         * ConcurrentMapCache provides access to
         * its underlying ConcurrentMap.
         */
        if (cache instanceof ConcurrentMapCache concurrentMapCache) {

            Map<?, ?> nativeCache =
                    concurrentMapCache.getNativeCache();

            nativeCache.forEach((key, value) ->
                    System.out.println(
                            "Key = " + key +
                            ", Value = " + value
                    )
            );
        }

        System.out.println("====================================");
    }
}