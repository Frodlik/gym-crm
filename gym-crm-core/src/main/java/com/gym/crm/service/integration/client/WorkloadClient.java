package com.gym.crm.service.integration.client;

import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "workload-service")
public interface WorkloadClient {

    @PostMapping("/api/v1/trainers/workload")
    void processWorkload(@RequestBody TrainerWorkloadRequest request);
}
