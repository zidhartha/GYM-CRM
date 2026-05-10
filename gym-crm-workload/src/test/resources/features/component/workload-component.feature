Feature: Workload REST API

  Scenario: Add workload for a trainer
    When a workload update is submitted for trainer "john.doe" with action "ADD" and duration 60 on "2025-03-15"
    Then the workload response status should be 200
    And the workload for trainer "john.doe" should show 60 minutes in month 3 of year 2025

  Scenario: Add and then subtract workload
    When a workload update is submitted for trainer "jane.smith" with action "ADD" and duration 120 on "2025-06-10"
    And a workload update is submitted for trainer "jane.smith" with action "DELETE" and duration 30 on "2025-06-10"
    Then the workload for trainer "jane.smith" should show 90 minutes in month 6 of year 2025

  Scenario: Delete workload for non-existent trainer does nothing
    When a workload update is submitted for trainer "nobody" with action "DELETE" and duration 60 on "2025-01-01"
    Then the workload response status should be 200

  Scenario: Get workload for non-existent trainer
    When the workload summary is requested for trainer "ghost.trainer"
    Then the workload response status should be 404

  Scenario: Submit workload with missing required fields
    When a workload update is submitted with missing trainer username
    Then the workload response status should be 400