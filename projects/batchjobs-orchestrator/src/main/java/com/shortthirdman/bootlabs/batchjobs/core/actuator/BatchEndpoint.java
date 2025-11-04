package com.shortthirdman.bootlabs.batchjobs.core.actuator;

import com.shortthirdman.bootlabs.batchjobs.core.service.BatchExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Component
@Endpoint(id = "batch")
@RequiredArgsConstructor
public class BatchEndpoint {

    private final BatchExecutorService batchExecutorService;

    @ReadOperation
    public List<String> listBatches() {
        return batchExecutorService.getRegisteredJobNames();
    }

    @WriteOperation
    public String runBatch(
            @Selector String name,
            @Selector Map<String, String> params) {
        try {
            CompletableFuture.runAsync(() -> batchExecutorService.runByName(name, params != null ? params : Map.of()), Executors.newVirtualThreadPerTaskExecutor());
            return "Batch started: " + name;
        } catch (Exception e) {
            return "Batch failed: " + e.getMessage();
        }
    }
}
