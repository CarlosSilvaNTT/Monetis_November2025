package stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.*;
import utils.Hooks;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

public class PaymentsSteps extends BaseSteps{

   // private WebDriver driver;
    private LoginPage loginPage;
    private AccountsPage accountsPage;
    private PaymentsPage paymentsPage;
    private TransactionsPage transactionsPage;

    // ------------------------------------------------------------------------
    // LOGIN + IR PARA PAYMENTS (idempotente e robusto)
    // ------------------------------------------------------------------------
    @Given("login and access payments page")
    public void login_and_access_payments_page() {

        init();
        loginPage = new LoginPage(driver);
        accountsPage = new AccountsPage(driver);
        paymentsPage = new PaymentsPage(driver);

        ensureLoggedIn(() -> defaultLoginFlow(loginPage));

        accountsPage.waitLoaded();
        accountsPage.clickPaymentsNav();
        paymentsPage.waitLoaded();

    }

    // ------------------------------------------------------------------------
    // PREENCHER PAGAMENTO (suporta DataTable vertical OU horizontal)
    // ------------------------------------------------------------------------
    @When("I make a payment with the following data")
    public void make_payment(DataTable table) {

        // Lê automaticamente o DataTable quer esteja no formato "horizontal" (header+linha)
        // quer no formato "vertical" (2 colunas: chave | valor).
        String account   = get(table, "ACCOUNT");
        String reference = get(table, "REFERENCE");
        String entity    = get(table, "ENTITY");
        String amountStr = get(table, "AMOUNT");
        String category  = get(table, "CATEGORY");

        paymentsPage.selectAccount(account);
        paymentsPage.enterReference(reference);
        paymentsPage.enterEntity(entity);
        paymentsPage.enterAmount(new BigDecimal(amountStr));
        paymentsPage.enterCategory(category);

        paymentsPage.goNextToConfirmation();
    }

    // Helper que suporta ambos os formatos de DataTable
    private String get(DataTable t, String key) {
        // 1) Tenta como tabela horizontal (header + 1 linha)
        try {
            Map<String, String> row = t.asMaps().get(0);
            String v = row.get(key);
            if (v != null) return v;
        } catch (Exception ignored) {}

        // 2) Tenta como tabela vertical (2 colunas: chave | valor)
        try {
            Map<String, String> kv = t.asMap(String.class, String.class);
            String v = kv.get(key);
            if (v != null) return v;
        } catch (Exception ignored) {}

        throw new IllegalArgumentException("Missing '" + key + "' in DataTable.");
    }

    // ------------------------------------------------------------------------
    // CONFIRMAÇÃO + SUCESSO
    // ------------------------------------------------------------------------
    @Then("Verify confirmation window appears with payment details")
    public void verify_confirmation() {
        Assert.assertTrue("Confirmation window not visible", paymentsPage.isConfirmationVisible());
    }

    @When("I click to proceed with payment")
    public void proceed_payment() {
        paymentsPage.confirmPayment();
    }

    @Then("Verify success payment page appears")
    public void verify_success() {
        Assert.assertTrue("Success window not visible", paymentsPage.isSuccessVisible());
    }

    // ------------------------------------------------------------------------
    // TRANSAÇÕES
    // ------------------------------------------------------------------------
    @When("I access transactions page for payments")
    public void access_transactions_page_for_payments() {
        transactionsPage = new TransactionsPage(driver);
        transactionsPage.openViaMenu();
    }

    @Then("Verify new transaction appears with {string} category and {int} amount")
    public void verify_transaction(String category, int amount) {
        String expectedAmount = "-" + amount + "€";
        boolean exists = transactionsPage.waitUntilTransactionAmountAndCategoryAppear(
                expectedAmount, category, Duration.ofSeconds(20)
        );
        Assert.assertTrue("Transaction not found with category=" + category + " and amount=" + expectedAmount, exists);
    }

    // ------------------------------------------------------------------------
    // (Opcional) Se quiseres, podemos ter este step para voltar a payments a partir de transactions
    // ------------------------------------------------------------------------
    @When("I navigate back to payments from transactions")
    public void go_back_to_payments_from_transactions() {
        accountsPage = new AccountsPage(driver);
        accountsPage.clickPaymentsNav();
        paymentsPage = new PaymentsPage(driver);
        paymentsPage.waitLoaded();
    }
}