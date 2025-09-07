package com.gym.crm.service;

import com.gym.crm.dto.trainer.TrainerWorkloadRequest;

public interface WorkloadService {
    void processTrainerWorkload(TrainerWorkloadRequest request);
}
