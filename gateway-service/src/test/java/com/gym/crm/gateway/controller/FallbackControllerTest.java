package com.gym.crm.gateway.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(FallbackController.class)
class FallbackControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void workloadFallback_ShouldReturn503WithMessage() {
        webTestClient.get()
                .uri("/fallback/workload")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.TEXT_PLAIN + ";charset=UTF-8")
                .expectBody(String.class)
                .isEqualTo("Workload service is currently unavailable. Please try again later.");
    }

    @Test
    void gymCrmFallback_ShouldReturn503WithMessage() {
        webTestClient.get()
                .uri("/fallback/gym-crm")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.TEXT_PLAIN + ";charset=UTF-8")
                .expectBody(String.class)
                .isEqualTo("Gym CRM service is currently unavailable. Please try again later.");
    }
}