package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.steps.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GymCrmCoreStepDefinitions {
    private static final String BASE_URL = "http://localhost:8091/api/v1";
    private static final String TRAINEE_REGISTRATION_ENDPOINT = "/trainees/register";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestContext testContext;

    @When("I register new trainee with following details:")
    public void iSendPostRequestWithBody(DataTable dataTable) throws Exception {
        Map<String, String> requestMap = dataTable.asMaps(String.class, String.class).getFirst();
        String requestBody = objectMapper.writeValueAsString(requestMap);

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(BASE_URL + TRAINEE_REGISTRATION_ENDPOINT);

        testContext.setResponse(response);
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
}
