package com.example.execution.temporal;

import com.example.execution.temporal.activity.ExecutionActivityImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class TemporalWorker {

    public static final String TASK_QUEUE = "SFC_TASK_QUEUE";

    private WorkerFactory workerFactory;
    private final ExecutionActivityImpl executionActivity;

    public TemporalWorker(ExecutionActivityImpl executionActivity) {
        this.executionActivity = executionActivity;
    }

    @PostConstruct
    public void start() {
        // Connect to Temporal server
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(
                io.temporal.serviceclient.WorkflowServiceStubsOptions.newBuilder()
                        .setTarget("localhost:7234")
                        .build()
        );
        WorkflowClient client = WorkflowClient.newInstance(service);
        workerFactory = WorkerFactory.newInstance(client);

        // Create worker listening on the same task queue
        Worker worker = workerFactory.newWorker(TASK_QUEUE);

        // Register ONLY the activity (workflow is in Demand-ms)
        worker.registerActivitiesImplementations(executionActivity);

        workerFactory.start();
        System.out.println("✅ Execution-ms Temporal Worker started on queue: " + TASK_QUEUE);
    }

    @PreDestroy
    public void stop() {
        if (workerFactory != null) {
            workerFactory.shutdown();
            System.out.println("🛑 Execution-ms Temporal Worker stopped");
        }
    }
}
