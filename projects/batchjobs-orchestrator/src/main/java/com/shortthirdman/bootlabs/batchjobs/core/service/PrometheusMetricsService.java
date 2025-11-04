package com.shortthirdman.bootlabs.batchjobs.core.service;

import com.shortthirdman.bootlabs.batchjobs.core.config.PrometheusPushGatewayConfiguration;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrometheusMetricsService {

    private static final String METRIC_EXECUTION_DURATION = "batch_execution_duration_seconds";
    private static final String METRIC_EXECUTION_TOTAL = "batch_execution_total";

    private final PrometheusPushGatewayConfiguration configuration;
    private final CollectorRegistry registry = new CollectorRegistry();

    public void recordBatchExecution(String batchName, boolean success, int retryCount, long durationMs, String traceId) {
        recordExecutionDuration(batchName, retryCount, traceId, durationMs);
        incrementExecutionCounter(batchName, traceId, success);
        pushToGateway(batchName);
        log.info("Recorded metrics for batch '{}' [success={}, retry={}, duration={}ms, traceId={}]",
                batchName, success, retryCount, durationMs, traceId);
    }

    private void recordExecutionDuration(String batchName, int retryCount, String traceId, long durationMs) {
        Histogram histogram = Histogram.build()
                .name(METRIC_EXECUTION_DURATION)
                .help("Duration of batch executions in seconds")
                .labelNames("batch_name", "trace_id", "retry_count")
                .register(registry);
        histogram.labels(batchName, traceId, String.valueOf(retryCount)).observe(durationMs / 1000.0);
    }

    private void incrementExecutionCounter(String batchName, String traceId, boolean success) {
        Counter counter = Counter.build()
                .name(METRIC_EXECUTION_TOTAL)
                .help("Total number of batch executions")
                .labelNames("batch_name", "trace_id", "status")
                .register(registry);
        counter.labels(batchName, traceId, success ? "success" : "failure").inc();
    }

    private void pushToGateway(String batchName) {
        PushGateway pg = new PushGateway(configuration.getUrl());
        Map<String, String> groupingKey = new HashMap<>();
        groupingKey.put("application", configuration.getApplicationName());
        groupingKey.put("environment", configuration.getEnvironment());
        groupingKey.put("instance", configuration.getInstanceId());
        groupingKey.put("batch_name", batchName);
        try {
            pg.pushAdd(registry, "batch_job_metrics", groupingKey);
            log.info("Metrics pushed to PushGateway for batch '{}' (app={}, env={})", batchName, configuration.getApplicationName(), configuration.getEnvironment());
        } catch (IOException e) {
            log.error("Failed to push metrics to PushGateway", e);
        }
    }
}
