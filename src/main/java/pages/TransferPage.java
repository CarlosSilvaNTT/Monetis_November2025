package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.math.BigDecimal;
import java.time.Duration;

public class TransferPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By LOADING_OVERLAY = By.cssSelector("div.loading_screen");
    private static final By TRANSFER_H1     = By.xpath("//div[contains(@class,'content')]/h1[normalize-space()='Transfer']");

    // Radio de tipo de transferência (lado direito do form)
    private static final By OWN_ACCOUNT_TAB =
            By.xpath("//div[@class='accounts-radio']/div[span[normalize-space()='Own Account']]");


    // dropdown imediatamente a seguir ao label for="targetownaccount"
    private static final By DEST_DROPDOWN_CONTROL = By.xpath(
            "//label[@for='targetownaccount']/following::div[contains(@class,'select')][1]" +
                    "//div[contains(@class,'css-13cymwt-control')]"
    );

    // input específico do react-select para DESTINO (id termina em 4)
    private static final By DEST_INPUT = By.cssSelector("#react-select-4-input");


    // após selecionar, o react-select mostra o valor num div singleValue
    private static final By DEST_SINGLE_VALUE = By.xpath(
            "//label[@for='targetownaccount']/following::div[contains(@class,'select')][1]" +
                    "//div[contains(@class,'css-1dimb5e-singleValue')]"
    );


    // Campo Amount
    private static final By AMOUNT_INPUT = By.cssSelector("input[name='amount']");

    // Botão Next (avança para Confirmation e depois para Success)
    private static final By NEXT_BUTTON = By.xpath("//button[normalize-space()='Next']");

    // “Stepper” da direita (estado)
    private static final By STATUS_DETAILS       = By.xpath("//div[contains(@class,'status')]//div[contains(@class,'item')][span='Details']");
    private static final By STATUS_CONFIRMATION  = By.xpath("//div[contains(@class,'status')]//div[contains(@class,'item')][span='Confirmation']");
    private static final By STATUS_SUCCESS       = By.xpath("//div[contains(@class,'status')]//div[contains(@class,'item')][span='Success']");

    //Tranfer to other account
    private static final By OTHER_TARGET_INPUT = By.cssSelector("input[name='iban']");


    public TransferPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    /** Aguarda página Transfer carregada. */
    public void waitLoaded() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
        wait.until(ExpectedConditions.visibilityOfElementLocated(TRANSFER_H1));
        wait.until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").equals("complete"));
        wait.until(ExpectedConditions.urlContains("/transfer"));
    }

    /** Variante caso prefiras clicar o menu Transfer a partir daqui. */
    public void openFromDashboard() {
        // o click no menu está no AccountsPage.clickTransferNav()
        // aqui apenas validamos que já estamos na página /transfer
        waitLoaded();
    }

    /** Garante que 'Own Account' está selecionado. */
    public void ensureOwnAccountSelected() {
        WebElement own = wait.until(ExpectedConditions.elementToBeClickable(OWN_ACCOUNT_TAB));
        // Alguns UIs já vêm ativos. Se não estiver, um click basta.
        try {
            own.click();
        } catch (ElementClickInterceptedException e) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", own);
        }
        // Deve continuar na etapa Details
        wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_DETAILS));
    }

    /** Seleciona a conta de destino (ex.: "Savings") no react-select. */
    public void selectDestinationOwnAccount(String accountLabel) {
        // 1) Container of the DESTINATION select: it's the first .select after the label for "targetownaccount"
        By DEST_CONTAINER = By.xpath(
                "//label[@for='targetownaccount']/following::div[contains(@class,'select')][1]"
        );
        // The clickable control inside the container
        By DEST_CONTROL_IN_CONTAINER = By.cssSelector("div.css-13cymwt-control");
        // The input (combobox) inside the same container (no fixed numeric id)
        By COMBOBOX_INPUT_IN_CONTAINER = By.cssSelector("div.css-19bb58m input[role='combobox']");
        // The listbox (menu) that react-select renders into the BODY (dynamic id)
        By RS_MENU = By.xpath("//div[contains(@id,'react-select') and contains(@id,'-listbox')]");
        // Option with exact text (case sensitive)
        By RS_OPTION_EXACT = By.xpath(
                "//div[contains(@id,'react-select') and contains(@id,'-listbox')]//div[normalize-space()='" + accountLabel + "']"
        );
        // Option case-insensitive (fallback)
        By RS_OPTION_CI = By.xpath(
                "//div[contains(@id,'react-select') and contains(@id,'-listbox')]//div[" +
                        "translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '" +
                        accountLabel.toLowerCase() + "']"
        );
        // After selection, react-select shows a single value element inside the same container
        By DEST_SINGLE_VALUE = By.xpath(
                "//label[@for='targetownaccount']/following::div[contains(@class,'select')][1]" +
                        "//div[contains(@class,'css-1dimb5e-singleValue')]"
        );

        // Ensure any overlay is gone
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));

        WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(DEST_CONTAINER));
        WebElement control   = container.findElement(DEST_CONTROL_IN_CONTAINER);

        // Scroll and open the dropdown
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", control);
        try {
            control.click();
        } catch (ElementClickInterceptedException e) {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", control);
        }

        // 2) Prefer clicking the option in the listbox (portal) for stability
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(RS_MENU));
            WebElement option = null;
            try {
                option = wait.until(ExpectedConditions.visibilityOfElementLocated(RS_OPTION_EXACT));
            } catch (TimeoutException ignore) {
                // Fallback to case-insensitive match
                option = wait.until(ExpectedConditions.visibilityOfElementLocated(RS_OPTION_CI));
            }

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", option);
            try {
                option.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
            }

        } catch (TimeoutException noMenu) {
            // 3) Fallback: type and ENTER into the combobox inside the same container
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(COMBOBOX_INPUT_IN_CONTAINER));
            input.sendKeys(accountLabel);
            input.sendKeys(Keys.ENTER);
        }

        // 4) Verify selected value is shown in the destination select
        wait.until(ExpectedConditions.textToBePresentInElementLocated(DEST_SINGLE_VALUE, accountLabel));
    }

    public void enterAmount(BigDecimal amount) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        input.clear();
        input.sendKeys(amount.toPlainString());
    }

    public void clickNext() {
        wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON)).click();
        // quando avançamos de Details -> Confirmation, o “stepper” muda
        wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_CONFIRMATION));
    }

    public boolean isConfirmationVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_CONFIRMATION));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void confirmTransfer() {
        // o botão “Next” avança também de Confirmation -> Success
        wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_SUCCESS));
    }

    public boolean isSuccessVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_SUCCESS));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    //transfer to other account
    public void selectOtherAccountOption() {
        WebElement otherTab = wait.until(ExpectedConditions.elementToBeClickable(OTHER_TARGET_INPUT));
        otherTab.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'status')]//span[text()='Details']")
        ));
    }


    public void enterOtherAccountTarget(String value) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(OTHER_TARGET_INPUT));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        input.click();
        input.clear();
        input.sendKeys(value);
    }


}

