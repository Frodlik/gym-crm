package com.gym.crm.integrationtests.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integrationtests.steps.context.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthStepDefinitions {
    private static final String BASE_URL = "http://localhost:8091/api/v1";
    private static final String LOGIN_ENDPOINT = "/auth/login";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestContext testContext;

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
                .post(BASE_URL + LOGIN_ENDPOINT);

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
}
