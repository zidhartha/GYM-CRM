Feature: Training to workload integration

  Scenario: Training creation publishes JMS workload message
    Given a registered trainee with first name "Int" and last name "Test"
    And a registered trainer with first name "Coach" and last name "Jms" specializing in "Yoga"
    And the user is authenticated
    When a training is created with name "Integration Yoga" on "2025-07-01" for 90 minutes
    Then the response status should be 200
    And a workload message should have been sent to the queue

  Scenario: Failed training creation does not publish JMS message
    Given the user is authenticated as any user
    When a training is created with trainee "ghost.trainee" and name "Bad Training" on "2025-07-01" for 60 minutes
    Then the response status should be 400
    And no workload message should have been sent to the queue