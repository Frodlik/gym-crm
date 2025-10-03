Feature: Trainer Workload Management
  In order to manage trainers' workloads
  As an authenticated trainer
  I want to be able to add and validate training workload data

  Scenario: Successfully add training workload for trainer
    Given I am authenticated as "john.trainer"
    When I send POST request to "/api/v1/trainers/workload" with body:
      """
      {
        "trainerUsername": "john.trainer",
        "trainerFirstName": "John",
        "trainerLastName": "Trainer",
        "isActive": true,
        "trainingDate": "2024-03-15",
        "trainingDuration": 90,
        "actionType": "ADD"
      }
      """
    Then the response status should be 200

  Scenario: Fail to add workload with invalid duration
    Given I am authenticated as "jane.trainer"
    When I send POST request to "/api/v1/trainers/workload" with body:
      """
      {
        "trainerUsername": "jane.trainer",
        "firstName": "Jane",
        "lastName": "Trainer",
        "isActive": true,
        "trainingDate": "2024-03-15",
        "trainingDuration": -50,
        "actionType": "ADD"
      }
      """
    Then the response status should be 400
    And the response should contain validation error message