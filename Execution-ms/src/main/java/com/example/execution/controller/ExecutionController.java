package com.example.execution.controller;

import com.example.execution.entity.Execution;
import com.example.execution.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/execution")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @PostMapping("/reserve")
    public Execution reserve(@RequestBody Execution execution) {
        return executionService.reserve(execution);
    }
}