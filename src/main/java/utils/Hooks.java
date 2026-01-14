package utils;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import java.time.Duration;

import static org.apache.logging.log4j.core.util.ExecutorServices.ensureInitialized;


public class Hooks {

    @Getter
    public static WebDriver driver;
    public static String baseURL;


    @Before(order = 0)
    public void setUp() {

        ensureInitialized();  // garante driver + BASE_URL
        if (baseURL != null && !baseURL.isEmpty()) {
            driver.get(baseURL);
        }
    }

    public static synchronized void ensureInitialized() {
        if (driver != null) return;

        Dotenv dotenv = Dotenv.load();
        baseURL = dotenv.get("BASE_URL");



        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();


// Abre a homepage (fallback caso não uses REGISTER_URL direto no step)

        if (baseURL != null && !baseURL.isEmpty()) {
            driver.get(baseURL);
        }

    }

    @After

    public void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
    }


    public static WebDriver getDriver() {
        return driver;
    }


}







