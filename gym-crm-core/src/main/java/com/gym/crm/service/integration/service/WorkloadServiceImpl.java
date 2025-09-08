package com.gym.crm.service.integration.service;

import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import com.gym.crm.exception.ServiceUnavailableException;
import com.gym.crm.service.integration.client.WorkloadClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl {
    private final WorkloadClient workloadClient;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallbackProcessTrainerWorkload")
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        workloadClient.processWorkload(request);
    }

    public void fallbackProcessTrainerWorkload(TrainerWorkloadRequest request, Throwable ex) {
        throw new ServiceUnavailableException("Workload service is currently unavailable. Please try again later.", ex);
    }
}
