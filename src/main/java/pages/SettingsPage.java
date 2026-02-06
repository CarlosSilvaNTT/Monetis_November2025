package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class SettingsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Raiz e overlay (existem no teu DOM)
    private static final By SETTINGS_ROOT   = By.cssSelector("div.settings");
    private static final By LOADING_OVERLAY = By.cssSelector("div.loading_screen");

    // Abas internas de Settings (a de Delete Account está ativa no teu HTML)
    private static final By TAB_DELETE_ACCOUNT = By.xpath(
            "//div[@class='tabs']//div[span[normalize-space()='Delete account']]"
    );
    private static final By TAB_DELETE_ACCOUNT_ACTIVE = By.xpath(
            "//div[@class='tabs']//div[contains(@class,'active')][span[normalize-space()='Delete account']]"
    );

    // Formulário de Delete
    private static final By INPUT_CONFIRM_PASSWORD = By.cssSelector("input[name='confirmDeletePassword']");
    private static final By CONFIRM_DELETE_BUTTON  = By.xpath("//button[@type='submit' and contains(@class,'delete') and normalize-space()='Confirm delete']");

    // Heurísticas de 'log out' (vai para login)
    private static final By LOGIN_CTA = By.xpath(
            "//button[normalize-space()='Get started' or normalize-space()='Login' or .//span[normalize-space()='Get started' or normalize-space()='Login']]"
    );

    public SettingsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    /** Aguarda que a página /settings carregue. */
    public void waitLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(SETTINGS_ROOT));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
        } catch (TimeoutException ignore) {
            // overlay pode já vir "hidden"; seguimos em frente
        }
        wait.until(ExpectedConditions.urlContains("/settings"));
    }

    /** Garante que a tab 'Delete account' está selecionada. Se não estiver, clica. */
    public void ensureDeleteAccountTabSelected() {
        try {
            driver.findElement(TAB_DELETE_ACCOUNT_ACTIVE);
        } catch (NoSuchElementException e) {
            WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(TAB_DELETE_ACCOUNT));
            tab.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(TAB_DELETE_ACCOUNT_ACTIVE));
        }
    }

    public void enterConfirmPassword(String password) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_CONFIRM_PASSWORD));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        input.click();
        input.clear();
        input.sendKeys(password);
    }

    public void clickConfirmDelete() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_DELETE_BUTTON));
        btn.click();
    }

    /**
     * Após apagar, a app indica: "After deleting your account, you will be logged out!"
     * Espera pelo redirecionamento para /login ou pela presença de um CTA de login.
     */
    public boolean waitUntilLoggedOut(Duration timeout) {
        try {
            return new WebDriverWait(driver, timeout).until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/login"),
                    ExpectedConditions.visibilityOfElementLocated(LOGIN_CTA)
            ));
        } catch (TimeoutException te) {
            return false;
        }
    }
}
