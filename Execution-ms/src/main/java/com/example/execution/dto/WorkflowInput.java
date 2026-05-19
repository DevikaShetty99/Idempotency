package com.example.execution.dto;

/**
 * DTO for Temporal Workflow Input
 * Shows as proper JSON in Temporal UI
 */
public class WorkflowInput {
    private int routerId;
    private int operationId;
    private String sfcId;
    private String txnId;

    // Constructors
    public WorkflowInput() {}

    public WorkflowInput(int routerId, int operationId, String sfcId, String txnId) {
        this.routerId = routerId;
        this.operationId = operationId;
        this.sfcId = sfcId;
        this.txnId = txnId;
    }

    // Getters & Setters
    public int getRouterId() {
        return routerId;
    }

    public void setRouterId(int routerId) {
        this.routerId = routerId;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public String getSfcId() {
        return sfcId;
    }

    public void setSfcId(String sfcId) {
        this.sfcId = sfcId;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }
}
