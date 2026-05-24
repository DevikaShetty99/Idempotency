package com.example.execution.repository;

import com.example.execution.entity.Execution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {

    /**
     * Find all executions by sfcId
     * Used to show duplicate records when idempotency is disabled
     */
    List<Execution> findBySfcId(String sfcId);

    /**
     * Update status for all executions with given sfcId
     * Returns count of updated records
     */
    @Modifying
    @Transactional
    @Query("UPDATE Execution e SET e.status = :newStatus WHERE e.sfcId = :sfcId")
    int updateStatusBySfcId(@Param("sfcId") String sfcId, @Param("newStatus") String newStatus);
}
