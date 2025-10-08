package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.steps.context.TestContext;
import com.gym.crm.integrationtests.util.JwtTokenGenerator;
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

public class WorkloadStepDefinitions {
    private static final String BASE_URL = "http://localhost:8086/api/v1";
    private static final String TRAINER_REGISTRATION_ENDPOINT = "/trainers/workload";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestContext testContext;

    @Given("I am authenticated as {string}")
    public void iAmAuthenticatedAs(String username) {
        String authToken = jwtTokenGenerator.generateValidToken(username, "ROLE_TRAINER");
        testContext.setAuthToken(authToken);
    }

    @When("I create new trainer workload with following details:")
    public void iSendPostRequestWithBody(DataTable dataTable) throws Exception {
        Map<String, String> tableMap = dataTable.asMap(String.class, String.class);
        String requestBody = objectMapper.writeValueAsString(tableMap);

        Response response = RestAssured
                .given()
                .header(HEADER_AUTHORIZATION, BEARER_PREFIX + testContext.getAuthToken())
                .contentType("application/json")
                .body(requestBody)
                .post(BASE_URL + TRAINER_REGISTRATION_ENDPOINT);

        testContext.setResponse(response);
    }

    @When("I retrieve workload data for trainer {string}")
    public void iSendGetRequest(String username) {
        String endpoint = String.format("/trainers/workload/%s", username);

        Response response = RestAssured
                .given()
                .header(HEADER_AUTHORIZATION, BEARER_PREFIX + testContext.getAuthToken())
                .get(BASE_URL + endpoint);

        testContext.setResponse(response);
    }

    @Then("response should contain trainer {string} with workload data")
    public void theResponseShouldContainTrainerWithWorkloadData(String username) throws Exception {
        String body = testContext.getResponse().getBody().asString();
        assertNotNull(body);

        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);

        assertEquals(username, responseBody.get("username"));
        assertNotNull(responseBody.get("firstName"));
        assertNotNull(responseBody.get("lastName"));
        assertNotNull(responseBody.get("years"));
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
}
