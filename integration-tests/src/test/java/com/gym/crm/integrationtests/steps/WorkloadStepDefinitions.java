package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.util.JwtTokenGenerator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class WorkloadStepDefinitions {
    private static final String BASE_URL = "http://localhost:8086";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;
    @Autowired
    private ObjectMapper objectMapper;

    private Response response;
    private String authToken;

    @Given("I am authenticated as {string}")
    public void iAmAuthenticatedAs(String username) {
        authToken = jwtTokenGenerator.generateValidToken(username, "ROLE_TRAINER");
    }

    @When("I send POST request to {string} with body:")
    public void iSendPostRequestWithBody(String endpoint, DataTable dataTable) throws Exception {
        Map<String, String> tableMap = dataTable.asMap(String.class, String.class);

        String requestBody = objectMapper.writeValueAsString(tableMap);

        response = RestAssured
                .given()
                .header(HEADER_AUTHORIZATION, BEARER_PREFIX + authToken)
                .contentType("application/json")
                .body(requestBody)
                .post(BASE_URL + endpoint);
    }

    @When("I send GET request to {string}")
    public void iSendGetRequest(String endpoint) {
        response = RestAssured
                .given()
                .header(HEADER_AUTHORIZATION, BEARER_PREFIX + authToken)
                .get(BASE_URL + endpoint);
    }

    @Then("response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        response.then().statusCode(expectedStatus);
    }

    @Then("response should contain trainer {string} with workload data")
    public void theResponseShouldContainTrainerWithWorkloadData(String username) throws Exception {
        String body = response.getBody().asString();
        assertNotNull(body);

        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);
        assertEquals(username, responseBody.get("username"));
        assertNotNull(responseBody.get("firstName"));
        assertNotNull(responseBody.get("lastName"));
        assertNotNull(responseBody.get("years"));
    }

    @Then("trainer workload duration should be {int}")
    public void theTrainerWorkloadDurationShouldBe(int expectedDuration) throws Exception {
        String body = response.getBody().asString();
        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);

        assertEquals(expectedDuration, responseBody.get("trainingDuration"));
    }

    @Then("response should contain field {string} with value {string}")
    public void theResponseShouldContainValidationErrorMessage(String fieldName, String expectedValue) {
        String body = response.getBody().asString();

        assertNotNull(body);
        assertTrue(body.contains(fieldName));
        assertTrue(body.contains(expectedValue));
    }

    @Then("I received error with next attributes:")
    public void iReceivedErrorWithNextAttributes(DataTable dataTable) throws Exception {
        Map<String, String> expectedAttributes = dataTable.asMap(String.class, String.class);

        String body = response.getBody().asString();
        assertNotNull(body);

        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);

        assertEquals(expectedAttributes.get("errorCode"), String.valueOf(responseBody.get("errorCode")));
        assertEquals(expectedAttributes.get("errorMessage"), String.valueOf(responseBody.get("errorMessage")));
    }
}
