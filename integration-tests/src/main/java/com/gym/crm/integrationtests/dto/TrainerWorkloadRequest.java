package com.gym.crm.integrationtests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadRequest {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private LocalDate trainingDate;
    private Integer trainingDuration;
    private Boolean isActive;
    private ActionType actionType;

    public enum ActionType {
        ADD,
        DELETE
    }
}
