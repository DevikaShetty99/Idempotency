package com.example.Demand.service;

import com.example.Demand.dto.ExecutionRequest;
import com.example.Demand.entity.Demand;
import com.example.Demand.repository.DemandRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class DemandServiceImpl implements DemandService {

    private final DemandRepository repository;
    private final RestTemplate restTemplate;

    // Constructor Injection (BEST PRACTICE)
    public DemandServiceImpl(DemandRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Demand createSFC(Demand sfc) {

        // 🟢 STEP 1: Generate txnId as MD5 hash of sfcId
        String txnId = generateTxnId(sfc.getSfcId());

        // 🟢 STEP 2: Save in MS1 DB
        Demand saved = repository.save(sfc);

        // 🟢 STEP 3: Prepare request for MS2
        String url = "http://localhost:8081/api/execution/reserve";

        ExecutionRequest request = new ExecutionRequest();
        request.setRouterId(sfc.getRouterId());
        request.setOperationId(sfc.getOperationId());
        request.setTxnId(txnId);

        // 🟢 STEP 4: Call MS2 and get response
        try {
            String executionResponse = restTemplate.postForObject(url, request, String.class);

            // 🟢 STEP 5: Update with txnId and status based on response
            saved.setTxnId(txnId);

            if (executionResponse != null) {
                // Check if it's a duplicate or new reservation by checking response
                if (executionResponse.contains("\"status\":\"DUPLICATE\"")) {
                    saved.setStatus("DUPLICATE");
                } else {
                    saved.setStatus("SUCCESS");
                }
            } else {
                saved.setStatus("FAILED");
            }

        } catch (Exception e) {
            // Network error, timeout, or other failure
            saved.setTxnId(txnId);
            saved.setStatus("FAILED");
            System.err.println("Failed to call Execution service: " + e.getMessage());
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