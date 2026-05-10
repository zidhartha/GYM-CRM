Feature: Training management

  Scenario: Successfully create a training
    Given a registered trainee with first name "Tom" and last name "Lee"
    And a registered trainer with first name "Coach" and last name "Pat" specializing in "Yoga"
    And the user is authenticated
    When a training is created with name "Morning Yoga" on "2025-06-01" for 60 minutes
    Then the response status should be 200

  Scenario: Create training with non-existent trainee
    Given a registered trainer with first name "Coach" and last name "Neg" specializing in "Pilates"
    And the user is authenticated as any user
    When a training is created with trainee "no.one" and name "Workout" on "2025-06-01" for 60 minutes
    Then the response status should be 400