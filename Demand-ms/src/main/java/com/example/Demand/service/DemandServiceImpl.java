package com.example.Demand.service;

import com.example.Demand.dto.WorkflowInput;
import com.example.Demand.dto.WorkflowOutput;
import com.example.Demand.entity.Demand;
import com.example.Demand.repository.DemandRepository;
import com.example.Demand.temporal.workflow.SFCWorkflow;
import com.example.Demand.temporal.TemporalWorker;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DemandServiceImpl implements DemandService {

    private final DemandRepository repository;
    private final WorkflowClient workflowClient;

    public DemandServiceImpl(DemandRepository repository) {
        this.repository = repository;
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(
                io.temporal.serviceclient.WorkflowServiceStubsOptions.newBuilder()
                        .setTarget("localhost:7234")
                        .build()
        );
        this.workflowClient = WorkflowClient.newInstance(service);
    }

    @Override
    public Demand createSFC(Demand sfc) {

        // 🟢 STEP 1: Generate txnId as MD5 hash of sfcId
        String txnId = generateTxnId(sfc.getSfcId());

        // 🟢 STEP 2: Save in MS1 DB
        Demand saved = repository.save(sfc);

        // 🟢 STEP 3: Start Temporal workflow
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(TemporalWorker.TASK_QUEUE)
                .setWorkflowId("SFC-" + txnId + "-" + UUID.randomUUID())
                .build();

        SFCWorkflow workflow = workflowClient.newWorkflowStub(SFCWorkflow.class, options);

        // 🟢 STEP 4: Prepare input DTO
        WorkflowInput input = new WorkflowInput(
                sfc.getRouterId(),
                sfc.getOperationId(),
                sfc.getSfcId(),
                txnId
        );

        // 🟢 STEP 5: Execute workflow synchronously (wait for result)
        try {
            WorkflowOutput output = workflow.createSFC(input);

            saved.setTxnId(txnId);
            saved.setTimestamp(LocalDateTime.parse(output.getTimestamp()));
            saved.setStatus("SUCCESS");

        } catch (Exception e) {
            saved.setTxnId(txnId);
            saved.setStatus("FAILED");
        }

        return repository.save(saved);
    }

    private String generateTxnId(String sfcId) {
        try {

            long nowEpochSecond = Instant.now().getEpochSecond();
            long interval = 5;
            long roundedEpochSecond = nowEpochSecond  - (nowEpochSecond % interval);
            String idempotencyKey=sfcId+"-"+roundedEpochSecond;

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(idempotencyKey.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}