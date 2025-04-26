package com.thales.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache configuration class.
 * This class automatically creates cache configuration based on thales.cache properties.
 */
@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnProperty(name = "thales.cache.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class CacheAutoConfiguration {

    private final CacheProperties cacheProperties;
    private final Environment environment;

    /**
     * Creates a Redis-backed cache manager.
     * Used when a Redis connection factory is available and cache type is set to REDIS.
     */
    @Primary
    @Bean(name = "cacheManager")
    @ConditionalOnClass(RedisConnectionFactory.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnProperty(name = "thales.cache.type", havingValue = "REDIS")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        log.info("Configuring Redis cache manager...");
        
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Custom cache configurations for TTL settings
        if (cacheProperties.getTtl() != null) {
            cacheProperties.getTtl().forEach((cacheName, ttl) -> {
                cacheConfigurations.put(cacheName, createRedisCacheConfiguration(ttl));
            });
        }

        // Prefixed cache keys with application name
        String prefix = "";
        if (cacheProperties.getRedis().isUseAppNameAsPrefix() && environment.getProperty("spring.application.name") != null) {
            prefix = environment.getProperty("spring.application.name") + "::";
        }
        
        if (!cacheProperties.getRedis().getKeyPrefix().isEmpty()) {
            prefix = prefix + cacheProperties.getRedis().getKeyPrefix() + "::";
        }
        
        String finalPrefix = prefix;
        
        // Create RedisCacheManager
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(createRedisCacheConfiguration(cacheProperties.getRedis().getDefaultTtl()))
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware();
        
        log.info("Redis cache manager prefix: {}", finalPrefix);
        return builder.build();
    }

    /**
     * Creates an in-memory (Caffeine) cache manager.
     * Used when cache type is set to IN_MEMORY or Redis is not available.
     */
    @Primary
    @Bean(name = "cacheManager")
    @ConditionalOnMissingBean(name = "redisCacheManager")
    @ConditionalOnClass(Caffeine.class)
    @ConditionalOnProperty(name = "thales.cache.type", havingValue = "IN_MEMORY", matchIfMissing = true)
    public CacheManager caffeineCacheManager() {
        log.info("Configuring in-memory (Caffeine) cache manager...");
        
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(cacheProperties.getInMemory().getMaximumSize())
                .expireAfterWrite(cacheProperties.getInMemory().getExpireAfterWrite())
                .expireAfterAccess(cacheProperties.getInMemory().getExpireAfterAccess());
        
        cacheManager.setCaffeine(caffeine);
        
        // Automatically create caches without custom TTL settings
        if (cacheProperties.getTtl() != null && !cacheProperties.getTtl().isEmpty()) {
            cacheManager.setCacheNames(cacheProperties.getTtl().keySet());
        }
        
        return cacheManager;
    }

    /**
     * Creates a Redis cache configuration with a custom TTL value.
     */
    private RedisCacheConfiguration createRedisCacheConfiguration(Duration ttl) {
        RedisSerializer<?> serializer = getRedisSerializer();
        
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();
    }
    
    /**
     * Creates a Redis serializer based on the specified serialization format in the configuration.
     */
    private RedisSerializer<?> getRedisSerializer() {
        String format = cacheProperties.getRedis().getSerializationFormat().toUpperCase();
        
        switch (format) {
            case "JSON":
                return new GenericJackson2JsonRedisSerializer();
            case "JDK":
            case "JAVA":
            default:
                return new JdkSerializationRedisSerializer();
        }
    }
} 