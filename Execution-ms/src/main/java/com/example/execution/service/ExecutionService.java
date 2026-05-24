package com.example.execution.service;

import com.example.execution.entity.Execution;

import java.util.List;

public interface ExecutionService {
    Execution reserve(Execution execution);

    int updateStatusBySfcId(String sfcId, String newStatus);

    List<Execution> findBySfcId(String sfcId);
}
