package com.example.execution.service;

import com.example.execution.entity.Execution;
import com.example.execution.entity.Idempotency;
import com.example.execution.repository.ExecutionRepository;
import com.example.execution.repository.IdempotencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExecutionServiceImpl implements ExecutionService {

    private final ExecutionRepository executionRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    public ExecutionServiceImpl(ExecutionRepository executionRepository,
                                IdempotencyRepository idempotencyRepository) {
        this.executionRepository = executionRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Override
    public Execution reserve(Execution execution) {

        // STEP 1: Check if txn already exists
        Optional<Idempotency> existing = idempotencyRepository.findByTxnId(execution.getTxnId());

        if (existing.isPresent()) {
            // If still processing, wait and retry
            if ("PROCESSING".equals(existing.get().getStatus())) {
                try {
                    Thread.sleep(5000); // Wait 5 seconds
                    existing = idempotencyRepository.findByTxnId(execution.getTxnId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Return the cached response from idempotency table
            try {
                String cachedResponse = existing.get().getResponse();
                if (cachedResponse == null || cachedResponse.isEmpty()) {
                    throw new RuntimeException("Cached response is empty - request still processing");
                }
                Execution cachedExecution = objectMapper.readValue(cachedResponse, Execution.class);
                return cachedExecution;
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize cached response: " + e.getMessage(), e);
            }
        }

        // STEP 2: Save idempotency record FIRST (claims this txnId)
        Idempotency idem = new Idempotency();
        idem.setTxnId(execution.getTxnId());
        idem.setStatus("PROCESSING");
        idem.setResponse("");
        idem.setCreatedAt(LocalDateTime.now());

        try {
            idempotencyRepository.save(idem);
            // No delay - fast execution

        } catch (Exception e) {
            // Another request claimed this txnId, return cached response
            Optional<Idempotency> claimed = idempotencyRepository.findByTxnId(execution.getTxnId());
            if (claimed.isPresent()) {
                try {
                    String cachedResponse = claimed.get().getResponse();
                    if (cachedResponse == null || cachedResponse.isEmpty()) {
                        throw new RuntimeException("Cached response is empty - request still processing");
                    }
                    Execution cachedExecution = objectMapper.readValue(cachedResponse, Execution.class);
                    return cachedExecution;
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to deserialize cached response: " + ex.getMessage(), ex);
                }
            }
            throw new RuntimeException("Failed to claim txnId and no cached response found");
        }

        // STEP 3: Process execution (txnId now claimed)
        try {
            execution.setStatus("RESERVED");
            execution.setTimestamp(LocalDateTime.now());
            Execution saved = executionRepository.save(execution);

            // STEP 4: Update idempotency with final response
            idem.setStatus("SUCCESS");
            idem.setResponse(String.format("{\"id\":%d,\"routerId\":%d,\"operationId\":%d,\"sfcId\":\"%s\",\"timestamp\":\"%s\"}",
                saved.getId(), saved.getRouterId(), saved.getOperationId(), saved.getSfcId(), saved.getTimestamp()));
            idempotencyRepository.save(idem);

            return saved;

        } catch (Exception e) {
            // Execution failed - update idempotency status to FAILED
            idem.setStatus("FAILED");
            idem.setResponse(String.format("{\"error\":\"%s\"}", e.getMessage()));
            idempotencyRepository.save(idem);

            throw new RuntimeException("Execution failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int updateStatusBySfcId(String sfcId, String newStatus) {
        return executionRepository.updateStatusBySfcId(sfcId, newStatus);
    }

    @Override
    public List<Execution> findBySfcId(String sfcId) {
        return executionRepository.findBySfcId(sfcId);
    }
}