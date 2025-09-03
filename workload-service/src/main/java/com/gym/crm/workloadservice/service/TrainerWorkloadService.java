package com.gym.crm.workloadservice.service;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import jakarta.validation.Valid;


public interface TrainerWorkloadService {
    void processTrainingWorkload(@Valid TrainerWorkloadRequest request);
}
