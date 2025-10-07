Feature: Training and Workload Service Integration
  In order to track trainer workloads automatically
  As a gym system
  I want to ensure training creation triggers workload updates in MongoDB

  Background:
    Given I register new trainee with following details:
      | firstName   | lastName     | dateOfBirth | address       |
      | John        | Trainee      | 1995-05-15  | 456 Gym St    |
    And I register new trainer with following details:
      | firstName   | lastName     | specialization |
      | Jane        | Trainer      | FITNESS        |
    And I extract credentials for user "john.trainee" from registration response
    And I extract credentials for user "jane.trainer" from registration response
    And I am authenticated as "jane.trainer"

  Scenario: Successfully create training and verify workload in MongoDB
    When I create new training with following details:
      | field             | value          |
      | traineeUsername   | john.trainee   |
      | trainerUsername   | jane.trainer   |
      | trainingName      | FITNESS        |
      | trainingDate      | 2025-10-15     |
      | trainingDuration  | 60             |
    Then response status should be 200
    And I wait 3 seconds for JMS message processing
    When I retrieve workload data for trainer "jane.trainer"
    Then response status should be 200
    And response should contain trainer "jane.trainer" with workload data
    And workload should contain year 2025 with month 10 and duration 60 minutes