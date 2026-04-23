package com.example.Demand.dto;

public class ExecutionRequest {
    private int routerId;
    private int operationId;
    private String txnId;
    private String sfcId;

    public int getRouterId() { return routerId; }
    public void setRouterId(int routerId) { this.routerId = routerId; }

    public int getOperationId() { return operationId; }
    public void setOperationId(int operationId) { this.operationId = operationId; }

    public String getTxnId() { return txnId; }
    public void setTxnId(String txnId) { this.txnId = txnId; }

    public String getSfcId() { return sfcId; }
    public void setSfcId(String sfcId) { this.sfcId = sfcId; }
}
