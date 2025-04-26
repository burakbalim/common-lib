package com.thales.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration class for common lib
 * Used for Spring Boot auto-configuration
 */
@Configuration
@ComponentScan(basePackages = "com.thales.common")
public class CommonLibAutoConfiguration {
    // Empty class for auto-configuration, component scan is sufficient
} 