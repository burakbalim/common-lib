package com.thales.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Common lib için otomatik yapılandırma sınıfı
 * Spring Boot auto-configuration için kullanılacak
 */
@Configuration
@ComponentScan(basePackages = "com.thales.common")
public class CommonLibAutoConfiguration {
    // Auto-configuration için boş sınıf, component scan yeterli
} 