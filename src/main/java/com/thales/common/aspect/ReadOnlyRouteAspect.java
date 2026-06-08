package com.thales.common.aspect;

import com.thales.common.datasource.DataSourceContextHolder;
import com.thales.common.datasource.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@Order(1)
@ConditionalOnProperty(name = "spring.datasource.replica.url")
public class ReadOnlyRouteAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)" +
            " || @within(org.springframework.transaction.annotation.Transactional)")
    public Object route(ProceedingJoinPoint pjp) throws Throwable {
        DataSourceContextHolder.push(resolveTarget(pjp));
        try {
            return pjp.proceed();
        } finally {
            DataSourceContextHolder.pop();
        }
    }

    private DataSourceType resolveTarget(ProceedingJoinPoint pjp) {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();
        Transactional tx = method.getAnnotation(Transactional.class);
        if (tx == null) {
            tx = pjp.getTarget().getClass().getAnnotation(Transactional.class);
        }
        return (tx != null && tx.readOnly()) ? DataSourceType.REPLICA : DataSourceType.PRIMARY;
    }
}
