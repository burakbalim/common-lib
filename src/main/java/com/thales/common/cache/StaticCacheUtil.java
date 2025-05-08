package com.thales.common.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Static utility methods for cache operations.
 * This class provides static access to cache operations without requiring dependency injection.
 */
public final class StaticCacheUtil {

    private static final AtomicReference<CacheManager> cacheManager = new AtomicReference<>();

    private StaticCacheUtil() {
        // Utility class, should not be instantiated
    }

    /**
     * Internal method to set the cache manager.
     * This is called by CacheUtil during its initialization.
     */
    static void setCacheManager(CacheManager manager) {
        cacheManager.set(manager);
    }

    /**
     * Clears all caches managed by the cache manager.
     */
    public static void clearAllCaches() {
        CacheManager manager = cacheManager.get();
        if (manager == null) return;

        Collection<String> cacheNames = manager.getCacheNames();
        cacheNames.forEach(cacheName -> {
            Cache cache = manager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    /**
     * Clears a specific cache by name.
     * 
     * @param cacheName the name of the cache to clear
     */
    public static void clearCache(String cacheName) {
        CacheManager manager = cacheManager.get();
        if (manager == null || cacheName == null) return;

        Cache cache = manager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Removes a specific entry from a cache.
     * 
     * @param cacheName the name of the cache
     * @param key the key to remove
     */
    public static void evict(String cacheName, Object key) {
        CacheManager manager = cacheManager.get();
        if (manager == null || cacheName == null || key == null) return;

        Cache cache = manager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    /**
     * Retrieves a value from a cache with the given key and type.
     * 
     * @param <T> the type of value to retrieve
     * @param cacheName the name of the cache
     * @param key the key to look up
     * @param type the expected type of the value
     * @return an Optional containing the value if found and of the correct type, or empty
     */
    public static <T> Optional<T> get(String cacheName, Object key, Class<T> type) {
        CacheManager manager = cacheManager.get();
        if (manager == null || cacheName == null || key == null) return Optional.empty();

        Cache cache = manager.getCache(cacheName);
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null && valueWrapper.get() != null) {
                Object value = valueWrapper.get();
                if (type.isInstance(value)) {
                    return Optional.of(type.cast(value));
                }
            }
        }
        return Optional.empty();
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
    public static <T> T getWithDefault(String cacheName, Object key, Class<T> type, T defaultValue) {
        Optional<T> value = get(cacheName, key, type);
        return value.orElse(defaultValue);
    }

    /**
     * Stores a value in a cache.
     * 
     * @param cacheName the name of the cache
     * @param key the key to store under
     * @param value the value to store
     */
    public static void put(String cacheName, Object key, Object value) {
        CacheManager manager = cacheManager.get();
        if (manager == null || cacheName == null || key == null) return;

        Cache cache = manager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * Checks if caching is enabled.
     * 
     * @return true if caching is enabled and available, false otherwise
     */
    public static boolean isCacheEnabled() {
        CacheManager manager = cacheManager.get();
        if (manager == null) return false;

        Collection<String> cacheNames = manager.getCacheNames();
        if (cacheNames == null || cacheNames.isEmpty()) return false;

        String sampleCacheName = cacheNames.iterator().next();
        Cache cache = manager.getCache(sampleCacheName);

        return cache != null && !(cache instanceof NoOpCache);
    }
} 