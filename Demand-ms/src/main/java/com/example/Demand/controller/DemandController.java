package com.example.Demand.controller;

import com.example.Demand.entity.Demand;
import com.example.Demand.service.DemandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sfc")
public class DemandController {

    @Autowired
    private DemandService demandService;

    @PostMapping("/create")
    public Demand create(@RequestBody Demand sfc) {
        return demandService.createSFC(sfc);
    }
}