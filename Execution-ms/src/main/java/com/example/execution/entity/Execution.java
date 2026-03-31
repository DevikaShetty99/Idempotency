package com.example.execution.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int routerId;
    private int operationId;
    private String status;

    // Getters & Setters
    public Long getId() { return id; }

    public int getRouterId() { return routerId; }
    public void setRouterId(int routerId) { this.routerId = routerId; }

    public int getOperationId() { return operationId; }
    public void setOperationId(int operationId) { this.operationId = operationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}