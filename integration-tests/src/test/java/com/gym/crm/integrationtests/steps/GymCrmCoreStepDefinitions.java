package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.steps.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GymCrmCoreStepDefinitions {
    private static final String BASE_URL = "http://localhost:8091/api/v1";
    private static final String TRAINEE_REGISTRATION_ENDPOINT = "/trainees/register";
    private static final String TRAINER_REGISTRATION_ENDPOINT = "/trainers/register";
    private static final String TRAINING_ENDPOINT = "/trainings";

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
                .header("Cookie", "access-token=" + testContext.getAuthToken())
                .body(objectMapper.writeValueAsString(requestBody))
                .post(BASE_URL + TRAINING_ENDPOINT);

        testContext.setResponse(response);
    }

    @And("I wait {int} seconds for JMS message processing")
    public void iWaitSecondsForJMSMessageProcessing(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L);
    }

    @And("workload should contain duration {int} minutes")
    public void workloadShouldContainDurationMinutes(int expectedDuration) {
        Response response = testContext.getResponse();
        assertNotNull(response);

        JsonPath jsonPath = response.jsonPath();
        Integer actualDuration = jsonPath.getInt("years[0].months[0].trainingSummaryDuration");

        assertNotNull(actualDuration);
        assertEquals(expectedDuration, actualDuration);
    }

    @Then("response should contain field {string} matching pattern {string}")
    public void theResponseShouldContainFieldMatchingPattern(String fieldName, String pattern) throws Exception {
        String body = testContext.getResponse().getBody().asString();
        assertNotNull(body);

        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);
        String fieldValue = (String) responseBody.get(fieldName);

        assertNotNull(fieldValue);
        assertTrue(fieldValue.matches(pattern));
    }

    @Then("response should contain field {string} with minimum length {int}")
    public void theResponseShouldContainFieldWithMinimumLength(String fieldName, int minLength) throws Exception {
        String body = testContext.getResponse().getBody().asString();
        assertNotNull(body);

        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);
        String fieldValue = (String) responseBody.get(fieldName);

        assertNotNull(fieldValue);
        assertTrue(fieldValue.length() >= minLength);
    }

    @Then("response username should start with {string}")
    public void theResponseUsernameShouldStartWith(String expectedPrefix) throws Exception {
        String body = testContext.getResponse().getBody().asString();
        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);
        String username = (String) responseBody.get("username");

        assertNotNull(username);
        assertTrue(username.startsWith(expectedPrefix));
    }

    @When("I request trainee profile for {string}")
    public void iRequestTraineeProfileFor(String username) {
        String endpoint = String.format("/trainees/%s", username);

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .header("Cookie", "access-token=" + testContext.getAuthToken())
                .get(BASE_URL + endpoint);

        testContext.setResponse(response);
    }

    @Then("response should contain field {string} with value {string}")
    public void responseShouldContainFieldWithValue(String field, String expectedValue) throws Exception {
        String body = testContext.getResponse().getBody().asString();
        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);

        assertTrue(responseBody.containsKey(field));
        assertEquals(expectedValue, responseBody.get(field));
    }
}
