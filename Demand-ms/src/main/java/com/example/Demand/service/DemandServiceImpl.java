package com.example.Demand.service;

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
                .setWorkflowId("SFC-" + txnId)
                .build();

        SFCWorkflow workflow = workflowClient.newWorkflowStub(SFCWorkflow.class, options);

        // 🟢 STEP 4: Execute workflow (Temporal handles retries)
        try {
            String executionResponse = workflow.createSFC(
                    sfc.getRouterId(),
                    sfc.getOperationId(),
                    sfc.getSfcId(),
                    txnId
            );

            saved.setTxnId(txnId);

            // Extract timestamp from response
            if (executionResponse != null && executionResponse.contains("\"timestamp\":\"")) {
                int start = executionResponse.indexOf("\"timestamp\":\"") + 13;
                int end = executionResponse.indexOf("\"", start);
                String timestampStr = executionResponse.substring(start, end);
                saved.setTimestamp(java.time.LocalDateTime.parse(timestampStr));
            }

            saved.setStatus("SUCCESS");

        } catch (Exception e) {
            saved.setTxnId(txnId);
            saved.setStatus("FAILED");
        }

        return repository.save(saved);
    }

    private String generateTxnId(String sfcId) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(sfcId.getBytes());
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