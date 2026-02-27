package stepdefinitions;

import io.cucumber.java.en.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.AccountsPage;
import pages.LoginPage;
import pages.SettingsPage;
import utils.Hooks;

import java.time.Duration;

public class DeleteAccountSteps {

    private WebDriver driver;
    private LoginPage loginPage;
    private AccountsPage accountsPage;
    private SettingsPage settingsPage;

    private String username;
    private String password; // usado também para confirmar a eliminação

    @Given("login and access delete account settings page")
    public void login_and_access_delete_account_settings_page() {
        driver = Hooks.getDriver();
        loginPage = new LoginPage(driver);
        accountsPage = new AccountsPage(driver);
        settingsPage = new SettingsPage(driver);

        // ⚠️ Use credenciais DEDICADAS para este teste destrutivo
        Dotenv dotenv = Dotenv.load();
        // Primeiro tenta variáveis dedicadas; se não houver, cai para USER/PASSWORD
        username = dotenv.get("USER_DELETE", dotenv.get("USER"));
        password = dotenv.get("PASSWORD_DELETE", dotenv.get("PASSWORD"));

        // Login
        loginPage.clickGetStarted();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();

        // Ir para Settings
        accountsPage.waitLoaded();
        accountsPage.clickSettingsNav();

        // Garantir tab "Delete account"
        settingsPage.waitLoaded();
        settingsPage.ensureDeleteAccountTabSelected();
    }

    @When("I confirm account deletion with my password")
    public void i_confirm_account_deletion_with_my_password() {
        settingsPage.enterConfirmPassword(password);
        settingsPage.clickConfirmDelete();
    }

    @Then("Verify I am logged out to the login page")
    public void verify_i_am_logged_out_to_the_login_page() {
        boolean loggedOut = settingsPage.waitUntilLoggedOut(Duration.ofSeconds(20));
        Assert.assertTrue("User did not log out after deletion", loggedOut);
    }
}
