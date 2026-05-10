Feature: Trainee management

  Scenario: Create trainee profile successfully
    When a trainee is registered with first name "John" and last name "Doe"
    Then the response status should be 201
    And the response should contain a username and password

  Scenario: Fail to create trainee profile due to invalid first name
    When a trainee is registered with first name "" and last name "Doe"
    Then the response status should be 400

  Scenario: Fail to create trainee profile due to invalid last name
    When a trainee is registered with first name "John" and last name ""
    Then the response status should be 400

  Scenario: Get trainee profile successfully
    Given a registered trainee with first name "Jane" and last name "Smith"
    And the user is authenticated
    When the trainee profile is requested
    Then the response status should be 200
    And the profile first name should be "Jane"
    And the profile last name should be "Smith"

  Scenario: Get trainee profile for non-existent username
    Given the user is authenticated as any user
    When the trainee profile is requested for username "ghost.user"
    Then the response status should be 400

  Scenario: Update trainee profile successfully
    Given a registered trainee with first name "Alex" and last name "Brown"
    And the user is authenticated
    When the trainee profile is updated with first name "Alexander"
    Then the response status should be 200
    And the profile first name should be "Alexander"

  Scenario: Fail to update trainee profile with invalid first name
    Given a registered trainee with first name "Mia" and last name "Stone"
    And the user is authenticated
    When the trainee profile is updated with first name ""
    Then the response status should be 400

  Scenario: Activate trainee profile which is active
    Given a registered trainee with first name "Nina" and last name "Fox"
    And the user is authenticated
    When the trainee status is changed to active
    Then the response status should be 409

  Scenario: Deactivate trainee profile
    Given a registered trainee with first name "Owen" and last name "Reed"
    And the user is authenticated
    When the trainee status is changed to inactive
    Then the response status should be 200

  Scenario: Delete trainee profile successfully
    Given a registered trainee with first name "Liam" and last name "Gray"
    And the user is authenticated
    When the trainee profile is deleted
    Then the response status should be 200