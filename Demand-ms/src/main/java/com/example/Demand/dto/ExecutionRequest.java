package com.example.Demand.dto;

public class ExecutionRequest {
    private int routerId;
    private int operationId;

    public int getRouterId() { return routerId; }
    public void setRouterId(int routerId) { this.routerId = routerId; }

    public int getOperationId() { return operationId; }
    public void setOperationId(int operationId) { this.operationId = operationId; }
}
