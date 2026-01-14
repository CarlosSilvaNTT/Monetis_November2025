//
//package pages;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import utils.DriverManager;
//
//public class LoginPage {
//    WebDriver driver = DriverManager.getDriver();
//
//    private By usernameField = By.id("email");
//    private By passwordField = By.id("password");
//    private By loginButton = By.cssSelector("button[type='submit']");
//    private By dashboardElement = By.xpath("//h1[contains(text(),'Dashboard')]");
//
//    public void navigateToLoginPage() {
//        driver.get("https://monetis-delta.vercel.app/login");
//    }
//
//    public void enterCredentials(String username, String password) {
//        driver.findElement(usernameField).sendKeys(username);
//        driver.findElement(passwordField).sendKeys(password);
//    }
//
//    public void clickLoginButton() {
//        driver.findElement(loginButton).click();
//    }
//
//    public void verifyDashboardPage() {
//        boolean isDashboardVisible = driver.findElement(dashboardElement).isDisplayed();
//        if (!isDashboardVisible) {
//            throw new AssertionError("Dashboard page not displayed after login!");
//        }
//    }
//}

