package com.example.Demand.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sfc")
public class Demand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;

    @Column(name = "router_id", nullable = false)
    private int routerId;

    @Column(name = "operation_id", nullable = false)
    private int operationId;

    @Column(name = "sfc_id")
    private String sfcId;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "txn_id")
    private String txnId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "status")
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}