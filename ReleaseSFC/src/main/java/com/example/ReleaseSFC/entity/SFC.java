package com.example.ReleaseSFC.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sfc")
public class SFC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "router_id", nullable = false)
    private int routerId;

    @Column(name = "operation_id", nullable = false)
    private int operationId;

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
}