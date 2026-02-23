package stepdefinitions;

import io.cucumber.java.en.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.LandingPage;
import utils.Hooks;

import static org.junit.Assert.assertTrue;

public class LandingPageSteps extends BaseSteps{

    private LandingPage landingPage;

    @Given("I open the application")
    public void i_open_the_application() {
        init();

        Dotenv env = Dotenv.load();
        String landingUrl = env.get("LANDING_URL");
        if (landingUrl == null || landingUrl.isEmpty()) {
            landingUrl = Hooks.baseURL; // usar BASE_URL
        }

        // Sessão limpa + navegação
        driver.manage().deleteAllCookies();
        driver.navigate().to(landingUrl);

        landingPage = new LandingPage(driver);
    }



        @Then("I verify the landing page is displayed correctly")
    public void i_verify_the_landing_page_is_displayed_correctly() {

            boolean visible = landingPage.isLandingPageVisible();
            Assert.assertTrue("Landing Page não está visível como esperado.", visible);
    }
}

