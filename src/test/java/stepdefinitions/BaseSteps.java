package stepdefinitions;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;
import utils.Hooks;

import java.time.Duration;

public class BaseSteps {
    protected WebDriver driver;
    protected WebDriverWait wait;

    protected void init() {
        this.driver = Hooks.getDriver();

// 2) 'Cinto de segurança': se ainda vier a null (timing), cria via Hooks
        if (this.driver == null) {
            System.out.println("[BaseSteps] driver estava null; a chamar Hooks.ensureInitialized()...");
            Hooks.ensureInitialized();   // usa o mesmo método do Hooks
            this.driver = Hooks.getDriver();
            if (this.driver == null) {
                throw new IllegalStateException(
                        "[BaseSteps] driver continua null mesmo após Hooks.ensureInitialized(). " +
                                "Verifica se o @Before do Hooks está a correr e se o glue inclui 'utils'."
                );
            }
        }

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected boolean isLoggedIn() {

        if (driver == null) return false;

        // 1) Sinal forte via URL das áreas autenticadas
        String url = driver.getCurrentUrl();
        if (url != null && (
                url.contains("/dashboard") ||
                        url.contains("/payments") ||
                        url.contains("/transactions") ||
                        url.contains("/transfer")
        )) {
            return true;
        }

// 2) Sinal forte via botão "Log out" na navegação autenticada
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[@class='navigation']//button[span[normalize-space()='Log out']]")
                    ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }


    }

    protected void ensureLoggedIn(Runnable loginAction) {
        if (!isLoggedIn()) {
            loginAction.run();
        }
    }

    protected void defaultLoginFlow(LoginPage loginPage) {
        if (isLoggedIn()) return;
        Dotenv env = Dotenv.load();
        loginPage.clickGetStarted();
        loginPage.enterUsername(env.get("USER"));
        loginPage.enterPassword(env.get("PASSWORD"));
        loginPage.clickLoginButton();
    }
}
