
Feature: User Registration


  Background:
    Given I open the application


  Scenario: Valid user registration
    Given I am on the registration page
    When I enter valid registration details
    And I accept the terms and conditions
    When I submit the registration form
    Then I should see a success message
