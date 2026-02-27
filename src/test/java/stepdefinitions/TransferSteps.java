package stepdefinitions;

import io.cucumber.java.en.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.*;
import utils.Hooks;

import java.math.BigDecimal;
import java.time.Duration;

public class TransferSteps extends BaseSteps{



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

        init(); // from BaseSteps
        loginPage = new LoginPage(driver);
        accountsPage = new AccountsPage(driver);
        transferPage = new TransferPage(driver);

        // ✅ Só faz login se for mesmo necessário
        ensureLoggedIn(() -> defaultLoginFlow(loginPage));

        // --- Accounts: capture balances BEFORE transfer ---
        accountsPage.waitLoaded();
        beforeChecking = accountsPage.getBalance("Checking");
        beforeSavings = accountsPage.getBalance("Savings");
        Assert.assertNotNull("Checking balance is null", beforeChecking);
        Assert.assertNotNull("Savings balance is null", beforeSavings);

        // --- Go to transfer page ---
        accountsPage.clickTransferNav();
        transferPage.waitLoaded();



    }

    @When("I select transfer to own account")

    public void i_select_transfer_to_own_account() {
        init();
        transferPage.ensureOwnAccountSelected();
    }


    @When("I select transfer to other account")
    public void i_select_transfer_to_other_account() {
        init();
        transferPage.selectOtherAccountOption();
    }



    @When("I fill in transfer form with {string} account and {int} amount and proceed")
    public void i_fill_in_transfer_form_with_account_and_amount_and_proceed(String toAccount, Integer amount) {
        init();
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
        init();
        this.requestedAmount = new BigDecimal(amount);

        Assert.assertTrue(
                "Not enough funds",
                requestedAmount.compareTo(beforeChecking) <= 0
        );

        transferPage.enterOtherAccountTarget(target);
        transferPage.enterAmount(requestedAmount);
        transferPage.clickNext();
    }


    @Then("Verify confirmation window appears with transfer details")
    public void verify_confirmation_window_appears_with_transfer_details() {
        init();
        Assert.assertTrue("Confirmation step not visible", transferPage.isConfirmationVisible());
//        Assert.assertTrue("Confirmation source mismatch", transferPage.confirmationShowsSource("checking"));
        //       Assert.assertTrue("Confirmation destination mismatch", transferPage.confirmationShowsDestination("savings"));
        //       Assert.assertTrue("Confirmation amount mismatch", transferPage.confirmationShowsAmount(requestedAmount));
    }

    @When("I click to proceed with transfer")
    public void i_click_to_proceed_with_transfer() {
        init();
        transferPage.confirmTransfer();
    }

    @Then("Verify success transfer page appears")
    public void verify_success_transfer_page_appears() {
        init();
        Assert.assertTrue("Success step not visible", transferPage.isSuccessVisible());
        //       Assert.assertTrue("Success page amount mismatch", transferPage.successShowsAmount(requestedAmount));
    }

    @When("I access accounts page")
    public void i_access_accounts_page() {
        // Return to Dashboard to read final balances
        init();
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
        init();
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
        init();
        transactionsPage = new TransactionsPage(driver);
        transactionsPage.openViaMenu();
    }


    @Then("Verify new transaction with {string} appears on the list")
    public void verify_new_transaction_appears(String text) {
        init();
        boolean ok = transactionsPage
                .waitUntilTransactionAmountAppears(text, Duration.ofSeconds(12));
        Assert.assertTrue("Transaction not found: " + text, ok);

    }


    @Then("verify {string} account balance increased")
    public void verify_account_balance_increased(String accountName) {
        init();


        // Esperados
        BigDecimal expectedChecking = beforeChecking.subtract(requestedAmount);
        BigDecimal expectedSavings  = beforeSavings.add(requestedAmount);

        // Aguarda até AMBOS os saldos refletirem a transferência (até 20s)
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20))
                .until(d -> {
                    BigDecimal chk = accountsPage.getBalance("Checking");  // faz waitLoaded internamente
                    BigDecimal sav = accountsPage.getBalance(accountName);
                    return expectedChecking.compareTo(chk) == 0
                            && expectedSavings.compareTo(sav) == 0;
                });

        // Leitura final (após a condição estar satisfeita)
        BigDecimal afterChecking = accountsPage.getBalance("Checking");
        BigDecimal afterSavings  = accountsPage.getBalance(accountName);



        Assert.assertEquals(
                "Savings balance did not increase by transfer amount",
                0, expectedSavings.compareTo(afterSavings)
        );

        // Optional but recommended:
        Assert.assertEquals(
                "Checking balance did not decrease by transfer amount",
                0, expectedChecking.compareTo(afterChecking)
        );
    }
}
