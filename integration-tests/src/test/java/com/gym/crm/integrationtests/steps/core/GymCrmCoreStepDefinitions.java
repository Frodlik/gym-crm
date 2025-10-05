package com.gym.crm.integrationtests.steps.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.config.TestConfig;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CucumberContextConfiguration
@SpringBootTest(classes = TestConfig.class)
public class GymCrmCoreStepDefinitions {
    private static final String BASE_URL = "http://localhost:8091";

    @Autowired
    private ObjectMapper objectMapper;

    private Response response;

    @When("I send POST request to {string} with body:")
    public void iSendPostRequestWithBody(String endpoint, DataTable dataTable) throws Exception {
        Map<String, String> requestMap = dataTable.asMaps(String.class, String.class).getFirst();

        String requestBody = objectMapper.writeValueAsString(requestMap);

        response = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(BASE_URL + endpoint);
    }

    @Then("response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        response.then().statusCode(expectedStatus);
    }

    @Then("response should contain field {string} matching pattern {string}")
    public void theResponseShouldContainFieldMatchingPattern(String fieldName, String pattern) throws Exception {
        String body = response.getBody().asString();
        assertNotNull(body);

        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);
        String fieldValue = (String) responseBody.get(fieldName);

        assertNotNull(fieldValue);
        assertTrue(fieldValue.matches(pattern));
    }

    @Then("response should contain field {string} with minimum length {int}")
    public void theResponseShouldContainFieldWithMinimumLength(String fieldName, int minLength) throws Exception {
        String body = response.getBody().asString();
        assertNotNull(body);

        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);
        String fieldValue = (String) responseBody.get(fieldName);

        assertNotNull(fieldValue);
        assertTrue(fieldValue.length() >= minLength);
    }

    @Then("response username should start with {string}")
    public void theResponseUsernameShouldStartWith(String expectedPrefix) throws Exception {
        String body = response.getBody().asString();
        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);
        String username = (String) responseBody.get("username");

        assertNotNull(username);
        assertTrue(username.startsWith(expectedPrefix));
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
