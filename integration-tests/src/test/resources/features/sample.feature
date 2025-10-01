Feature: Sample Integration Test
  As a developer
  I want to verify the application starts correctly
  So that I can ensure the basic setup works

  Scenario: Application context loads successfully
    Given the application is running
    When I check the application status
    Then the application should be healthy