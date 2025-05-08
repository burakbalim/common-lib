package com.thales.common.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for CacheUtil to ensure it's properly initialized
 * in Spring context for all applications using the common-lib.
 */
@Configuration
public class CacheConfiguration {

    /**
     * Creates CacheUtil bean if not already defined in the application context.
     * This ensures CacheUtil is always available as a dependency.
     * 
     * @param cacheManager Spring's CacheManager bean
     * @return a CacheUtil instance
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheUtil cacheUtil(CacheManager cacheManager) {
        return new CacheUtil(cacheManager);
    }
} 