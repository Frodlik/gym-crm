package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.steps.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
