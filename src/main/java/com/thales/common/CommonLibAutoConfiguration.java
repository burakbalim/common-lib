package com.thales.common;

import com.thales.common.cache.CacheConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration class for common lib
 * Used for Spring Boot auto-configuration
 */
@Configuration
@ComponentScan(basePackages = "com.thales.common")
@Import({CacheConfiguration.class})
@AutoConfigureBefore(RedisAutoConfiguration.class)
@ImportAutoConfiguration(exclude = {RedisAutoConfiguration.class})
public class CommonLibAutoConfiguration {
    // Empty class for auto-configuration, component scan is sufficient
} 