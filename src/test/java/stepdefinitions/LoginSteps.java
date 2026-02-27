
package stepdefinitions;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.eo.Se;
import io.github.cdimascio.dotenv.Dotenv;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;
import utils.Hooks;

import java.net.MalformedURLException;
import java.time.Duration;

public class LoginSteps extends BaseSteps{

    //WebDriver driver;
    LoginPage loginPage;
    Dotenv dotenv = Dotenv.load();



    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        init();
        driver = Hooks.getDriver();
        loginPage = new LoginPage(driver);
        loginPage.clickGetStarted();

    }

    @When("I enter a valid username {string}")
    public void i_enter_a_valid_username(String ignored) {
        init();
        Dotenv dotenv = Dotenv.load();
        String username = dotenv.get("USER"); // pega o valor completo do .env
        loginPage.enterUsername(username);

    }

    @And("I enter a valid password {string}")
    public void i_enter_a_valid_password(String password) {

        init();
// Se o parâmetro estiver vazio, usa o valor do .env
        if (password == null || password.isEmpty()) {
            io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
            password = dotenv.get("PASSWORD");
        }

        loginPage.enterPassword(password);
    }

    @And("I click the login button")
    public void i_click_the_login_button() {
        init();
        loginPage.clickLoginButton();
    }

    @Then("I should be redirected to the dashboard page")
    public void i_should_be_redirected_to_the_dashboard_page() {
        init();
        loginPage.verifyDashboardPage();
    }

    @Then("I verify if new page contains expected text")
    public void i_verify_if_new_page_contains_expected_text() {
        init();
        loginPage.verifyExpectedText();
    }


}