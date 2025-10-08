Feature: Trainee Management
  In order to manage trainees
  As a gym system user
  I want to be able to register and validate trainee data

  @PositiveScenario
  Scenario: Successfully register a new trainee
    When I register new trainee with following details:
      | firstName   | lastName     | dateOfBirth | address       |
      | Billy       | Herrington   | 2000-03-22  | 123 Main St   |
    Then response status should be 200
    And response should contain field "username" matching pattern "^[a-zA-Z]+\.[a-zA-Z]+(\d+)?$"
    And response should contain field "password" with minimum length 10
    And response username should start with "billy.herrington"

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