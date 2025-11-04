package com.shortthirdman.bootlabs.authentication.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

    @Around("execution(* com.shortthirdman.bootlabs.authentication.controller..*(..))")
    public Object logApiRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        var method = request.getMethod();
        var uri = request.getRequestURI();
        var query = request.getQueryString();

        Object[] args = joinPoint.getArgs();

        log.info("➡️ START: [{}] {}?{} | Payload: {}", method, uri, query, Arrays.toString(args));
        try {
            Object result = joinPoint.proceed();
            long timeTaken = System.currentTimeMillis() - start;
            log.info("✅ END: [{}] {} | Time: {}ms | Response: {}", method, uri, timeTaken, result);
            return result;
        } catch (Exception e) {
            long timeTaken = System.currentTimeMillis() - start;
            log.error("❌ ERROR: [{}] {} | Time: {}ms | Exception: {}", method, uri, timeTaken, e.getMessage(), e);
            throw e;
        }
    }
}
