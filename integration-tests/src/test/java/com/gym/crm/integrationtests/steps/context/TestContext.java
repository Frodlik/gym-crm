package com.gym.crm.integrationtests.steps.context;

import io.restassured.response.Response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TestContext {
    private Response response;
    private String username;
    private String password;
    private String authToken;
}
