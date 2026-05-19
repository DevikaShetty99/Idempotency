package com.example.execution.temporal.activity;

import com.example.execution.dto.WorkflowInput;
import com.example.execution.dto.WorkflowOutput;
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
    public WorkflowOutput reserveExecution(WorkflowInput input) {
        // Create Execution object from input DTO
        Execution execution = new Execution();
        execution.setRouterId(input.getRouterId());
        execution.setOperationId(input.getOperationId());
        execution.setSfcId(input.getSfcId());
        execution.setTxnId(input.getTxnId());

        // Call the service layer (which has idempotency logic)
        Execution result = executionService.reserve(execution);

        // Return structured DTO (shows as JSON in Temporal UI)
        WorkflowOutput output = new WorkflowOutput();
        output.setId(result.getId());
        output.setRouterId(result.getRouterId());
        output.setOperationId(result.getOperationId());
        output.setSfcId(result.getSfcId());
        output.setTxnId(result.getTxnId());
        output.setTimestamp(result.getTimestamp().toString());
        output.setStatus(result.getStatus());

        return output;
    }
}
