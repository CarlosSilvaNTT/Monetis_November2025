@transfer_other_account
Feature: Transfer to other account

  Background:
    Given login and access transfer page

  Scenario: Make a transfer to another account
    When I select transfer to other account
    And I fill in transfer form with "BH1895685301458576227" target, 1000 amount and proceed
    Then Verify confirmation window appears with transfer details
    When I click to proceed with transfer
    Then Verify success transfer page appears
    When I access accounts page
    Then Verify "checking" account balance decreased
    When I access transactions page
    Then Verify new transaction with "-1000€" appears on the list