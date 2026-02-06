package stepdefinitions;

import io.cucumber.java.en.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.*;
import utils.Hooks;

import java.math.BigDecimal;
import java.time.Duration;

public class TransferSteps {

    private WebDriver driver;

    private LoginPage loginPage;
    private AccountsPage accountsPage;
    private TransferPage transferPage;
    private TransactionsPage transactionsPage;

    // Scenario state
    private BigDecimal beforeChecking;
    private BigDecimal beforeSavings;
    private BigDecimal requestedAmount;

    @Given("login and access transfer page")
    public void login_and_access_transfer_page() {
        driver = Hooks.getDriver();
        loginPage = new LoginPage(driver);
        accountsPage = new AccountsPage(driver);
        transferPage = new TransferPage(driver);

        // ---credentials ---
        Dotenv dotenv = Dotenv.load();
        String username = dotenv.get("USER");
        String password = dotenv.get("PASSWORD");

        // Login flow
        loginPage.clickGetStarted();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();

        // --- Accounts: capture balances BEFORE transfer ---
        accountsPage.waitLoaded();
        beforeChecking = accountsPage.getBalance("Checking");
        beforeSavings  = accountsPage.getBalance("Savings");
        Assert.assertNotNull("Checking balance is null", beforeChecking);
        Assert.assertNotNull("Savings balance is null", beforeSavings);

        // --- Go to transfer page ---
        accountsPage.clickTransferNav();
        transferPage.waitLoaded();
    }

    @When("I select transfer to own account")

    public void i_select_transfer_to_own_account() {
        transferPage.ensureOwnAccountSelected();
    }


    @When("I select transfer to other account")
    public void i_select_transfer_to_other_account() {
        transferPage.selectOtherAccountOption();
    }



    @When("I fill in transfer form with {string} account and {int} amount and proceed")
    public void i_fill_in_transfer_form_with_account_and_amount_and_proceed(String toAccount, Integer amount) {
        // Use BigDecimal to avoid float rounding issues
        this.requestedAmount = new BigDecimal(amount);

        // VERY IMPORTANT NOTE:
        // "existent amount must always be superior or equal of the amount we're going to transfer"
        // => requestedAmount <= beforeChecking  (i.e., checking has enough funds)
        Assert.assertTrue(
                String.format("Requested amount %s must be <= checking balance %s",
                        requestedAmount, beforeChecking),
                requestedAmount.compareTo(beforeChecking) <= 0
        );

        // Perform the transfer form steps
        transferPage.selectDestinationOwnAccount(toAccount); // "savings"
        transferPage.enterAmount(requestedAmount);
        transferPage.clickNext(); // shows confirmation
    }


    @When("I fill in transfer form with {string} target, {int} amount and proceed")
    public void i_fill_in_transfer_form_other_account(String target, Integer amount) {

        this.requestedAmount = new BigDecimal(amount);

        Assert.assertTrue(
                "Not enough funds",
                requestedAmount.compareTo(beforeChecking) <= 0
        );

        transferPage.enterOtherAccountTarget(target);
        transferPage.enterAmount(requestedAmount);
        transferPage.clickNext();

        // lógica cenário 4
    }


    @Then("Verify confirmation window appears with transfer details")
    public void verify_confirmation_window_appears_with_transfer_details() {
        Assert.assertTrue("Confirmation step not visible", transferPage.isConfirmationVisible());
//        Assert.assertTrue("Confirmation source mismatch", transferPage.confirmationShowsSource("checking"));
        //       Assert.assertTrue("Confirmation destination mismatch", transferPage.confirmationShowsDestination("savings"));
        //       Assert.assertTrue("Confirmation amount mismatch", transferPage.confirmationShowsAmount(requestedAmount));
    }

    @When("I click to proceed with transfer")
    public void i_click_to_proceed_with_transfer() {
        transferPage.confirmTransfer();
    }

    @Then("Verify success transfer page appears")
    public void verify_success_transfer_page_appears() {
        Assert.assertTrue("Success step not visible", transferPage.isSuccessVisible());
        //       Assert.assertTrue("Success page amount mismatch", transferPage.successShowsAmount(requestedAmount));
    }

    @When("I access accounts page")
    public void i_access_accounts_page() {
        // Return to Dashboard to read final balances

        String url = driver.getCurrentUrl();
        String dashUrl = url.contains("/transfer")
                ? url.replace("/transfer", "/dashboard")
                : url; // fallback if already there

        driver.navigate().to(dashUrl);
        accountsPage.waitLoaded();

        // Return using the menu (ensures the SPA triggers fresh data load)
        accountsPage.clickDashboardNav();

    }

    @Then("Verify {string} account balance decreased")
    public void verify_account_balance_decreased(String accountName) {
        BigDecimal afterChecking = accountsPage.getBalance("Checking");
        BigDecimal expected = beforeChecking.subtract(requestedAmount);

        Assert.assertEquals(
                "Checking balance did not decrease correctly",
                0,
                expected.compareTo(afterChecking)
        );
    }


    @When("I access transactions page")
    public void i_access_transactions_page() {
        transactionsPage = new TransactionsPage(driver);
        transactionsPage.openViaMenu();
    }


    @Then("Verify new transaction with {string} appears on the list")
    public void verify_new_transaction_appears(String text) {

        boolean ok = transactionsPage
                .waitUntilTransactionAmountAppears(text, Duration.ofSeconds(12));
        Assert.assertTrue("Transaction not found: " + text, ok);

    }


    @Then("verify {string} account balance increased")
    public void verify_account_balance_increased(String accountName) {

        BigDecimal afterChecking = accountsPage.getBalance("Checking");

        BigDecimal expectedChecking = beforeChecking.subtract(requestedAmount);

// Compute the expected value
        BigDecimal expectedSavings = beforeSavings.add(requestedAmount);

        // Wait/poll until the UI reflects the updated balance
        BigDecimal afterSavings = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                .until(d -> {
                    BigDecimal current = accountsPage.getBalance(accountName);
                    return expectedSavings.compareTo(current) == 0 ? current : null;
                });


        org.junit.Assert.assertEquals(
                "Savings balance did not increase by transfer amount",
                0, expectedSavings.compareTo(afterSavings)
        );

        // Optional but recommended:
        org.junit.Assert.assertEquals(
                "Checking balance did not decrease by transfer amount",
                0, expectedChecking.compareTo(afterChecking)
        );
    }
}
