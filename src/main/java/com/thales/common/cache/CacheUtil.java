package com.thales.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Utility class for cache operations to be used as a Spring bean.
 * For static access to cache operations, use the StaticCacheUtil class.
 */
@Component
public final class CacheUtil {
    
    private final CacheManager cacheManager;

    @Autowired
    public CacheUtil(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        // Register the cacheManager with the static utility class
        StaticCacheUtil.setCacheManager(cacheManager);
    }

    /**
     * Clears a specific cache by name.
     * 
     * @param cacheName the name of the cache to clear
     */
    public void clear(String cacheName) {
        if (cacheManager == null || cacheName == null) return;

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Clears all caches managed by the cache manager.
     */
    public void clearAll() {
        if (cacheManager == null) return;

        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    /**
     * Removes a specific entry from a cache.
     * 
     * @param cacheName the name of the cache
     * @param key the key to remove
     */
    public void evict(String cacheName, Object key) {
        if (cacheManager == null || cacheName == null || key == null) return;

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    /**
     * Retrieves a value from a cache.
     * 
     * @param <T> the type of value to retrieve
     * @param cacheName the name of the cache
     * @param key the key to look up
     * @param type the expected type of the value
     * @return the value if found and of the correct type, or null
     */
    public <T> T get(String cacheName, Object key, Class<T> type) {
        if (cacheManager == null || cacheName == null || key == null) return null;

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null && valueWrapper.get() != null) {
                Object value = valueWrapper.get();
                if (type.isInstance(value)) {
                    return type.cast(value);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a value from a cache with a default fallback.
     * 
     * @param <T> the type of value to retrieve
     * @param cacheName the name of the cache
     * @param key the key to look up
     * @param type the expected type of the value
     * @param defaultValue the default value to return if not found
     * @return the cached value if found, or the default value
     */
    public <T> T getWithDefault(String cacheName, Object key, Class<T> type, T defaultValue) {
        T value = get(cacheName, key, type);
        return value != null ? value : defaultValue;
    }

    /**
     * Stores a value in a cache.
     * 
     * @param cacheName the name of the cache
     * @param key the key to store under
     * @param value the value to store
     */
    public void put(String cacheName, Object key, Object value) {
        if (cacheManager == null || cacheName == null || key == null) return;

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * Checks if caching is enabled.
     * 
     * @return true if caching is enabled and available, false otherwise
     */
    public boolean isCacheEnabled() {
        if (cacheManager == null) return false;

        Collection<String> cacheNames = cacheManager.getCacheNames();
        if (cacheNames == null || cacheNames.isEmpty()) return false;

        String sampleCacheName = cacheNames.iterator().next();
        Cache cache = cacheManager.getCache(sampleCacheName);

        return cache != null && !(cache instanceof NoOpCache);
    }
    
    /**
     * Gets the underlying CacheManager.
     * 
     * @return the CacheManager
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
