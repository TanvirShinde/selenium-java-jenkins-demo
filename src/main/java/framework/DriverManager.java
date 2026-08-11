package framework;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory. Uses a ThreadLocal so tests can run in
 * parallel (TestNG parallel="methods"/"classes") without drivers colliding.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void initDriver(String browser) {
        WebDriver driver;
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        switch (browser == null ? "chrome" : browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (headless) ffOptions.addArguments("-headless");
                driver = new FirefoxDriver(ffOptions);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments(
                        "--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--window-size=1920,1080",
                        "--disable-gpu"
                );
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().deleteAllCookies();
        driverThreadLocal.set(driver);
    }

    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            driverThreadLocal.remove();
        }
    }
}
