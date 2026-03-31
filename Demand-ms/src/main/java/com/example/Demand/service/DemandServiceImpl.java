package com.example.Demand.service;

import com.example.Demand.dto.ExecutionRequest;
import com.example.Demand.entity.Demand;
import com.example.Demand.repository.DemandRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

        // 1. Save in MS1 DB
        Demand saved = repository.save(sfc);

        // 2. Prepare request for MS2
        String url = "http://localhost:8081/api/execution/reserve";

        ExecutionRequest request = new ExecutionRequest();
        request.setRouterId(sfc.getRouterId());
        request.setOperationId(sfc.getOperationId());

        // 3. Call MS2
        restTemplate.postForObject(url, request, String.class);

        return saved;
    }
}