Feature: Trainee Management
  In order to manage trainees
  As a gym system user
  I want to be able to register and validate trainee data

  Background:
    Given I register new trainee with following details:
      | firstName   | lastName     | dateOfBirth | address       |
      | Billy       | Herrington   | 2000-03-22  | 123 Main St   |
    Then response status should be 200
    And I extract credentials for user "billy.herrington" from registration response
    When I log in using username "billy.herrington" and password "generated"
    Then response status should be 200
    And I should be successfully logged in

  @PositiveScenario
  Scenario: Retrieve own trainee profile
    When I request trainee profile for "billy.herrington"
    Then response status should be 200
    And response should contain field "firstName" with value "Billy"
    And response should contain field "isActive" with value "true"

  @PositiveScenario
  Scenario: Update trainee profile information
    When I update trainee "billy.herrington" profile with following details:
      | firstName   | lastName     | dateOfBirth | address        |
      | Billy       | Herrington   | 2000-03-22  | 456 Freedom Rd |
    Then response status should be 200
    And response should contain field "address" with value "456 Freedom Rd"

  @PositiveScenario
  Scenario: Deactivate trainee account
    When I change activation status for trainee "billy.herrington" to false
    Then response status should be 200
    And trainee "billy.herrington" should now have isActive = false

  @PositiveScenario
  Scenario: Delete own trainee profile successfully
    When I delete trainee profile for "billy.herrington"
    Then response status should be 200
    And trainee "billy.herrington" should not exist anymore

  @NegativeScenario
  Scenario: Fail to reactivate already active trainee account
    Given trainee "billy.herrington" is inactive
    When I change activation status for trainee "billy.herrington" to true
    Then response status should be 500
    And trainee "billy.herrington" should now have isActive = true

  @NegativeScenario
  Scenario: Fail to register trainee with missing required fields
    When I register new trainee with following details:
      | firstName |
      | Billy     |
    Then I received error with next attributes:
      | field         | value                                       |
      | errorCode     | 2760                                        |
      | errorMessage  | Validation error: lastName must not be null |
    And response status should be 400