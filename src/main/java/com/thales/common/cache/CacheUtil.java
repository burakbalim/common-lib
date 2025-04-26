package com.thales.common.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;

import java.util.Collection;
import java.util.Optional;

/**
 * Utility class containing helper methods for cache operations.
 */
public final class CacheUtil {

    private CacheUtil() {
        // Utility class, should not be instantiated
    }

    /**
     * Clears all caches in the specified cache manager.
     *
     * @param cacheManager the cache manager to be cleared
     */
    public static void clearAllCaches(CacheManager cacheManager) {
        if (cacheManager == null) return;
        
        Collection<String> cacheNames = cacheManager.getCacheNames();
        if (cacheNames != null) {
            cacheNames.forEach(cacheName -> {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                }
            });
        }
    }

    /**
     * Clears a specific cache in the specified cache manager.
     *
     * @param cacheManager the cache manager containing the cache
     * @param cacheName    the name of the cache to be cleared
     */
    public static void clearCache(CacheManager cacheManager, String cacheName) {
        if (cacheManager == null || cacheName == null) return;
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Removes an item from the specified cache.
     *
     * @param cacheManager the cache manager to operate on
     * @param cacheName    the name of the cache to operate on
     * @param key          the key of the item to be removed
     */
    public static void evictFromCache(CacheManager cacheManager, String cacheName, Object key) {
        if (cacheManager == null || cacheName == null || key == null) return;
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    /**
     * Reads a value from the specified cache.
     *
     * @param cacheManager the cache manager to operate on
     * @param cacheName    the name of the cache to operate on
     * @param key          the key of the item to be read
     * @param type         the type of the return value
     * @param <T>          the type of the return value
     * @return the value from the cache or an empty optional
     */
    public static <T> Optional<T> getFromCache(CacheManager cacheManager, String cacheName, Object key, Class<T> type) {
        if (cacheManager == null || cacheName == null || key == null) return Optional.empty();
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            T value = cache.get(key, type);
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }

    /**
     * Writes a value to the specified cache.
     *
     * @param cacheManager the cache manager to operate on
     * @param cacheName    the name of the cache to operate on
     * @param key          the key of the item to be written
     * @param value        the value to be written
     */
    public static void putToCache(CacheManager cacheManager, String cacheName, Object key, Object value) {
        if (cacheManager == null || cacheName == null || key == null) return;
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * Checks if caching is enabled.
     * Cache is not enabled if it's of type NoOpCache or null.
     *
     * @param cacheManager the cache manager to check
     * @return whether caching is enabled
     */
    public static boolean isCacheEnabled(CacheManager cacheManager) {
        if (cacheManager == null) return false;
        
        Collection<String> cacheNames = cacheManager.getCacheNames();
        if (cacheNames == null || cacheNames.isEmpty()) return false;
        
        // Get a sample cache name and check it
        String sampleCacheName = cacheNames.iterator().next();
        Cache cache = cacheManager.getCache(sampleCacheName);
        
        return cache != null && !(cache instanceof NoOpCache);
    }
} 