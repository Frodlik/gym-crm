package com.gym.crm.workloadservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.openapi.model.MonthlyWorkload;
import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.openapi.model.TrainerWorkloadResponse;
import com.gym.crm.openapi.model.YearlyWorkload;
import com.gym.crm.workloadservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WorkloadController.class)
class WorkloadControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TrainerWorkloadService service;

    @Test
    void shouldProcessAddWorkloadSuccessfully() throws Exception {
        TrainerWorkloadRequest request = createWorkloadRequest(LocalDate.of(2025, 8, 25));

        mockMvc.perform(post("/api/v1/trainers/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(service).processTrainingWorkload(any(TrainerWorkloadRequest.class));
    }

    @Test
    void shouldReturnTrainerWorkload() throws Exception {
        Mockito.when(service.getTrainerWorkload("saitama.punch"))
                .thenReturn(createTrainerWorkloadResponse());

        mockMvc.perform(get("/api/v1/trainers/workload/{username}", "saitama.punch"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("saitama.punch"))
                .andExpect(jsonPath("$.firstName").value("Saitama"))
                .andExpect(jsonPath("$.lastName").value("Punch"))
                .andExpect(jsonPath("$.years[0].year").value(2025))
                .andExpect(jsonPath("$.years[0].months[0].month").value(8))
                .andExpect(jsonPath("$.years[0].months[0].trainingSummaryDuration").value(180));
    }

    private TrainerWorkloadRequest createWorkloadRequest(LocalDate date) {
        return new TrainerWorkloadRequest(
                "saitama.punch", "Saitama", "Punch",
                date, 60, true, TrainerWorkloadRequest.ActionTypeEnum.ADD
        );
    }

    private TrainerWorkloadResponse createTrainerWorkloadResponse() {
        TrainerWorkloadResponse response = new TrainerWorkloadResponse();
        response.setUsername("saitama.punch");
        response.setFirstName("Saitama");
        response.setLastName("Punch");
        response.setIsActive(true);

        YearlyWorkload yearly = new YearlyWorkload();
        yearly.setYear(2025);

        MonthlyWorkload monthly = new MonthlyWorkload();
        monthly.setMonth(8);
        monthly.setTrainingSummaryDuration(180);

        yearly.setMonths(List.of(monthly));
        response.setYears(List.of(yearly));

        return response;
    }
}