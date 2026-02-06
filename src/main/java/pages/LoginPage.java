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

    @FindBy(xpath = ("//span[normalize-space()='Get Started']"))
    WebElement getStartedButton;

    @FindBy(xpath = ("//input[@placeholder='Email']"))
    public// Replace with actual locator
    WebElement usernameField;

    @FindBy(xpath=("//input[@placeholder='Password']")) // Replace with actual locator
    WebElement passwordField;

    @FindBy(xpath = ("//button[@type='submit']")) // Replace with actual locator
    WebElement loginButton;


    @FindBy(xpath = ("//*[contains(text(), 'Welcome')]"))
    WebElement expectedText;

    private static final By LOADING_OVERLAY = By.cssSelector("div.loading_screen");


    public LoginPage(WebDriver driver) {

        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);

    }



    public void clickGetStarted() {


        wait.until(ExpectedConditions.elementToBeClickable(getStartedButton)).click();

    }


    public void enterUsername(String username) {


        usernameField.clear(); //  limpar
        usernameField.sendKeys(username);

    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
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
            wait.until(ExpectedConditions.visibilityOf(expectedText));
            Assert.assertTrue("Dashboard is not displayed", expectedText.isDisplayed());
        } catch (TimeoutException e) {
            Assert.fail("Dashboard não foi apresentado após login (timeout a aguardar elemento).");
        }
    }


    public LandingPage login(String testEmail, String testPassword) {
        return null;
    }
}
