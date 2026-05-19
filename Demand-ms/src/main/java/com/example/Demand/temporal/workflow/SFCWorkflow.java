package com.example.Demand.temporal.workflow;

import com.example.Demand.dto.WorkflowInput;
import com.example.Demand.dto.WorkflowOutput;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SFCWorkflow {
    @WorkflowMethod
    WorkflowOutput createSFC(WorkflowInput input);
}
