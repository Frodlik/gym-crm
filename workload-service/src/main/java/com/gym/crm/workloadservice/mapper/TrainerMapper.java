package com.gym.crm.workloadservice.mapper;

import com.gym.crm.openapi.model.MonthlyWorkload;
import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.openapi.model.TrainerWorkloadResponse;
import com.gym.crm.openapi.model.YearlyWorkload;
import com.gym.crm.workloadservice.model.Month;
import com.gym.crm.workloadservice.model.Trainer;
import com.gym.crm.workloadservice.model.Year;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    @Mapping(source = "trainerUsername", target = "username")
    @Mapping(source = "trainerFirstName", target = "firstName")
    @Mapping(source = "trainerLastName", target = "lastName")
    @Mapping(source = "isActive", target = "isActive")
    Trainer toTrainer(TrainerWorkloadRequest request);

    void updateTrainerFromRequest(TrainerWorkloadRequest request, @MappingTarget Trainer trainer);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "isActive", source = "isActive")
    TrainerWorkloadResponse toTrainerWorkloadResponse(Trainer trainer);

    @Mapping(target = "year", source = "yearNumber")
    YearlyWorkload toYearlyWorkload(Year year);

    @Mapping(target = "month", source = "monthNumber")
    @Mapping(target = "trainingSummaryDuration", source = "totalDurationMinutes")
    MonthlyWorkload toMonthlyWorkload(Month month);
}
