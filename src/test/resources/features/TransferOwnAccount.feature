@transfer_own_account
Feature: Transfer to own account

  Background:
    Given login and access transfer page

  Scenario: Make a transfer from checking to savings
    When I select transfer to own account
    And I fill in transfer form with "Savings" account and 1000 amount and proceed
    Then Verify confirmation window appears with transfer details
    When I click to proceed with transfer
    Then Verify success transfer page appears
    When I access accounts page
    Then verify "Savings" account balance increased