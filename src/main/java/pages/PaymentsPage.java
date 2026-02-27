package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.math.BigDecimal;
import java.time.Duration;

public class PaymentsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By LOADING = By.cssSelector("div.loading_screen");

    private static final By DETAILS_TITLE =
            By.xpath("//h2[normalize-space()='Fill in payment details']");

    private static final By CONFIRM_TITLE =
            By.xpath("//h2[contains(.,'Confirm')]");

    private static final By SUCCESS_TITLE =
            By.xpath("//h2[contains(.,'Success') or contains(.,'Transaction completed')]");

    public PaymentsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ---------------------------------------------------------
    public void waitLoaded() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING));
        } catch (Exception ignored) {}
        wait.until(ExpectedConditions.visibilityOfElementLocated(DETAILS_TITLE));
    }

    // ---------------------------------------------------------
    // ACCOUNT SELECT (React-Select-3)
    // ---------------------------------------------------------
    public void selectAccount(String account) {

        WebElement wrapper = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@class='css-hlgwow'])[1]")
        ));
        safeClick(wrapper);

        WebElement opt = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(@id,'react-select-3-option') " +
                        "and translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='" +
                        account.toLowerCase() + "']")
        ));
        safeClick(opt);
    }

    // ---------------------------------------------------------
    public void enterReference(String ref) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("reference")));
        el.clear();
        el.sendKeys(ref);
    }

    public void enterEntity(String entity) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("entity")));
        el.clear();
        el.sendKeys(entity);
    }

    public void enterAmount(BigDecimal amount) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("amount")));
        el.clear();
        el.sendKeys(amount.toPlainString());
    }

    // ---------------------------------------------------------
    // CATEGORY (React-Select-2)
    // ---------------------------------------------------------
    public void enterCategory(String category) {
        WebElement wrapper = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@class='css-hlgwow'])[2]")
        ));
        safeClick(wrapper);

        WebElement opt = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(@id,'react-select-2-option') " +
                        "and translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='" +
                        category.toLowerCase() + "']")
        ));
        safeClick(opt);
    }

    // ---------------------------------------------------------
    public void goNextToConfirmation() {
        WebElement nextBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//form//button[normalize-space()='Next']")
                )
        );
        safeClick(nextBtn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(CONFIRM_TITLE));
    }

    public boolean isConfirmationVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(CONFIRM_TITLE));
            return true;
        } catch (Exception e) { return false; }
    }

    // ---------------------------------------------------------
    public void confirmPayment() {
        WebElement next = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form//button[@type='submit']")
        ));
        safeClick(next);

        wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_TITLE));
    }

    public boolean isSuccessVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_TITLE));
            return true;
        } catch (Exception e) { return false; }
    }

    // ---------------------------------------------------------
    private void safeClick(WebElement el) {
        try { el.click(); }
        catch (Exception e1) {
            try { new Actions(driver).moveToElement(el).click().perform(); }
            catch (Exception e2) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
        }
    }
}
