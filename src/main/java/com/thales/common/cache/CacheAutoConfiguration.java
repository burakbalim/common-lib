package com.thales.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnProperty(name = "thales.cache.enabled", havingValue = "true", matchIfMissing = true)
public class CacheAutoConfiguration {

    private final CacheProperties cacheProperties;

    @Primary
    @Bean(name = "cacheManager")
    @ConditionalOnMissingBean(name = "cacheManager")
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

        if (cacheProperties.getTtl() != null && !cacheProperties.getTtl().isEmpty()) {
            cacheManager.setCacheNames(cacheProperties.getTtl().keySet());
        }

        return cacheManager;
    }
}
