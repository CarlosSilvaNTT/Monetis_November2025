Feature: Login Functionality

    Background:
        Given I open the application


    Scenario: Successful login with valid credentials
        Given I am on the login page
        When I enter a valid username ""
        And I enter a valid password ""
        And I click the login button
        Then I should be redirected to the dashboard page
        Then I verify if new page contains expected text


