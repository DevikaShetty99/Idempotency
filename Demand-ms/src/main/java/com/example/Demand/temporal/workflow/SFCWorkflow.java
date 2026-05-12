package com.example.Demand.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface SFCWorkflow {
    @WorkflowMethod
    String createSFC(int routerId, int operationId, String sfcId, String txnId);

}
