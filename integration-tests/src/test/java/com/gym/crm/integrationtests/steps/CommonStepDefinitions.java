package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.steps.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonStepDefinitions {
    @Autowired
    private TestContext testContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Given("I am not authenticated")
    public void iAmNotAuthenticated() {
        testContext.setAuthToken(null);
    }

    @Then("response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        testContext.getResponse().then().statusCode(expectedStatus);
    }

    @And("I wait {int} seconds for JMS message processing")
    public void iWaitSecondsForJMSMessageProcessing(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L);
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

    @Then("response should contain field {string} with value {string}")
    public void responseShouldContainFieldWithValue(String field, String expectedValue) throws Exception {
        String body = testContext.getResponse().getBody().asString();
        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);

        assertTrue(responseBody.containsKey(field));
        assertEquals(expectedValue, responseBody.get(field).toString());
    }

    @Then("I received error with next attributes:")
    public void iReceivedErrorWithNextAttributes(DataTable dataTable) throws Exception {
        Map<String, String> expectedAttributes = dataTable.asMap(String.class, String.class);
        String body = testContext.getResponse().getBody().asString();
        assertNotNull(body);

        Map<String, Object> responseBody = objectMapper.readValue(body, Map.class);
        assertEquals(expectedAttributes.get("errorCode"), String.valueOf(responseBody.get("errorCode")));
        assertEquals(expectedAttributes.get("errorMessage"), String.valueOf(responseBody.get("errorMessage")));
    }

    @Given("specialization {string} exists")
    public void specializationExists(String specializationName) {
        jdbcTemplate.update("INSERT INTO training_types (training_type_name) VALUES (?)", specializationName);
    }

    @And("ActiveMQ container is stopped")
    public void stopActiveMQContainer() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "pause", "activemq-test"
        );
        Process process = pb.start();
        process.waitFor();
    }

    @And("ActiveMQ container is running")
    public void runActiveMQContainer() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "unpause", "activemq-test"
        );
        Process process = pb.start();
        process.waitFor();
    }
}
