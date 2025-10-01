package com.gym.crm.integrationtests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SampleStepDefinitions {
    private boolean applicationRunning;
    private String applicationStatus;

    @Given("the application is running")
    public void theApplicationIsRunning() {
        applicationRunning = true;
    }

    @When("I check the application status")
    public void iCheckTheApplicationStatus() {
        applicationStatus = applicationRunning ? "healthy" : "unhealthy";
    }

    @Then("the application should be healthy")
    public void theApplicationShouldBeHealthy() {
        assertThat(applicationStatus).isEqualTo("healthy");
    }
}
