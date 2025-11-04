package com.shortthirdman.bootlabs.batchjobs.core.annotations;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

public class RunningBatchCondition implements Condition {

    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        var batchStatus = context.getEnvironment().getProperty("batch.running.status");
        return batchStatus == null || !batchStatus.equals("RUNNING");
    }
}
