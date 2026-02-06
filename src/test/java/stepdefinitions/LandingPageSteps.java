package stepdefinitions;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.LandingPage;
import utils.Hooks;

import static org.junit.Assert.assertTrue;

public class LandingPageSteps {

    private WebDriver driver;
    private LandingPage landingPage;

    @Given("I open the application")
    public void i_open_the_application() {

// Inicializa se necessário
        Hooks.ensureInitialized();
        driver = Hooks.getDriver();

        // Abre a homepage (BASE_URL)
        driver.get(Hooks.baseURL);

        landingPage = new LandingPage(driver);
    }


@Then("I verify the landing page is displayed correctly")
    public void i_verify_the_landing_page_is_displayed_correctly() {
        // Verifica se algum elemento chave da Landing Page está visível
        assertTrue("Landing Page não está visível!", landingPage.isLandingPageVisible());


    }


}

