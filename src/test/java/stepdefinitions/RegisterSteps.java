
package stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.github.cdimascio.dotenv.Dotenv;
import io.cucumber.java.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.LoginPage;
import pages.RegisterPage;
import utils.Hooks;

public class RegisterSteps {
    private WebDriver driver;
    private RegisterPage registerPage;
    private LoginPage loginPage;

    private final Dotenv dotenv = Dotenv.load();
    private final String firstName = dotenv.get("name");
    private final String lastName = dotenv.get("surname");
    private final String email = dotenv.get("USER");
    private final String phone = dotenv.get("phone_number");
    private final String street = dotenv.get("street_address");
    private final String postalCode = dotenv.get("postal_code");
    private final String city = dotenv.get("city");
    private final String country = dotenv.get("countryTEXT");
    private final String password = dotenv.get("PASSWORD");
    private final String confirmPass = dotenv.get("confirmPassword");



    @Before(order=1)
    public void init() {
        if (registerPage == null) {
            driver = Hooks.getDriver();
            registerPage = new RegisterPage(driver);
        }

    }

    @Given("I am on the registration page")
    public void iAmOnTheRegistrationPage() {
        registerPage.navigateToRegisterPage();
    }


    @When("I enter valid registration details")
    public void i_enter_valid_registration_details() {

        registerPage.fillRegistrationForm(

                firstName,
                lastName,
                email,
                phone,
                street,
                postalCode,
                city,
                country,
                password,
                confirmPass
        );

    }

    @And("I accept the terms and conditions")
    public void iAcceptTheTermsAndConditions() {
        registerPage.acceptTerms();
    }

    @When("I submit the registration form")
    public void iSubmitTheRegistrationForm() {
        registerPage.submit();

// Assim que detectas que o email já existe, fecha a mensagem e vai ao Login:
        registerPage.closeErrorAndGoToLogin();
        // (Opcional) Agora faz o sign in
        if (loginPage == null) loginPage = new LoginPage(Hooks.getDriver());

        Dotenv dotenv = Dotenv.load();
        String username = dotenv.get("USER");
        loginPage.enterUsername(username);
        String password = dotenv.get("PASSWORD");
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();

    }


//    @Then("I should see a success message")
//    public void iShouldSeeASuccessMessage() {
//
//        Assert.assertTrue(
//                "Expected success message to be visible after registration",
//                registerPage.isSuccessBannerVisible()
//        );
//
//    }


}




