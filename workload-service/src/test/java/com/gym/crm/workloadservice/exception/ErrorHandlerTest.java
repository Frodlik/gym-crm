package com.gym.crm.workloadservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.workloadservice.config.TestSecurityConfig;
import com.gym.crm.workloadservice.controller.WorkloadController;
import com.gym.crm.workloadservice.service.TrainerWorkloadService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WorkloadController.class)
@Import(TestSecurityConfig.class)
class ErrorHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TrainerWorkloadService trainerWorkloadService;

    @Test
    void shouldReturnNotFoundError() throws Exception {
        Mockito.when(trainerWorkloadService.getTrainerWorkload(Mockito.anyString()))
                .thenThrow(new ResourceNotFoundException("Trainer not found with username: saitama.punch"));

        mockMvc.perform(get("/api/v1/trainers/workload/{username}", "saitama.punch"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(2835))
                .andExpect(jsonPath("$.errorMessage", containsString("Trainer not found with username")));
    }

    @Test
    void shouldReturnInvalidRequestOnNullPointer() throws Exception {
        Mockito.when(trainerWorkloadService.getTrainerWorkload(Mockito.anyString()))
                .thenThrow(new NullPointerException("Null trainer"));

        mockMvc.perform(get("/api/v1/trainers/workload/{username}", "null.trainer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(2400))
                .andExpect(jsonPath("$.errorMessage").value("Invalid or malformed request data"));
    }

    @Test
    void shouldReturnValidationErrorOnConstraintViolation() throws Exception {
        ConstraintViolationException cve = new ConstraintViolationException("Validation failed: trainerUsername", null);
        Mockito.when(trainerWorkloadService.getTrainerWorkload(Mockito.anyString()))
                .thenThrow(cve);

        mockMvc.perform(get("/api/v1/trainers/workload/{username}", "wrong.format"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(2760))
                .andExpect(jsonPath("$.errorMessage", containsString("Validation error")));
    }

    @Test
    void shouldReturnServerErrorOnUnhandledException() throws Exception {
        Mockito.when(trainerWorkloadService.getTrainerWorkload(Mockito.anyString()))
                .thenThrow(new RuntimeException("Something broke"));

        mockMvc.perform(get("/api/v1/trainers/workload/{username}", "oops"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value(3200))
                .andExpect(jsonPath("$.errorMessage").value("Internal processing error"));
    }
}