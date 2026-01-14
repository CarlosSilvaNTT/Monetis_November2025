
package e2e;

import api.RegisterUser;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.LandingPage;
import utils.Hooks;

import java.io.IOException;
import java.util.Map;

public class UserRegistrationAndLoginTest extends Hooks {

    private final String testEmail = "testing@example.com"; // mesmo email do RegisterUser
    private final String testPassword = "testingPassword!1";

    @BeforeClass
    public void registerUser() throws IOException, InterruptedException {
        Map<String, Object> response = RegisterUser.register();
        System.out.println("API Response: " + response);
        Assert.assertTrue(response.containsKey("success"), "User registration failed!");
    }

    @Test
    public void loginAfterRegistration() {
      //  LoginPage loginPage = new LoginPage(driver);
      //  LandingPage dashboardPage = loginPage.login(testEmail, testPassword);
      //  Assert.assertTrue(dashboardPage.isOnLandingPage(), "User is not on dashboard!");
    }
}

