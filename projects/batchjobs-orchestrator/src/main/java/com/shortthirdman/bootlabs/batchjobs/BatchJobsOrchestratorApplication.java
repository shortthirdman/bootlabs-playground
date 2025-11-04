package com.shortthirdman.bootlabs.batchjobs;

import com.shortthirdman.bootlabs.batchjobs.core.invoker.BatchApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchJobsOrchestratorApplication {

	public static void main(String[] args) {
        BatchApplication.runIfBatchMode(BatchJobsOrchestratorApplication.class, args);
		SpringApplication.run(BatchJobsOrchestratorApplication.class, args);
	}

}
