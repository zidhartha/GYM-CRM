Feature: JMS workload message processing

  Scenario: JMS message triggers workload update in MongoDB
    When a JMS workload message is sent for trainer "jms.trainer" with action "ADD" and duration 75 on "2025-04-10"
    And the message is processed
    Then the workload for trainer "jms.trainer" should show 75 minutes in month 4 of year 2025

  Scenario: Invalid JMS message does not create workload record
    When an invalid JMS workload message is sent with blank trainer username
    And the message is processed
    Then no workload record should exist for trainer ""