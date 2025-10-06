Feature: User registration and login
  As a new user of the Gym CRM system,
  I want to register and log in using the credentials I receive after registration

  Scenario: Successful login using generated credentials
    Given I register new trainee with following details:
      | firstName   | lastName     | dateOfBirth | address       |
      | Billy       | Herrington   | 2000-03-22  | 123 Main St   |
    Then response status should be 200
    And I extract credentials for user "billy.herrington" from registration response
    When I log in using username "billy.herrington" and password "generated"
    Then response status should be 200
    And I should be successfully logged in

  Scenario: Failed login attempt with invalid password
    Given I register new trainee with following details:
      | firstName   | lastName     | dateOfBirth | address       |
      | Billy       | Herrington   | 2000-03-22  | 123 Main St   |
    Then response status should be 200
    And I extract credentials for user "billy.herrington" from registration response
    When I log in using username "billy.herrington" and password "wrongpass123"
    Then I received error with next attributes:
      | field         | value                |
      | errorCode     | 2805                 |
      | errorMessage  | Authentication fails |
    And response status should be 401
