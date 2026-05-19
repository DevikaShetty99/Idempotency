package com.example.Demand.temporal.workflow;

import com.example.Demand.dto.WorkflowInput;
import com.example.Demand.dto.WorkflowOutput;
import com.example.Demand.temporal.activity.ExecutionActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

/**
 * SFC Workflow Implementation
 *
 * Architecture:
 * 1. Demand-ms creates and executes this workflow
 * 2. Workflow calls ExecutionActivity via Temporal
 * 3. Execution-ms worker picks up and executes the activity
 * 4. No direct HTTP calls between microservices
 */
public class SFCWorkflowImpl implements SFCWorkflow {

    private final ExecutionActivity executionActivity;

    public SFCWorkflowImpl() {
        // Configure activity options with retry policy
        ActivityOptions activityOptions = ActivityOptions.newBuilder()
                .setScheduleToStartTimeout(Duration.ofMinutes(2))  // Wait up to 2 minutes for worker
                .setStartToCloseTimeout(Duration.ofSeconds(30))    // Max time for activity execution
                .setRetryOptions(RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(1))
                        .setMaximumInterval(Duration.ofSeconds(10))
                        .setBackoffCoefficient(2.0)
                        .setMaximumAttempts(5)  // Max 5 attempts
                        .build())
                .build();

        this.executionActivity = Workflow.newActivityStub(ExecutionActivity.class, activityOptions);
    }

    @Override
    public WorkflowOutput createSFC(WorkflowInput input) {
        // Call the activity - Temporal routes this to Execution-ms worker
        // Temporal handles retries, idempotency, and failure recovery
        WorkflowOutput output = executionActivity.reserveExecution(input);
        return output;
    }
}
