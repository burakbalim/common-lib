package com.thales.common.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Utility class for URL operations.
 * Used to read URL values from application configuration
 * (application.yml or application.properties).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class URLUtil {

    private final Environment environment;

    /**
     * -- GETTER --
     *  Returns the base URL defined in application settings.
     *
     * @return Base URL (e.g. www.wordflashy.com, staging.wordflashy.com)
     */
    @Getter
    @Value("${thales.url.base:localhost}")
    private String baseUrl;

    /**
     * Returns the URL value according to the specified property key.
     *
     * @param propertyKey Configuration key
     * @return URL value or null if not found
     */
    public String getUrl(String propertyKey) {
        String url = environment.getProperty(propertyKey);
        if (url == null) {
            log.warn("No URL defined with configuration key '{}'", propertyKey);
        }
        return url;
    }

    /**
     * Returns the URL value according to the specified property key.
     * Returns the default value if URL is not found.
     *
     * @param propertyKey Configuration key
     * @param defaultValue Default value
     * @return URL value or default value if not found
     */
    public String getUrl(String propertyKey, String defaultValue) {
        String url = environment.getProperty(propertyKey);
        if (url == null) {
            log.debug("No URL defined with configuration key '{}', using default value: {}",
                    propertyKey, defaultValue);
            return defaultValue;
        }
        return url;
    }

    /**
     * Creates a full URL by adding path to the base URL.
     *
     * @param path Path to add
     * @return Full URL
     */
    public String buildUrl(String path) {
        if (path == null || path.isEmpty()) {
            return baseUrl;
        }

        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return baseUrl + normalizedPath;
    }

    /**
     * Creates a full URL with the specified URL and parameters.
     *
     * @param baseUrl Base URL
     * @param path Path to add
     * @return Full URL
     */
    public static String buildUrl(String baseUrl, String path) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            return path;
        }

        if (path == null || path.isEmpty()) {
            return baseUrl;
        }

        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String normalizedPath = path.startsWith("/") ? path : "/" + path;

        return normalizedBaseUrl + normalizedPath;
    }
}
