package com.example.execution.service;

import com.example.execution.entity.Execution;
import com.example.execution.repository.ExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExecutionServiceImpl implements ExecutionService {

    @Autowired
    private ExecutionRepository repository;

    @Override
    public Execution reserve(Execution execution) {

        // Simulate business logic
        execution.setStatus("RESERVED");

        return repository.save(execution);
    }
}