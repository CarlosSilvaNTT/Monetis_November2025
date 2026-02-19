Feature: Payments
  Background:
    Given login and access payments page

  Scenario: Make payment (single example)
    When I make a payment with the following data
      | ACCOUNT  | REFERENCE  | ENTITY | AMOUNT | CATEGORY |
      | checking | INV-900234 | 12345  | 100    | house    |
    Then Verify confirmation window appears with payment details
    When I click to proceed with payment
    Then Verify success payment page appears
    When I access transactions page for payments
    Then Verify new transaction appears with "house" category and 100 amount