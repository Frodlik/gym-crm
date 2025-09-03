package com.gym.crm.workloadservice.service.impl;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workloadservice.exception.ResourceNotFoundException;
import com.gym.crm.workloadservice.mapper.TrainerMapper;
import com.gym.crm.workloadservice.model.Month;
import com.gym.crm.workloadservice.model.Trainer;
import com.gym.crm.workloadservice.model.Year;
import com.gym.crm.workloadservice.repository.TrainerRepository;
import com.gym.crm.workloadservice.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
@Validated
@Transactional
@RequiredArgsConstructor
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer addTrainingWorkload(@Valid TrainerWorkloadRequest request) {
        log.info("Processing workload for trainer: {}, action: {}", request.getTrainerUsername(), request.getActionType());

        Trainer trainer = findOrCreateTrainer(request);
        Year year = findOrCreateYear(trainer, request.getTrainingDate().getYear());
        Month month = findOrCreateMonth(year, request.getTrainingDate().getMonthValue());

        processWorkloadAction(month, request);

        return trainerRepository.save(trainer);
    }

    @Override
    public void deleteTrainingWorkload(String username) {
        trainerRepository.findByUsername(username).ifPresent(trainer -> {
            trainer.getYears().clear();
            trainerRepository.save(trainer);
        });
    }

    private Trainer findOrCreateTrainer(TrainerWorkloadRequest request) {
        Optional<Trainer> optional = trainerRepository.findByUsername(request.getTrainerUsername());

        if (optional.isPresent()) {
            Trainer trainer = optional.get();
            trainerMapper.updateTrainerFromRequest(request, trainer);
            return trainer;
        }

        return trainerMapper.toTrainer(request);
    }

    private Year findOrCreateYear(Trainer trainer, int yearNumber) {
        return trainer.getYears().stream()
                .filter(y -> y.getYearNumber() == yearNumber)
                .findFirst()
                .orElseGet(() -> addYearToTrainer(trainer, yearNumber));
    }

    private Year addYearToTrainer(Trainer trainer, int yearNumber) {
        Year newYear = Year.builder()
                .yearNumber(yearNumber)
                .trainer(trainer)
                .months(new ArrayList<>())
                .build();
        trainer.getYears().add(newYear);

        return newYear;
    }

    private Month findOrCreateMonth(Year year, int monthNumber) {
        return year.getMonths().stream()
                .filter(m -> m.getMonthNumber() == monthNumber)
                .findFirst()
                .orElseGet(() -> addMonthToYear(year, monthNumber));
    }

    private Month addMonthToYear(Year year, int monthNumber) {
        Month newMonth = Month.builder()
                .monthNumber(monthNumber)
                .totalDurationMinutes(0)
                .year(year)
                .build();
        year.getMonths().add(newMonth);

        return newMonth;
    }

    private void processWorkloadAction(Month month, TrainerWorkloadRequest request) {
        int current = month.getTotalDurationMinutes();
        int change = request.getTrainingDuration();

        switch (request.getActionType()) {
            case ADD -> month.setTotalDurationMinutes(current + change);
            case DELETE -> month.setTotalDurationMinutes(Math.max(0, current - change));
            default -> throw new ResourceNotFoundException("Unknown action type: " + request.getActionType());
        }
    }
}
