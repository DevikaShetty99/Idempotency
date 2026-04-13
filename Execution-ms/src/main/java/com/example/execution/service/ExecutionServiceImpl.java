package com.example.execution.service;

import com.example.execution.entity.Execution;
import com.example.execution.entity.Idempotency;
import com.example.execution.repository.ExecutionRepository;
import com.example.execution.repository.IdempotencyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ExecutionServiceImpl implements ExecutionService {

    private final ExecutionRepository executionRepository;
    private final IdempotencyRepository idempotencyRepository;

    public ExecutionServiceImpl(ExecutionRepository executionRepository,
                                IdempotencyRepository idempotencyRepository) {
        this.executionRepository = executionRepository;
        this.idempotencyRepository = idempotencyRepository;
    }

    @Override
    public Execution reserve(Execution execution) {

        // 🔴 STEP 1: Check if txn already exists
        Optional<Idempotency> existing = idempotencyRepository.findByTxnId(execution.getTxnId());

        if (existing.isPresent()) {
            System.out.println("Duplicate request detected - returning cached response");

            // Return cached execution to indicate it was already processed
            Execution cachedExecution = new Execution();
            cachedExecution.setRouterId(execution.getRouterId());
            cachedExecution.setOperationId(execution.getOperationId());
            cachedExecution.setStatus("DUPLICATE"); // Mark as DUPLICATE so Demand can identify it
            cachedExecution.setTxnId(execution.getTxnId());

            return cachedExecution;
        }

        // 🟢 STEP 2: Process normally
        execution.setStatus("RESERVED");

        Execution saved = executionRepository.save(execution);

        // 🟢 STEP 3: Save idempotency record
        Idempotency idem = new Idempotency();
        idem.setTxnId(execution.getTxnId());
        idem.setStatus("SUCCESS");
        idem.setResponse(String.format("{\"id\":%d,\"routerId\":%d,\"operationId\":%d,\"status\":\"%s\",\"txnId\":\"%s\"}",
            saved.getId(), saved.getRouterId(), saved.getOperationId(), saved.getStatus(), saved.getTxnId()));
        idem.setCreatedAt(LocalDateTime.now());

        idempotencyRepository.save(idem);

        return saved;
    }
}