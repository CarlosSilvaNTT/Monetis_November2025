

@delete_account @destructive
Feature: Delete user account

  Background:
    Given login and access delete account settings page

  Scenario: Delete the user account successfully
    When I confirm account deletion with my password
    Then Verify I am logged out to the login page

