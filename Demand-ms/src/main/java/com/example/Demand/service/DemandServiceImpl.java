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
        request.setSfcId(sfc.getSfcId());

        // 🟢 STEP 4: Call MS2 and get response
        try {
            String executionResponse = restTemplate.postForObject(url, request, String.class);

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