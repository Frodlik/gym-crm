package com.gym.crm.integrationtests;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = CucumberIntegrationTestsApplication.class)
public class CucumberSpringConfig {
}
