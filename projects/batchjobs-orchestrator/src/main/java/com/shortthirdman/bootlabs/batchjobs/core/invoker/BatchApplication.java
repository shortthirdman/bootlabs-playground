package com.shortthirdman.bootlabs.batchjobs.core.invoker;

import com.shortthirdman.bootlabs.batchjobs.core.service.BatchExecutorService;
import com.shortthirdman.bootlabs.batchjobs.core.utils.SystemPropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class BatchApplication {

    private static final String DEFAULT_ARG_BATCH_NAME = "batchName";
    private static final String DEFAULT_ARG_BATCH_PARAMS = "batchParams";
    private static final String DEFAULT_PROP_BATCH_STATUS = "batch.running.status";
    private static final String STATUS_RUNNING = "RUNNING";

    public static void runIfBatchMode(Class<?> primarySource,String[] args) {
        ApplicationArguments appArgs = new DefaultApplicationArguments(args);
        String batchArgName = SystemPropertyUtils.getProperty("spring.application.batch.argName", DEFAULT_ARG_BATCH_NAME, String.class);

        if (!appArgs.containsOption(batchArgName)) {
            return;
        }

        log.info("Starting Batch Application...");
        SpringApplication app = new SpringApplication(primarySource);
        ConfigurableApplicationContext context = launchBatchContext(appArgs, app);
        String batchName = appArgs.getOptionValues(batchArgName).getFirst();
        Map<String, String> parameters = extractBatchParameters(appArgs);

        try {
            BatchExecutorService executor = context.getBean(BatchExecutorService.class);
            int status = executor.runByName(batchName, parameters);
            log.info("Batch '{}' completed with status: {}", batchName, status);
            shutdownGracefully(context, status);
        } catch (Exception e) {
            log.error("Batch execution failed for '{}'", batchName, e);
            System.exit(1);
        }
    }

    private static ConfigurableApplicationContext launchBatchContext(ApplicationArguments appArgs, SpringApplication app) {
        app.setWebApplicationType(WebApplicationType.NONE);
        app.addInitializers(ctx -> injectBatchRuntimeProperty(ctx.getEnvironment()));
        return app.run(appArgs.getSourceArgs());
    }


    private static void shutdownGracefully(ConfigurableApplicationContext context, int exitStatus) {
        log.info("Graceful shutdown initiated");
        int finalCode = SpringApplication.exit(context, () -> exitStatus);
        System.exit(finalCode);
    }

    private static void injectBatchRuntimeProperty(ConfigurableEnvironment env) {
        Map<String, Object> properties = Map.of(DEFAULT_PROP_BATCH_STATUS, STATUS_RUNNING);
        env.getPropertySources().addFirst(new MapPropertySource("batchDynamicProps", properties));
    }

    private static Map<String, String> extractBatchParameters(ApplicationArguments appArgs) {
        String executionParamsArgName = SystemPropertyUtils.getProperty("spring.application.batch.executionParamsArgName", DEFAULT_ARG_BATCH_PARAMS, String.class);
        List<String> rawValues = appArgs.getOptionValues(executionParamsArgName);
        if (rawValues == null || rawValues.isEmpty()) return Collections.emptyMap();
        String paramLine = rawValues.getFirst();
        if (paramLine == null || paramLine.isBlank()) return Collections.emptyMap();
        Map<String, String> params = new HashMap<>();
        for (String entry : paramLine.split(",")) {
            String[] kv = entry.split("=", 2);
            if (kv.length == 2 && !kv[0].isBlank()) {
                params.put(kv[0].trim(), kv[1].trim());
            }
        }
        return params;
    }
}
