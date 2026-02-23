package pages;

import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class LoginPage {
    private final WebDriverWait wait;
    WebDriver driver;

    private static final By getStartedButton = By.xpath(
            "//button[span[normalize-space()='Get Started']] " +
                    " | //a[normalize-space()='Get Started'] " +
                    " | //span[normalize-space()='Get Started']/ancestor::button"
    );

    private static final By usernameField = By.xpath("//input[@placeholder='Email']");

    private static final By passwordField = By.xpath("//input[@placeholder='Password']");

    private static final By loginButton = By.xpath("//button[@type='submit']");

    private static final By expectedText = By.xpath("//*[contains(text(), 'Welcome')]");

    private static final By LOADING_OVERLAY = By.cssSelector("div.loading_screen");


    public LoginPage(WebDriver driver) {

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    }



    public void clickGetStarted() {

        try {
            // Tenta encontrar/clicar; se não existir, apanha o Timeout e segue
            wait.until(ExpectedConditions.elementToBeClickable(getStartedButton)).click();
        } catch (TimeoutException | NoSuchElementException ignored) {
            // Já estamos para lá do landing, ou o produto mudou o botão - segue sem clicar
        }


    }


    public void enterUsername(String username) {

        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        el.clear();
        el.sendKeys(username);


    }

    public void enterPassword(String password) {

        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        el.clear();
        el.sendKeys(password);

    }

    public void clickLoginButton() {

        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
        // aguarda o DOM ficar “complete”
        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").equals("complete"));

    }

    public void verifyDashboardPage() {

        try {
            wait.until(ExpectedConditions.urlContains("/dashboard"));
        } catch (TimeoutException e) {
            Assert.fail("URL não contém '/dashboard' após login (timeout). URL atual: " + driver.getCurrentUrl());
        }


    }

    public void verifyExpectedText() {

        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(expectedText));
            Assert.assertTrue("Dashboard text not visible", el.isDisplayed());
        } catch (TimeoutException e) {
            Assert.fail("Dashboard not shown after login.");
        }


}


    public LandingPage login(String testEmail, String testPassword) {
        return null;
    }
}
