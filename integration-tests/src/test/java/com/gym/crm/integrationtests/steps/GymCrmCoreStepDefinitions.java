package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.steps.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GymCrmCoreStepDefinitions {
    private static final String BASE_URL = "http://localhost:8091/api/v1";
    private static final String TRAINEE_REGISTRATION_ENDPOINT = "/trainees/register";
    private static final String TRAINER_REGISTRATION_ENDPOINT = "/trainers/register";
    private static final String TRAINEE_ENDPOINT = "/trainees";
    private static final String TRAINING_ENDPOINT = "/trainings";
    private static final String ACTIVATION_STATUS_ENDPOINT = "/change-activation-status";
    private static final String ACCESS_TOKEN_COOKIE = "access-token=";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestContext testContext;

    @When("I register new trainee with following details:")
    public void iRegisterNewTraineeWithFollowingDetails(DataTable dataTable) throws Exception {
        Map<String, String> requestMap = dataTable.asMaps(String.class, String.class).getFirst();
        String requestBody = objectMapper.writeValueAsString(requestMap);

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(BASE_URL + TRAINEE_REGISTRATION_ENDPOINT);

        testContext.setResponse(response);
    }

    @When("I update trainee {string} profile with following details:")
    public void iUpdateTraineeProfileWithFollowingDetails(String username, DataTable dataTable) throws Exception {
        Map<String, String> updateMap = dataTable.asMaps(String.class, String.class).getFirst();

        Map<String, Object> requestBody = new HashMap<>();
        putIfValid(requestBody, "firstName", updateMap.get("firstName"));
        putIfValid(requestBody, "lastName", updateMap.get("lastName"));
        putIfValid(requestBody, "dateOfBirth", updateMap.get("dateOfBirth"));
        putIfValid(requestBody, "address", updateMap.get("address"));

        requestBody.put("isActive", Boolean.parseBoolean(updateMap.getOrDefault("isActive", "true")));

        String endpoint = String.format("%s/%s", TRAINEE_ENDPOINT, username);

        Response response = RestAssured.given()
                .contentType("application/json")
                .header("Cookie", ACCESS_TOKEN_COOKIE + testContext.getAuthToken())
                .body(objectMapper.writeValueAsString(requestBody))
                .put(BASE_URL + endpoint);

        testContext.setResponse(response);
    }

    @When("I change activation status for trainee {string} to {word}")
    public void iChangeActivationStatusForTrainee(String username, String status) {
        boolean isActive = Boolean.parseBoolean(status);
        String endpoint = String.format("%s/%s%s", TRAINEE_ENDPOINT, username, ACTIVATION_STATUS_ENDPOINT);

        Map<String, Object> requestBody = Map.of("isActive", isActive);

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .header("Cookie", ACCESS_TOKEN_COOKIE + testContext.getAuthToken())
                .body(requestBody)
                .patch(BASE_URL + endpoint);

        testContext.setResponse(response);
    }

    @Then("trainee {string} should now have isActive = {word}")
    public void traineeShouldNowHaveIsActive(String username, String expectedStatus) throws Exception {
        boolean expectedIsActive = Boolean.parseBoolean(expectedStatus);

        String endpoint = String.format("%s/%s", TRAINEE_ENDPOINT, username);
        Response response = RestAssured
                .given()
                .contentType("application/json")
                .header("Cookie", ACCESS_TOKEN_COOKIE + testContext.getAuthToken())
                .get(BASE_URL + endpoint);

        Map<String, Object> responseBody = objectMapper.readValue(response.getBody().asString(), Map.class);
        assertEquals(expectedIsActive, responseBody.get("isActive"));
    }

    @Given("trainee {string} is inactive")
    public void traineeIsInactive(String username) {
        String endpoint = String.format("%s/%s?isActive=false", TRAINEE_ENDPOINT, username);

        RestAssured
                .given()
                .contentType("application/json")
                .header("Cookie", ACCESS_TOKEN_COOKIE + testContext.getAuthToken())
                .patch(BASE_URL + endpoint);
    }

    @When("I delete trainee profile for {string}")
    public void iDeleteTraineeProfileFor(String username) {
        String endpoint = String.format("%s/%s", TRAINEE_ENDPOINT, username);

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .header("Cookie", ACCESS_TOKEN_COOKIE + testContext.getAuthToken())
                .delete(BASE_URL + endpoint);

        testContext.setResponse(response);
    }

    @Then("trainee {string} should not exist anymore")
    public void traineeShouldNotExistAnymore(String username) {
        String endpoint = String.format("%s/%s", TRAINEE_ENDPOINT, username);

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .header("Cookie", ACCESS_TOKEN_COOKIE + testContext.getAuthToken())
                .get(BASE_URL + endpoint);

        assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 401);
    }

    @Given("I register new trainer with following details:")
    public void iRegisterNewTrainerWithFollowingDetails(DataTable dataTable) throws Exception {
        Map<String, String> requestMap = dataTable.asMaps(String.class, String.class).getFirst();
        String requestBody = objectMapper.writeValueAsString(requestMap);

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(BASE_URL + TRAINER_REGISTRATION_ENDPOINT);

        testContext.setResponse(response);
    }

    @When("I create new training with following details:")
    public void iCreateNewTrainingWithFollowingDetails(DataTable dataTable) throws Exception {
        Map<String, String> requestMap = dataTable.asMap(String.class, String.class);

        Map<String, Object> requestBody = Map.of(
                "traineeUsername", requestMap.get("traineeUsername"),
                "trainerUsername", requestMap.get("trainerUsername"),
                "trainingName", requestMap.get("trainingName"),
                "trainingDate", requestMap.get("trainingDate"),
                "trainingDuration", Integer.parseInt(requestMap.get("trainingDuration"))
        );

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .header("Cookie", ACCESS_TOKEN_COOKIE + testContext.getAuthToken())
                .body(objectMapper.writeValueAsString(requestBody))
                .post(BASE_URL + TRAINING_ENDPOINT);

        testContext.setResponse(response);
    }

    @When("I request trainee profile for {string}")
    public void iRequestTraineeProfileFor(String username) {
        String endpoint = String.format("/trainees/%s", username);

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .header("Cookie", ACCESS_TOKEN_COOKIE + testContext.getAuthToken())
                .get(BASE_URL + endpoint);

        testContext.setResponse(response);
    }

    private void putIfValid(Map<String, Object> target, String key, String value) {
        if (value != null && !value.isBlank() && !value.equals("[empty]")) {
            target.put(key, value);
        }
    }
}
