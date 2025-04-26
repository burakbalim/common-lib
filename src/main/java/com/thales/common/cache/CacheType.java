package com.thales.common.cache;

/**
 * Defines the supported cache types.
 */
public enum CacheType {
    /**
     * In-memory cache, uses Caffeine library
     */
    IN_MEMORY,
    
    /**
     * Distributed cache, uses Redis
     */
    REDIS,
    
    /**
     * Caching is disabled
     */
    NONE
} 