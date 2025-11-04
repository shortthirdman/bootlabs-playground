package com.shortthirdman.bootlabs.batchjobs.core.service;

import com.shortthirdman.bootlabs.batchjobs.core.annotations.BatchDefinition;
import com.shortthirdman.bootlabs.batchjobs.core.annotations.BatchMain;
import com.shortthirdman.bootlabs.batchjobs.core.beans.BatchHolder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchExecutorService {

    private final ApplicationContext context;
    private final PrometheusMetricsService metricsService;
    private final Map<String, BatchHolder> registeredBatches = new HashMap<>();

    @PostConstruct
    public void registerBatchJobs() {
        Map<String, Object> beans = context.getBeansWithAnnotation(BatchDefinition.class);
        if (beans.isEmpty()) {
            log.warn("No @BatchDefinition beans found. BatchExecutorService initialized empty.");
            return;
        }

        for (Object bean : beans.values()) {
            Class<?> beanClass = AopUtils.getTargetClass(bean);
            BatchDefinition definition = beanClass.getAnnotation(BatchDefinition.class);
            Method entryPoint = locateBatchEntryPoint(beanClass);

            registeredBatches.put(definition.name(), new BatchHolder(bean, definition, entryPoint));
            log.info("Registered Batch [{}]", definition.name());
        }
        log.info("Total {} batches registered.", registeredBatches.size());
    }

    public List<String> getRegisteredJobNames() {
        return List.copyOf(registeredBatches.keySet());
    }

    public int runByName(String batchName, Map<String, String> params) {
        BatchHolder holder = registeredBatches.get(batchName);

        if (holder == null) {
            log.error("No batch found with name '{}'", batchName);
            return 1;
        }

        BatchDefinition definition = holder.definition();
        if (!definition.enabled()) {
            log.warn("Batch '{}' is disabled and will not be executed.", batchName);
            return 0;
        }

        log.info("Starting Batch '{}' with parameters: {}", batchName, params);
        boolean success = executeWithRetries(holder, params);
        return logAndReturnStatus(batchName, success, definition.retries());
    }

    private boolean executeWithRetries(BatchHolder holder, Map<String, String> params) {
        BatchDefinition definition = holder.definition();
        String traceId = UUID.randomUUID().toString();
        int maxAttempts = definition.retries();
        long startTime = System.currentTimeMillis();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Executing Batch '{}' [attempt {}/{}] traceId={}",
                        definition.name(), attempt, maxAttempts, traceId);

                invokeBatchMethod(holder, params);
                long durationMs = System.currentTimeMillis() - startTime;
                metricsService.recordBatchExecution(definition.name(), true, attempt,  durationMs, traceId);
                log.info("Batch '{}' [traceId={}] completed successfully at attempt {}/{}",
                        definition.name(), traceId, attempt, maxAttempts);
                return true;
            } catch (Exception e) {
                Throwable rootCause = findRootCause(e);
                log.error("""
                                Batch '{}' failed at attempt {}/{}
                                TraceId: {}
                                Cause: {} - {}
                                """,
                        definition.name(),
                        attempt,
                        maxAttempts,
                        traceId,
                        rootCause.getClass().getSimpleName(),
                        rootCause.getMessage());

                log.debug("Detailed failure stacktrace for batch '{}':", definition.name(), e);

                if (attempt == maxAttempts) {
                    long durationMs = System.currentTimeMillis() - startTime;
                    metricsService.recordBatchExecution(definition.name(), false, attempt,  durationMs, traceId);
                    return false;
                }
            }
        }
        return false;
    }

    private void invokeBatchMethod(BatchHolder holder, Map<String, String> params) {
        Method method = holder.entryPoint();
        Object target = holder.bean();
        try {
            method.setAccessible(true);
            if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(Map.class)) {
                method.invoke(target, params);
            } else {
                method.invoke(target);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing batch method: " + method.getName(), e);
        }
    }


    private Method locateBatchEntryPoint(Class<?> targetClass) {
        Method found = null;
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BatchMain.class)) {
                if (found != null) {
                    throw new IllegalStateException("Multiple @BatchMain methods in " + targetClass.getName());
                }
                found = method;
            }
        }
        if (found == null) {
            throw new IllegalStateException("No @BatchMain method found in " + targetClass.getName());
        }
        found.setAccessible(true);
        return found;
    }


    private int logAndReturnStatus(String batchName, boolean success, int retries) {
        if (success) {
            log.info("Batch '{}' finished successfully.", batchName);
            return 0;
        } else {
            log.error("Batch '{}' failed after {} retries.", batchName, retries);
            return 1;
        }
    }


    private Throwable findRootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
