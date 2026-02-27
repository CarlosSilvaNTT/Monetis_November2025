Feature: Payments

  Background:
    Given login and access payments page

  @MultiplePayments
  Scenario Outline: Make multiple payments
    When I make a payment with the following data
      | ACCOUNT  | REFERENCE  | ENTITY | AMOUNT | CATEGORY |
      | <ACCOUNT>| <REFERENCE>| <ENTITY>| <AMOUNT>| <CATEGORY> |
    Then Verify confirmation window appears with payment details
    When I click to proceed with payment
    Then Verify success payment page appears

    # Validate transaction
    When I access transactions page for payments
    Then Verify new transaction appears with "<CATEGORY>" category and <AMOUNT> amount

    # Return to payments to start next iteration
    When I navigate back to payments from transactions

    Examples:
      | ACCOUNT  | REFERENCE  | ENTITY | AMOUNT | CATEGORY |
      | checking | INV-900234 | 12345  | 100    | car      |
      | checking | INV-995005 | 92199  | 4      | Mobile   |
      | checking | INV-988700 | 90009  | 24     | bills    |
      | checking | INV-900001 | 55199  | 200    | house    |