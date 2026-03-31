package com.example.ReleaseSFC.controller;

import com.example.ReleaseSFC.entity.SFC;
import com.example.ReleaseSFC.service.SFCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sfc")
public class SFCController {

    @Autowired
    private SFCService sfcService;

    @PostMapping("/create")
    public SFC create(@RequestBody SFC sfc) {
        return sfcService.createSFC(sfc);
    }
}