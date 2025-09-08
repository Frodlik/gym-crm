package com.gym.crm.service.integration.service;

import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import com.gym.crm.service.integration.client.WorkloadClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl {
    private final WorkloadClient workloadClient;

    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        workloadClient.processWorkload(request);
    }
}
