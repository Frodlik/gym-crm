package com.gym.crm.workloadservice.service;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.openapi.model.TrainerWorkloadResponse;
import jakarta.validation.Valid;


public interface TrainerWorkloadService {
    void processTrainingWorkload(@Valid TrainerWorkloadRequest request);

    TrainerWorkloadResponse getTrainerWorkload(String username);
}
