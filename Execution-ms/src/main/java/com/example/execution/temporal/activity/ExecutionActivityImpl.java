package com.example.execution.temporal.activity;

import com.example.execution.entity.Execution;
import com.example.execution.service.ExecutionService;
import org.springframework.stereotype.Component;

@Component
public class ExecutionActivityImpl implements ExecutionActivity {

    private final ExecutionService executionService;

    public ExecutionActivityImpl(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @Override
    public String reserveExecution(int routerId, int operationId, String sfcId, String txnId) {
        // Create Execution object
        Execution execution = new Execution();
        execution.setRouterId(routerId);
        execution.setOperationId(operationId);
        execution.setSfcId(sfcId);
        execution.setTxnId(txnId);

        // Call the service layer (which has idempotency logic)
        Execution result = executionService.reserve(execution);

        // Return JSON response
        return String.format("{\"id\":%d,\"routerId\":%d,\"operationId\":%d,\"sfcId\":\"%s\",\"timestamp\":\"%s\",\"status\":\"%s\"}",
                result.getId(), result.getRouterId(), result.getOperationId(),
                result.getSfcId(), result.getTimestamp(), result.getStatus());
    }
}
