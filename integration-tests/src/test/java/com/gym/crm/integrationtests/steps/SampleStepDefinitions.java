package com.gym.crm.integrationtests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SampleStepDefinitions {
    private boolean isApplicationRunning;
    private String applicationStatus;

    @Given("the application is running")
    public void theApplicationIsRunning() {
        isApplicationRunning = true;
    }

    @When("I check the application status")
    public void iCheckTheApplicationStatus() {
        applicationStatus = isApplicationRunning ? "healthy" : "unhealthy";
    }

    @Then("the application should be healthy")
    public void theApplicationShouldBeHealthy() {
        assertThat(applicationStatus).isEqualTo("healthy");
    }
}
