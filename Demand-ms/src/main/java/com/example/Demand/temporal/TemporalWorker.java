package com.example.Demand.temporal;

import com.example.Demand.temporal.workflow.SFCWorkflowImpl;
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

    @PostConstruct
    public void start() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(
                io.temporal.serviceclient.WorkflowServiceStubsOptions.newBuilder()
                        .setTarget("localhost:7234")
                        .build()
        );
        WorkflowClient client = WorkflowClient.newInstance(service);
        workerFactory = WorkerFactory.newInstance(client);

        Worker worker = workerFactory.newWorker(TASK_QUEUE);

        // Register ONLY the workflow (activities are registered in Execution-ms)
        worker.registerWorkflowImplementationTypes(SFCWorkflowImpl.class);

        workerFactory.start();
        System.out.println(" Demand-ms Temporal Worker started - Workflow registered on queue: " + TASK_QUEUE);
    }

    @PreDestroy
    public void stop() {
        if (workerFactory != null) {
            workerFactory.shutdown();
            System.out.println(" Demand-ms Temporal Worker stopped");
        }
    }
}
