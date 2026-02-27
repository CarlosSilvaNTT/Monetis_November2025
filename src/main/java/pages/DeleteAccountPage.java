package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class DeleteAccountPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By DELETE_BUTTON = By.xpath("//button[normalize-space()='Delete account']");
    private static final By CONFIRM_MODAL = By.cssSelector("div.confirmation-modal");
    private static final By CONFIRM_BUTTON = By.xpath("//button[normalize-space()='Confirm']");
    private static final By SUCCESS_TOAST = By.xpath("//div[contains(@class,'Toastify')]//*[contains(text(),'successfully deleted')]");

    public DeleteAccountPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    public void clickDelete() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(DELETE_BUTTON));
        button.click();
    }

    public void confirmDelete() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(CONFIRM_MODAL));
        driver.findElement(CONFIRM_BUTTON).click();
    }

    public boolean successToastVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_TOAST));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
