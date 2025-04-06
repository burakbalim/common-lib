package com.thales.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * İstek token'larını Feign Client üzerinden yönlendirmek için interceptor
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            String token = attributes.getRequest().getHeader(AUTHORIZATION_HEADER);
            if (Objects.nonNull(token)) {
                template.header(AUTHORIZATION_HEADER, token);
            }
        }
    }
} 