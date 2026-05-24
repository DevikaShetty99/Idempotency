package com.example.execution.controller;

import com.example.execution.entity.Execution;
import com.example.execution.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/execution")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @PostMapping("/reserve")
    public Execution reserve(@RequestBody Execution execution) {
        return executionService.reserve(execution);
    }

    /**
     * Update status of an execution by sfcId
     * Shows the impact of duplicate records when idempotency is disabled
     */
    @PutMapping("/update-status/{sfcId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable String sfcId,
            @RequestParam String newStatus) {

        int updatedCount = executionService.updateStatusBySfcId(sfcId, newStatus);

        if (updatedCount == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UpdateStatusResponse(
                sfcId,
                newStatus,
                updatedCount,
                updatedCount > 1 ? "WARNING: Multiple records updated due to duplicates!" : "OK"
        ));
    }

    /**
     * Get all executions by sfcId (to show duplicates)
     */
    @GetMapping("/by-sfc/{sfcId}")
    public List<Execution> getBySfcId(@PathVariable String sfcId) {
        return executionService.findBySfcId(sfcId);
    }

    // Response DTO
    static class UpdateStatusResponse {
        public String sfcId;
        public String newStatus;
        public int recordsUpdated;
        public String message;

        public UpdateStatusResponse(String sfcId, String newStatus, int recordsUpdated, String message) {
            this.sfcId = sfcId;
            this.newStatus = newStatus;
            this.recordsUpdated = recordsUpdated;
            this.message = message;
        }
    }
}
