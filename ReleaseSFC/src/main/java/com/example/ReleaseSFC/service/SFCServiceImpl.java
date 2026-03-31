package com.example.ReleaseSFC.service;

import com.example.ReleaseSFC.dto.InventoryRequest;
import com.example.ReleaseSFC.entity.SFC;
import com.example.ReleaseSFC.repository.SFCRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SFCServiceImpl implements SFCService {

    private final SFCRepository repository;
    private final RestTemplate restTemplate;

    // Constructor Injection (BEST PRACTICE)
    public SFCServiceImpl(SFCRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Override
    public SFC createSFC(SFC sfc) {

        // 1. Save in MS1 DB
        SFC saved = repository.save(sfc);

        // 2. Prepare request for MS2
        String url = "http://localhost:8081/api/inventory/reserve";

        InventoryRequest request = new InventoryRequest();
        request.setRouterId(sfc.getRouterId());
        request.setOperationId(sfc.getOperationId());

        // 3. Call MS2
        restTemplate.postForObject(url, request, String.class);

        return saved;
    }
}