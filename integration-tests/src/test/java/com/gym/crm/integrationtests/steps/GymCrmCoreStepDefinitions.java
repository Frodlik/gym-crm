package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.steps.context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @And("I extract credentials for user {string} from registration response")
    public void iExtractCredentialsFromRegistrationResponse(String expectedUsername) throws Exception {
        Response response = testContext.getResponse();
        Map<String, Object> body = objectMapper.readValue(response.getBody().asString(), Map.class);

        String username = (String) body.get("username");
        String password = (String) body.get("password");

        assertEquals(expectedUsername, username);
        assertNotNull(password);

        testContext.setUsername(username);
        testContext.setPassword(password);
    }

    @When("I log in using username {string} and password {string}")
    public void iLogInUsingUsernameAndPassword(String username, String password) throws Exception {
        if (password.equalsIgnoreCase("generated")) {
            password = testContext.getPassword();
        }

        Map<String, String> credentials = Map.of(
                "username", username,
                "password", password
        );

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(credentials))
                .post(BASE_URL + "/auth/login");

        testContext.setResponse(response);
    }

    @And("I should be successfully logged in")
    public void iShouldBeSuccessfullyLoggedIn() {
        Response response = testContext.getResponse();
        assertNotNull(response);

        Map<String, String> cookies = response.getCookies();
        assertNotNull(cookies);
        assertFalse(cookies.isEmpty());

        String token = cookies.get("access-token");

        assertNotNull(token);
        assertFalse(token.isEmpty());

        testContext.setAuthToken(token);
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

    @When("I request trainee profile for {string} without authentication")
    public void iRequestTraineeProfileForWithoutAuth(String username) {
        String endpoint = String.format("/trainees/%s", username);

        Response response = RestAssured
                .given()
                .contentType("application/json")
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
