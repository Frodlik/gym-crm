Feature: Trainer Workload Management
  In order to manage trainers' workloads
  As an authenticated trainer
  I want to be able to add and validate training workload data

  Scenario: Successfully add training workload and retrieve trainer data
    Given I am authenticated as "kiyotaka.ayanokoji"
    When I send POST request to "/api/v1/trainers/workload" with body:
      | field             | value                 |
      | trainerUsername   | kiyotaka.ayanokoji    |
      | trainerFirstName  | Kiyotaka              |
      | trainerLastName   | Ayanokoji             |
      | isActive          | true                  |
      | trainingDate      | 2024-03-15            |
      | trainingDuration  | 90                    |
      | actionType        | ADD                   |
    Then response status should be 200
    When I send GET request to "/api/v1/trainers/workload/kiyotaka.ayanokoji"
    Then response status should be 200
    And response should contain trainer "kiyotaka.ayanokoji" with workload data

  Scenario: Successfully delete training workload for trainer
    Given I am authenticated as "kiyotaka.ayanokoji"
    When I send POST request to "/api/v1/trainers/workload" with body:
      | field             | value                 |
      | trainerUsername   | kiyotaka.ayanokoji    |
      | trainerFirstName  | Kiyotaka              |
      | trainerLastName   | Ayanokoji             |
      | isActive          | true                  |
      | trainingDate      | 2024-03-15            |
      | trainingDuration  | 90                    |
      | actionType        | DELETE                |
    Then response status should be 200

  Scenario: Fail to add workload with invalid duration
    Given I am authenticated as "jane.trainer"
    When I send POST request to "/api/v1/trainers/workload" with body:
      | field             | value           |
      | trainerUsername   | jane.trainer    |
      | trainerFirstName  | Jane            |
      | trainerLastName   | Trainer         |
      | isActive          | true            |
      | trainingDate      | 2024-03-15      |
      | trainingDuration  | -50             |
      | actionType        | ADD             |
    Then I received error with next attributes:
      | field         | value                                                |
      | errorCode     | 2760                                                 |
      | errorMessage  | Validation error: must be greater than or equal to 1 |
    And response status should be 400
