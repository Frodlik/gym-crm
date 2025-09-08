package com.gym.crm.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @RequestMapping("/fallback/workload")
    public ResponseEntity<String> workloadFallback() {
        return ResponseEntity.status(503)
                .body("Workload service is currently unavailable. Please try again later.");
    }

    @RequestMapping("/fallback/gym-crm")
    public ResponseEntity<String> gymCrmFallback() {
        return ResponseEntity.status(503)
                .body("Gym CRM service is currently unavailable. Please try again later.");
    }
}