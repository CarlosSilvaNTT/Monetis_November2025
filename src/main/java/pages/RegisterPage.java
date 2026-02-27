
package pages;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;

import java.time.Duration;
import java.time.Instant;

public class RegisterPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        PageFactory.initElements(driver, this);
    }

    // ---- Navegação (link da homepage) ----
    @FindBy(xpath = "//a[normalize-space()='Start your journey']")
    WebElement startYourJourneyLink;

    // ------------------- Form fields -------------------
    @FindBy(xpath = "//input[@placeholder='John' or @name='firstName' or contains(@id,'firstName')]")
    private WebElement namefield;

    @FindBy(xpath = "//input[@placeholder='Doe' or @name='lastName' or contains(@id,'lastName')]")
    private WebElement surnamefield;

    @FindBy(xpath = "//input[@placeholder='johndoe@me.com' or @type='email']")
    private WebElement emailfield;

    @FindBy(xpath = "//input[@placeholder='e.g. 123456789' or @name='phone' or contains(@id,'phone')]")
    private WebElement phonefield;

    // Some apps had a typo 'Sttr.' in placeholder; we include both
    @FindBy(xpath = "//input[@placeholder='e.g. Sttr. Example, 123' or @placeholder='e.g. Str. Example, 123' or @name='street' or contains(@id,'street')]")
    private WebElement streetfield;

    @FindBy(xpath = "//input[@placeholder='e.g. 12345-678' or @name='postalCode' or contains(@id,'postal')]")
    private WebElement postalfield;

    @FindBy(xpath = "//input[@placeholder='London' or @name='city' or contains(@id,'city')]")
    private WebElement cityField;

    @FindBy(xpath = "//input[@placeholder='Password' and (@type='password' or @name='password')]")
    private WebElement passfield;

    @FindBy(xpath = "//input[@placeholder='Confirm password' and (@type='password' or @name='confirm' or contains(@id,'confirm'))]")
    private WebElement passconfirmfield;

    // Submit
    @FindBy(xpath = "//button[@type='submit']")
    private WebElement submitButton;


    // --- locators auxiliares ---

    private static final By COOKIE_BANNER_CLOSE = By.cssSelector(
            "[id*='cookie' i] [aria-label*='close' i], [class*='cookie' i] [aria-label*='close' i], " +
                    "[id*='cookie' i] button, [class*='cookie' i] button"
    );
    private static final By GENERIC_TOAST = By.cssSelector("[role='alert'], .toast, [class*='toast' i]");
    private static final By FORM_SELECTOR = By.cssSelector("form");

    //---Se email já existe

    @FindBy(xpath = "//a[@class='signUp' or contains(.,'Sign in') or contains(.,'Login')]")
    private WebElement loginLink;  // link para voltar ao Login (apesar do nome 'signUp' no class)


    // O teu alvo específico (se continuar a existir)
    private static final By EMAIL_IN_USE_CLOSE_BUTTON = By.xpath("//*[@id='1']/button");


    By ANY_LOGIN_ANCHOR = By.xpath("//a[normalize-space()='Login' or contains(normalize-space(.),'Sign in')]");
    By ANY_LOGIN_BUTTON = By.xpath("//button[normalize-space()='Login' or .//span[normalize-space()='Login']]");
    By GET_STARTED      = By.xpath("//span[normalize-space()='Get Started' or normalize-space()='Get started']");




    // --- utility: close overlays that may intercept clicks ---
    private void dismissOverlaysIfPresent() {
        try {
            for (WebElement el : driver.findElements(COOKIE_BANNER_CLOSE)) {
                if (el.isDisplayed()) {
                    try {
                        el.click();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                    }
                }
            }

            // small pause if toasts/overlays are on the screen
            if (driver.findElements(GENERIC_TOAST).stream().anyMatch(WebElement::isDisplayed)) {
                Thread.sleep(400);
            }
        } catch (Exception ignore) {
        }
    }

    // --- utility: check disabled state (HTML & ARIA) ---
    private boolean isDisabled(WebElement el) {
        try {
            String disabled = el.getAttribute("disabled");
            String aria = el.getAttribute("aria-disabled");
            return disabled != null || "true".equalsIgnoreCase(aria);
        } catch (Exception e) {
            return false;
        }
    }


    // Keeping the same (typo preserved) to match your existing codebase
    @FindBy(xpath = "//*[contains(text(),'Welcome,')]")
    private WebElement welcomeMessage;

    // ------------------- Country (React-Select primary) -------------------
    // Placeholder (used as a last-resort click target)
    @FindBy(xpath = "//div[@id='react-select-2-placeholder']")
    private WebElement countryfieldPlaceholder;

    // React-Select control wrapper (clickable)
    @FindBy(css = "[class*='react-select'] [class*='control']")
    private WebElement countryControl;

    // Native <select> fallback if present
    @FindBy(xpath = "//select[contains(@id,'country') or contains(@name,'country') or contains(@aria-label,'Country')]")
    private WebElement countrySelect;

    // ------------------- Terms checkbox -------------------
    // We'll use explicit methods to locate and interact, not @FindBy directly
    private static final By TERMS_INPUT_XPATH = By.xpath("//input[@id='terms']");
    private static final By TERMS_LABEL_XPATH = By.xpath("//label[@for='terms']");
    private static final By HOME = By.xpath("//div[@class='home-navigation']");

    // ------------------- Navigation -------------------

    public void navigateToRegisterPage() {

        Dotenv dotenv = Dotenv.load();
        String registerUrl = dotenv.get("REGISTER_URL");
        if (registerUrl == null || registerUrl.isEmpty()) {
            throw new IllegalStateException("REGISTER_URL not found in .env");
        }
        driver.get(registerUrl);
    }


    // ------------------- Public API -------------------
    public void fillRegistrationForm(
            String firstName,
            String lastName,
            String email,
            String phone,
            String street,
            String postalCode,
            String city,
            String countryText,
            String PASSWORD,
            String confirmPassword
    ) {
        type(namefield, safeTrim(firstName));
        type(surnamefield, safeTrim(lastName));
        type(emailfield, safeTrim(email));
        type(phonefield, safeTrim(phone));
        type(streetfield, safeTrim(street));
        type(postalfield, safeTrim(postalCode));
        type(cityField, safeTrim(city));

        selectCountry(safeTrim(countryText)); // React-Select with native fallback

        type(passfield, PASSWORD);      // don't trim passwords
        type(passconfirmfield, confirmPassword);
    }

    public void submit() {

        dismissOverlaysIfPresent();

        WebElement clickable = wait.until(ExpectedConditions.visibilityOf(submitButton));
        scrollIntoView(clickable);

        try {
            // prefer explicit clickable wait + normal click
            wait.until(ExpectedConditions.elementToBeClickable(clickable)).click();

        } catch (ElementClickInterceptedException e) {
            // Something is overlaying the button; try Actions first, then JS
            try {
                new Actions(driver).moveToElement(clickable).click().perform();
            } catch (Exception ignore) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
            }

        } catch (ElementNotInteractableException e) {
            // Element is in DOM but not interactable; try JS click directly
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);

        } catch (TimeoutException e) {
            // Never reached 'clickable' state; last resort, JS click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
        }
    }




    /** 1) Clica no botão pai do <svg> (//*[@id='1']/button) para fechar a mensagem de erro */
    public void clickEmailAlreadyInUseCloseButton() {
        // Espera presença (não usa "clickable" ainda para evitar Timeout prematuro)
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(EMAIL_IN_USE_CLOSE_BUTTON));
        scrollIntoView(btn);

        // Tenta clique "normal"
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
            // Espera desaparecer (invisibilidade do botão)
            wait.until(ExpectedConditions.invisibilityOfElementLocated(EMAIL_IN_USE_CLOSE_BUTTON));

        } catch (ElementClickInterceptedException e) {
            // Algo a cobrir o botão → tenta clique via JS
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            try {

                wait.until(ExpectedConditions.invisibilityOfElementLocated(EMAIL_IN_USE_CLOSE_BUTTON)); } catch (Exception ignore) {}

        } catch (ElementNotInteractableException e) {
            // Em DOM mas não interativo → JS click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            try { wait.until(ExpectedConditions.invisibilityOfElementLocated(EMAIL_IN_USE_CLOSE_BUTTON)); } catch (Exception ignore) {}

        } catch (TimeoutException e) {
            // Nunca ficou "clickable" dentro do tempo → tenta JS mesmo assim
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            try { wait.until(ExpectedConditions.invisibilityOfElementLocated(EMAIL_IN_USE_CLOSE_BUTTON)); } catch (Exception ignore) {}

        } catch (StaleElementReferenceException e) {
            // Re-localiza e tenta novamente
            WebElement fresh = driver.findElement(EMAIL_IN_USE_CLOSE_BUTTON);
            scrollIntoView(fresh);
            try {
                fresh.click();
            } catch (Exception e2) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fresh);
            }
            try { wait.until(ExpectedConditions.invisibilityOfElementLocated(EMAIL_IN_USE_CLOSE_BUTTON)); } catch (Exception ignore) {}
        }
    }

    /** 2) Clica no link //a[@class='signUp'] para ir para Login */
    public void goToLoginViaSignUpLink() {

        try {
            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
            scrollIntoView(link);
            try { link.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link); }
            return;
        } catch (TimeoutException ignore) {
            // continua para fallbacks
        }

        for (By locator : new By[]{ANY_LOGIN_ANCHOR, ANY_LOGIN_BUTTON, GET_STARTED}) {
            try {
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
                scrollIntoView(el);
                try { el.click(); }
                catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); }
                return;
            } catch (TimeoutException ignore) { /* tenta o próximo */ }
        }

        try {
            Dotenv dotenv = Dotenv.load();
            String loginUrl = dotenv.get("LOGIN_URL");
            if (loginUrl != null && !loginUrl.isEmpty()) {
                driver.get(loginUrl);
                return;
            }
        } catch (Exception ignore) {}

        String url = driver.getCurrentUrl();
        if (url != null && url.contains("/register")) {
            driver.navigate().to(url.replace("/register", "/login"));
        } else {
            // se não for possível inferir, tenta a rota /login
            driver.navigate().to("/login");
        }


    }

    /** 3) Fluxo completo: fecha a mensagem (se visível) e vai para Login */
    public void closeErrorAndGoToLogin() {
        // Só tenta clicar no botão se a mensagem/botão estiver visível
        if (elementVisible(EMAIL_IN_USE_CLOSE_BUTTON)) {
            clickEmailAlreadyInUseCloseButton();
        }
        goToLoginViaSignUpLink();


    }







                public boolean isSuccessMessageVisible() {
        try {
            WebElement banner = wait.until(ExpectedConditions.visibilityOf(welcomeMessage));
            return banner != null && banner.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Accept the terms checkbox (id='terms') with robust fallbacks */
    public void acceptTerms() {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(TERMS_INPUT_XPATH));
        scrollIntoView(input);

        // already selected?
        if (isCheckboxSelected(input)) return;

        // Try clicking the label first (often more reliable)
//        try {
//            WebElement label = wait.until(ExpectedConditions.elementToBeClickable(TERMS_LABEL_XPATH));
//            scrollIntoView(label);
//            label.click();
//        } catch (Exception e) {
//            // Fallback: click input directly
            clickCheckboxDirect(input);
//        }

        // If still not selected, force via JS + React events
        if (!isCheckboxSelected(input)) {
            setCheckboxWithJS(input, true);
        }



    }

    // ------------------- Country selection -------------------
    private void selectCountry(String countryText) {
        if (tryReactSelectCountry(countryText)) {
            return;
        }
        tryNativeSelectCountry(countryText);
    }

    /** React-Select flow: open control, type, pick exact option by text */
    private boolean tryReactSelectCountry(String countryText) {
        By controlLocator = By.cssSelector("[class*='react-select'] [class*='control']");
        // Use role='combobox' to ensure we get the interactive input element
        By inputLocator   = By.cssSelector("input[id^='react-select-'][id$='-input'][role='combobox']");
        By listboxLocator = By.cssSelector("div[role='listbox']");
        // Exact XPath for an option by text (e.g., "Portugal")
        By optionByText   = By.xpath(
                "//div[@role='listbox']//*[(@role='option' or contains(@class,'option')) and normalize-space(.)='" + countryText + "']"
        );

        WebElement input;
        try {
            // Prefer clicking the control (wrapper)
            WebElement control = wait.until(ExpectedConditions.visibilityOfElementLocated(controlLocator));
            scrollIntoView(control);
            try {
                control.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", control);
            }

            // Focus the actual input
            input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputLocator));
        } catch (TimeoutException e) {
            // Fallback: click placeholder to open, then find input
            try {
                scrollIntoView(countryfieldPlaceholder);
                try {
                    countryfieldPlaceholder.click();
                } catch (ElementClickInterceptedException ice) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", countryfieldPlaceholder);
                }
                input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputLocator));
            } catch (TimeoutException stillMissing) {
                return false; // React-Select not present
            }
        }

        // Ensure input focused
        scrollIntoView(input);
        try {
            input.click();
        } catch (ElementClickInterceptedException e) {
            new Actions(driver).moveToElement(input).click().perform();
        }

        // Type country (clear first)
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(countryText);

        // Wait for options to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(listboxLocator));

        // Click the exact option
        try {
            WebElement opt = wait.until(ExpectedConditions.elementToBeClickable(optionByText));
            scrollIntoView(opt);
            try {
                opt.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
            }
        } catch (TimeoutException e) {
            // If explicit option click failed, press ENTER to accept the top match
            input.sendKeys(Keys.ENTER);
        }

        // Optional: verify selection by reading the single-value display
        try {
            WebElement singleValue = driver.findElement(By.cssSelector("[class*='react-select__single-value']"));
            String selected = singleValue.getText().trim();
            return countryText.equals(selected);
        } catch (NoSuchElementException ignore) {
            return true; // assume success if single-value not present
        }
    }

    /** Native <select> fallback */
    private void tryNativeSelectCountry(String countryText) {
        // 1) Wired element
        try {
            if (countrySelect != null && countrySelect.isDisplayed()) {
                scrollIntoView(countrySelect);
                Select sel = new Select(countrySelect);
                try {
                    sel.selectByVisibleText(countryText);
                } catch (NoSuchElementException e) {
                    // Common value for Portugal
                    sel.selectByValue("PT");
                }
                return;
            }
        } catch (Exception ignored) { }

        // 2) Discover a select that looks like "country"
        WebElement fallbackSelect = null;
        try {
            fallbackSelect = driver.findElement(
                    By.cssSelector("select[id*='country'], select[name*='country'], select[aria-label*='Country']")
            );
        } catch (NoSuchElementException ignored) { }

        if (fallbackSelect != null) {
            Select sel = new Select(fallbackSelect);
            try {
                sel.selectByVisibleText(countryText);
            } catch (NoSuchElementException e) {
                sel.selectByValue("PT");
            }
        } else {
            throw new RuntimeException("Country selector not found (React-Select or native).");
        }
    }

    // ------------------- Checkbox helpers -------------------
    private boolean isCheckboxSelected(WebElement input) {
        try {
            return input.isSelected();
        } catch (StaleElementReferenceException e) {
            WebElement fresh = driver.findElement(TERMS_INPUT_XPATH);
            return fresh.isSelected();
        }
    }

    private void clickCheckboxDirect(WebElement input) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(input));
            scrollIntoView(input);
            try {
                input.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);
            }
        } catch (TimeoutException te) {
            new Actions(driver).moveToElement(input).click().perform();
        }
    }

    private void setCheckboxWithJS(WebElement input, boolean checked) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "arguments[0].checked = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input',  { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                input, checked
        );
    }

    // ------------------- Generic form helpers -------------------

    /** Existe pelo menos um elemento para o locator e está visível? */
    private boolean elementVisible(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return el != null && el.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }



    private void type(WebElement el, String value) {
        WebElement ready = wait.until(ExpectedConditions.visibilityOf(el));
        scrollIntoView(ready);

        try {
            ready.click(); // focus
        } catch (Exception ignored) { }

        try {
            ready.clear();
        } catch (InvalidElementStateException ignored) { /* some inputs don't support clear */ }

        try {
            ready.sendKeys(value);
            // If value didn't stick (some React inputs), set via JS + event:
            if (!valueEquals(ready, value)) {
                setValueWithJS(ready, value);
            }
        } catch (ElementNotInteractableException e) {
            // Fallback for hidden/covered inputs
            setValueWithJS(ready, value);
        }
    }

    private boolean valueEquals(WebElement el, String expected) {
        try {
            String val = el.getAttribute("value");
            return expected != null && expected.equals(val);
        } catch (Exception e) {
            return false;
        }
    }

    private void setValueWithJS(WebElement el, String value) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                el, value
        );
    }

    private void scrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el);
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }
}










