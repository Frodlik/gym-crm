package com.gym.crm.workloadservice.mapper;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workloadservice.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "years", ignore = true)
    Trainer toTrainer(TrainerWorkloadRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "years", ignore = true)
    void updateTrainerFromRequest(TrainerWorkloadRequest request, @MappingTarget Trainer trainer);
}
