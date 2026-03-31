package com.example.execution.service;

import com.example.execution.entity.Execution;

public interface ExecutionService {
    Execution reserve(Execution execution);
}