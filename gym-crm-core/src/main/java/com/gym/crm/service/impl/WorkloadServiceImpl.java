package com.gym.crm.service.impl;

import com.gym.crm.client.WorkloadClient;
import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import com.gym.crm.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final WorkloadClient workloadClient;

    @Override
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        workloadClient.processWorkload(request);
    }
}
