package com.gym.crm.workloadservice.service;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workloadservice.model.Trainer;
import jakarta.validation.Valid;


public interface TrainerWorkloadService {
    Trainer addTrainingWorkload(@Valid TrainerWorkloadRequest request);

    void deleteTrainingWorkload(String username);
}
