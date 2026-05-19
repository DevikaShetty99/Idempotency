package com.example.Demand.dto;

/**
 * DTO for Temporal Workflow Output
 * Shows as proper JSON in Temporal UI
 */
public class WorkflowOutput {
    private Long id;
    private int routerId;
    private int operationId;
    private String sfcId;
    private String txnId;
    private String timestamp;
    private String status;

    // Constructors
    public WorkflowOutput() {}

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
