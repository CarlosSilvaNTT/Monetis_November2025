package stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Assert;
import org.openqa.selenium.*;
import pages.*;
import utils.Hooks;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

public class PaymentsSteps {

    private WebDriver driver;
    private LoginPage loginPage;
    private AccountsPage accountsPage;
    private PaymentsPage paymentsPage;
    private TransactionsPage transactionsPage;

    // ------------------------------------------------------------
    // LOGIN + NAVIGATE TO PAYMENTS
    // ------------------------------------------------------------
    @Given("login and access payments page")
    public void login_and_access_payments_page() {

        driver = Hooks.getDriver();
        loginPage = new LoginPage(driver);
        accountsPage = new AccountsPage(driver);
        paymentsPage = new PaymentsPage(driver);

        // LOGIN ONLY IF NOT AUTHENTICATED
        if (!isLoggedIn()) {
            Dotenv env = Dotenv.load();
            loginPage.clickGetStarted();
            loginPage.enterUsername(env.get("USER"));
            loginPage.enterPassword(env.get("PASSWORD"));
            loginPage.clickLoginButton();
        }

        // Always ensure Dashboard is active
        accountsPage.waitLoaded();

        // Navigate to Payments
        accountsPage.clickPaymentsNav();
        paymentsPage.waitLoaded();
    }

    private boolean isLoggedIn() {
        try {
            driver.findElement(By.cssSelector("div.navigation"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ------------------------------------------------------------
    // PAYMENT FLOW
    // ------------------------------------------------------------
    @When("I make a payment with the following data")
    public void make_payment(DataTable table) {
        Map<String, String> row = table.asMaps().get(0);

        paymentsPage.selectAccount(row.get("ACCOUNT"));
        paymentsPage.enterReference(row.get("REFERENCE"));
        paymentsPage.enterEntity(row.get("ENTITY"));
        paymentsPage.enterAmount(new BigDecimal(row.get("AMOUNT")));
        paymentsPage.enterCategory(row.get("CATEGORY"));

        paymentsPage.goNextToConfirmation();
    }

    @Then("Verify confirmation window appears with payment details")
    public void verify_confirmation() {
        Assert.assertTrue(paymentsPage.isConfirmationVisible());
    }

    @When("I click to proceed with payment")
    public void proceed_payment() {
        paymentsPage.confirmPayment();
    }

    @Then("Verify success payment page appears")
    public void verify_success() {
        Assert.assertTrue(paymentsPage.isSuccessVisible());
    }

    // ------------------------------------------------------------
    // TRANSACTIONS VALIDATION
    // ------------------------------------------------------------
    @When("I access transactions page for payments")
    public void access_transactions() {
        transactionsPage = new TransactionsPage(driver);
        transactionsPage.openViaMenu();
    }

    @Then("Verify new transaction appears with {string} category and {int} amount")
    public void verify_transaction(String category, int amount) {

        String expectedAmount = "-" + amount + "€";

        boolean exists = transactionsPage.waitUntilTransactionAmountAndCategoryAppear(
                expectedAmount,
                category,
                Duration.ofSeconds(20)
        );

        Assert.assertTrue("Transaction not found!", exists);
    }
}
