package com.gym.crm.workloadservice.controller;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.openapi.model.TrainerWorkloadResponse;
import com.gym.crm.workloadservice.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gym.crm.workloadservice.controller.ApiConstant.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/trainers")
@RequiredArgsConstructor
public class WorkloadController {
    private final TrainerWorkloadService trainerWorkloadService;

    @PostMapping("/workload")
    public ResponseEntity<Void> addOrDeleteTrainerWorkload(@RequestBody TrainerWorkloadRequest request) {
        trainerWorkloadService.processTrainingWorkload(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/workload/{username}")
    public ResponseEntity<TrainerWorkloadResponse> getTrainerWorkload(@PathVariable String username) {
        TrainerWorkloadResponse response = trainerWorkloadService.getTrainerWorkload(username);

        return ResponseEntity.ok(response);
    }
}
