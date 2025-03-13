package com.example.forex.controller;

import com.example.forex.service.ForexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/forex")
public class ForexController {
    
    @Autowired
    private ForexService forexService;
    
    /**
     * 查詢匯率
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<?> getForexRates(@RequestBody Map<String, String> request) {
        return forexService.getForexRates(
            request.get("startDate"),
            request.get("endDate"),
            request.get("currency")
        );
    }
} 