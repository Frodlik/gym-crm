Feature: Trainee profile permissions
  As an authenticated user of the Gym CRM system
  I want to access my trainee profile information only when I’m logged in

  Background:
    Given I register new trainee with following details:
      | firstName   | lastName     | dateOfBirth | address       |
      | Billy       | Herrington   | 2000-03-22  | 123 Main St   |
    Then response status should be 200
    And I extract credentials for user "billy.herrington" from registration response

  @PositiveScenario
  Scenario: Successful access to trainee profile with valid token
    When I log in using username "billy.herrington" and password "generated"
    Then response status should be 200
    And I should be successfully logged in
    When I request trainee profile for "billy.herrington"
    Then response status should be 200
    And response should contain field "firstName" with value "Billy"
    And response should contain field "lastName" with value "Herrington"

  @NegativeScenario
  Scenario: Failed access to trainee profile without authentication
    Given I am not authenticated
    When I request trainee profile for "billy.herrington"
    Then response status should be 401