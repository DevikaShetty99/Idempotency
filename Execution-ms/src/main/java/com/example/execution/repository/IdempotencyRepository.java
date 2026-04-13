package com.example.execution.repository;

import com.example.execution.entity.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<Idempotency, String> {
    Optional<Idempotency> findByTxnId(String txnId);
}
