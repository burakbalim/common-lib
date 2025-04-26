package com.thales.common.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Properties class for cache configuration.
 * Can be configured from application.properties or application.yml file.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "thales.cache")
public class CacheProperties {

    /**
     * Cache type (IN_MEMORY, REDIS, NONE)
     */
    private CacheType type = CacheType.IN_MEMORY;

    /**
     * Whether caching is enabled
     */
    private boolean enabled = true;

    /**
     * Cache names and their TTL durations
     */
    private Map<String, Duration> ttl = new HashMap<>();

    /**
     * In-memory cache (Caffeine) configuration
     */
    private final InMemory inMemory = new InMemory();

    /**
     * Redis cache configuration
     */
    private final Redis redis = new Redis();

    /**
     * Inner class for in-memory cache configuration
     */
    @Getter
    @Setter
    public static class InMemory {
        /**
         * Default maximum cache size
         */
        private long maximumSize = 1000;
        
        /**
         * Default TTL duration (seconds)
         */
        private Duration defaultTtl = Duration.ofMinutes(10);
        
        /**
         * Expiration delay after write (seconds)
         */
        private Duration expireAfterWrite = Duration.ofMinutes(10);
        
        /**
         * Expiration period after last access (seconds)
         */
        private Duration expireAfterAccess = Duration.ofMinutes(10);
    }

    /**
     * Inner class for Redis cache configuration
     */
    @Getter
    @Setter
    public static class Redis {
        /**
         * TTL duration (seconds)
         */
        private Duration defaultTtl = Duration.ofMinutes(30);
        
        /**
         * Cache name prefix (when multiple applications use the same Redis)
         */
        private String keyPrefix = "";
        
        /**
         * Whether to use application name as a prefix in cache keys
         */
        private boolean useAppNameAsPrefix = true;
        
        /**
         * Serialization format (default: JDK)
         * Alternatives: JSON, JAVA
         */
        private String serializationFormat = "JDK";
    }
} 