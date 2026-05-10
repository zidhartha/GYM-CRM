Feature: Trainer management

  Scenario: Create trainer profile successfully
    When a trainer is registered with first name "Coach" and last name "Mike" specializing in "Yoga"
    Then the response status should be 201
    And the response should contain a username and password

  Scenario: Fail to create trainer profile due to missing first name
    When a trainer is registered with first name "" and last name "Mike" specializing in "Yoga"
    Then the response status should be 400

  Scenario: Fail to create trainer profile due to invalid specialization
    When a trainer is registered with first name "Coach" and last name "Bad" specializing in "FakeType"
    Then the response status should be 400

  Scenario: Get trainer profile successfully
    Given a registered trainer with first name "Coach" and last name "Dan" specializing in "Pilates"
    And the user is authenticated
    When the trainer profile is requested
    Then the response status should be 200
    And the profile first name should be "Coach"

  Scenario: Get trainer profile for non-existent username
    Given a registered trainer with first name "Coach" and last name "Ghost" specializing in "Yoga"
    And the trainer is authenticated
    When the trainer profile is requested for username "ghost.trainer"
    Then the response status should be 400

  Scenario: Update trainer profile successfully
    Given a registered trainer with first name "Coach" and last name "Neo" specializing in "Yoga"
    And the trainer is authenticated
    When the trainer profile is updated with first name "NeoUpdated"
    Then the response status should be 200
    And the profile first name should be "NeoUpdated"

  Scenario: Fail to update trainer profile with invalid first name
    Given a registered trainer with first name "Coach" and last name "Ivy" specializing in "Pilates"
    And the user is authenticated
    When the trainer profile is updated with first name ""
    Then the response status should be 400

  Scenario: Activate trainer profile which is active
    Given a registered trainer with first name "Coach" and last name "Ray" specializing in "Yoga"
    And the user is authenticated
    When the trainer status is changed to active
    Then the response status should be 409

  Scenario: Deactivate trainer profile
    Given a registered trainer with first name "Coach" and last name "Max" specializing in "Pilates"
    And the user is authenticated
    When the trainer status is changed to inactive
    Then the response status should be 200

  Scenario: Get unassigned trainers for trainee successfully
    Given a registered trainee with first name "Tara" and last name "West"
    And the user is authenticated
    When unassigned trainers are requested for the trainee
    Then the response status should be 200