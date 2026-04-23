package com.example.execution.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int routerId;
    private int operationId;
    private String sfcId;
    private LocalDateTime timestamp;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String txnId;

    // Getters & Setters
    public Long getId() { return id; }

    public int getRouterId() { return routerId; }
    public void setRouterId(int routerId) { this.routerId = routerId; }

    public int getOperationId() { return operationId; }
    public void setOperationId(int operationId) { this.operationId = operationId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTxnId() { return txnId; }
    public void setTxnId(String txnId) { this.txnId = txnId; }

    public String getSfcId() { return sfcId; }
    public void setSfcId(String sfcId) { this.sfcId = sfcId; }
}