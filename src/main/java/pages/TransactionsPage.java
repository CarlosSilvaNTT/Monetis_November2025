package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class TransactionsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By NAV_TRANSACTIONS = By.xpath(
            "//div[@class='navigation']//div[@class='options']/div[span[normalize-space()='Transactions']]"
    );

    private static final By TRANSACTIONS_ROOT = By.cssSelector("div.transactions");
    private static final By LOADING_OVERLAY   = By.cssSelector("div.loading_screen");
    private static final By TRANSACTION_ROWS  = By.cssSelector("div.transactions-list div.transaction");
    private static final By AMOUNT_IN_ROW     = By.cssSelector(".amount");
    private static final By CATEGORY_IN_ROW   = By.cssSelector(".category");

    public TransactionsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    /** Abre via menu e espera DOM + fim de loading da página */
    public void openViaMenu() {
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(NAV_TRANSACTIONS));
        menu.click();
        waitPageReady();
    }

    /** Garante que a página e a lista estão prontas */
    private void waitPageReady() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(TRANSACTIONS_ROOT));
        // overlay pode estar "hidden" mas por segurança aguardamos invisibilidade
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
        } catch (TimeoutException ignore) { /* overlay pode nem existir, segue */ }

        // aguarda haver (pelo menos) 1 linha carregada
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> !d.findElements(TRANSACTION_ROWS).isEmpty());
    }

    /***
     * Espera até que UMA transação com o montante esperado (qualquer formatação) esteja presente.
     * Ex.: "-1000€", "-1.000,00 €", "-1 000,00€", "-1000.00 €"
     */
    public boolean waitUntilTransactionAmountAppears(String expectedRaw, Duration timeout) {
        final String expectedCanonical = canonicalAmount(expectedRaw);

        try {
            return new WebDriverWait(driver, timeout)
                    .until(d -> {
                        List<WebElement> rows = d.findElements(TRANSACTION_ROWS);
                        for (WebElement row : rows) {
                            WebElement amountEl;
                            try {
                                amountEl = row.findElement(AMOUNT_IN_ROW);
                            } catch (NoSuchElementException e) {
                                continue;
                            }
                            String uiCanonical = canonicalAmount(amountEl.getText());
                            if (uiCanonical.equals(expectedCanonical)) {
                                return true;
                            }
                        }
                        // dispara um pequeno re-check forçando o DOM a “respirar”
                        return false;
                    });
        } catch (TimeoutException te) {
            return false;
        }
    }

    /**
     * Variante que confirma também a category (por exemplo: "To BH1895685301458576227").
     * Útil para garantir que estamos a validar MESMO a transferência para other account.
     */
    public boolean waitUntilTransactionAmountAndCategoryAppear(String expectedRaw, String expectedCategoryContains, Duration timeout) {
        final String expectedCanonical = canonicalAmount(expectedRaw);

        try {
            return new WebDriverWait(driver, timeout)
                    .until(d -> {
                        List<WebElement> rows = d.findElements(TRANSACTION_ROWS);
                        for (WebElement row : rows) {
                            WebElement amountEl;
                            try {
                                amountEl = row.findElement(AMOUNT_IN_ROW);
                            } catch (NoSuchElementException e) {
                                continue;
                            }
                            String uiCanonical = canonicalAmount(amountEl.getText());
                            if (!uiCanonical.equals(expectedCanonical)) {
                                continue;
                            }

                            // Confirma também a category
                            String categoryText = "";
                            try {
                                categoryText = row.findElement(CATEGORY_IN_ROW).getText();
                            } catch (NoSuchElementException ignore) { /* category pode não existir */ }

                            if (categoryText != null && categoryText.contains(expectedCategoryContains)) {
                                return true;
                            }
                        }
                        return false;
                    });
        } catch (TimeoutException te) {
            return false;
        }
    }

    // ---------- Normalização canónica (sem startsWith/substring/length) ----------
    private String canonicalAmount(String raw) {
        if (raw == null) return "";

        String cleaned = raw
                .replace("€", "")
                .replace("\u00A0", "")  // NBSP
                .replace(" ", "")
                .replace(".", "")       // milhares
                .replace(",", ".")      // decimal
                .replace("−", "-")      // minus unicode
                .replace("–", "-")
                .replace("—", "-")
                .trim();

        boolean negative = cleaned.matches("^-.*");
        String unsigned  = cleaned.replaceFirst("^-", "");

        java.math.BigDecimal bd;
        try {
            bd = new java.math.BigDecimal(unsigned);
        } catch (NumberFormatException e) {
            return (negative ? "-" : "") + unsigned;
        }

        bd = bd.stripTrailingZeros();
        if (bd.scale() < 0) bd = bd.setScale(0);

        return (negative ? "-" : "") + bd.toPlainString();
    }
}

