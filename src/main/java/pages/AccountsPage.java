package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.*;

import java.math.BigDecimal;
import java.time.Duration;

public class AccountsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By DASHBOARD_ROOT   = By.cssSelector("div.dashboard");
    private static final By NAV_ROOT         = By.cssSelector("div.navigation");
    private static final By LOADING_OVERLAY  = By.cssSelector("div.loading_screen");
    private static final By ACCOUNTS_BALANCE = By.cssSelector("div.accounts_balance");

    // Menu navigation left side
    private static final By NAV_TRANSFER = By.xpath(
            "//div[@class='navigation']//div[@class='options']/div[span[normalize-space()='Transfer']]"
    );

    private static final By NAV_DASHBOARD = By.xpath(
            "//div[@class='navigation']//div[@class='options']/div[span[normalize-space()='Dashboard']]"
    );


    private static final By NAV_SETTINGS = By.xpath(
            "//div[@class='navigation']//div[@class='options']/div[span[normalize-space()='Settings']]"
    );



    private final By paymentsNav =
            By.xpath("//div[@class='options']//div[span='Payments']");



    public AccountsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    /** Ensure Dashboard page finished loading */
    public void waitLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(DASHBOARD_ROOT));
        wait.until(ExpectedConditions.visibilityOfElementLocated(NAV_ROOT));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNTS_BALANCE));

        // Ensure DOM fully ready
        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").equals("complete"));

        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    /** Click Transfer from navigation menu */
    public void clickTransferNav() {
        waitLoaded();
        WebElement transfer = wait.until(ExpectedConditions.elementToBeClickable(NAV_TRANSFER));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", transfer);

        try { transfer.click(); }
        catch (ElementClickInterceptedException e) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", transfer);
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
        wait.until(ExpectedConditions.urlContains("/transfer"));
    }

    /** Click Dashboard from navigation menu */
    public void clickDashboardNav() {
        waitLoaded();
        WebElement dash = wait.until(ExpectedConditions.elementToBeClickable(NAV_DASHBOARD));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", dash);

        try { dash.click(); }
        catch (ElementClickInterceptedException e) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dash);
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
        waitLoaded();
    }

    /**
     * Case‑insensitive balance extraction.
     * Accepts “Checking”, “Savings”, “checking”, “savings”, etc.
     */
    public BigDecimal getBalance(String accountTitle) {
        waitLoaded();

        String normalizedTitle = accountTitle.trim().toLowerCase();

        // Locate the <p> containing balance inside the card with matching <h2>
        By amountLocator = By.xpath(
                "//div[@class='accounts_balance']" +
                        "//div[contains(@class,'account')]//h2[" +
                        "translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='" + normalizedTitle + "'" +
                        "]/following-sibling::p[1]"
        );

        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(amountLocator));
        String raw = el.getText().trim();

        return parseBalance(raw);
    }

    /**
     * Correct Portuguese (and general EU) amount parsing:
     *  "10.000,00 €" → "10000.00"
     *  handles: NBSP, any unicode spaces, thousand separators, decimal commas, euro symbols.
     */
    private BigDecimal parseBalance(String raw) {
        if (raw == null) return BigDecimal.ZERO;

        // Remove € or any currency symbol + all whitespace (including NBSP)
        String cleaned = raw
                .replaceAll("[\\p{Sc}€]", "")         // currency symbols
                .replaceAll("[\\s\\u00A0]", "");      // spaces + NBSP

        // Remove thousand separators (.)
        cleaned = cleaned.replace(".", "");

        // Change decimal comma to dot
        cleaned = cleaned.replace(",", ".");

        if (cleaned.isEmpty()) return BigDecimal.ZERO;

        return new BigDecimal(cleaned);
    }


    public void clickSettingsNav() {
        waitLoaded();
        WebElement settings = wait.until(ExpectedConditions.elementToBeClickable(NAV_SETTINGS));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", settings);
        try {
            settings.click();
        } catch (ElementClickInterceptedException e) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", settings);
        }
        // Espera rota /settings
        wait.until(ExpectedConditions.urlContains("/settings"));
    }

    public void clickPaymentsNav(){
        wait.until(ExpectedConditions.elementToBeClickable(paymentsNav)).click();
    }


}