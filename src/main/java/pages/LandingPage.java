
package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LandingPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Elemento chave da Landing Page
    private final By landingPageLogo = By.xpath("//img[@class='logo-home']");
    public LandingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isLandingPageVisible() {
        return
        wait.until(ExpectedConditions.visibilityOfElementLocated(landingPageLogo)).isDisplayed();
    }

    public boolean isOnLandingPage() {
        return false;
    }
}

