package com.thales.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnClass(name = "org.springframework.data.redis.connection.RedisConnectionFactory")
@ConditionalOnProperty(name = "thales.cache.type", havingValue = "REDIS")
public class RedisCacheManagerConfiguration {

    private final CacheProperties cacheProperties;

    @Primary
    @Bean(name = "cacheManager")
    @ConditionalOnBean(type = "org.springframework.data.redis.connection.RedisConnectionFactory")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        log.info("Configuring Redis cache manager...");

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        if (cacheProperties.getTtl() != null) {
            cacheProperties.getTtl().forEach((cacheName, ttl) ->
                    cacheConfigurations.put(cacheName, createRedisCacheConfiguration(ttl)));
        }

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(createRedisCacheConfiguration(cacheProperties.getRedis().getDefaultTtl()))
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    private RedisCacheConfiguration createRedisCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(getRedisSerializer()))
                .disableCachingNullValues();
    }

    private RedisSerializer<?> getRedisSerializer() {
        String format = cacheProperties.getRedis().getSerializationFormat().toUpperCase();
        return switch (format) {
            case "JSON" -> new GenericJackson2JsonRedisSerializer();
            default -> new JdkSerializationRedisSerializer();
        };
    }
}
