package tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import framework.DriverManager;

public class BaseTest {

    protected WebDriver driver;
    protected static final Properties config = new Properties();

    @BeforeSuite(alwaysRun = true)
    public void loadConfig() throws IOException {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            config.load(in);
        }
    }

    @Parameters({"browser"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        // system property (-Dbrowser=) passed from Maven/Jenkins wins if present
        String resolvedBrowser = System.getProperty("browser", browser);
        DriverManager.initDriver(resolvedBrowser);
        driver = DriverManager.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            // hook point: screenshot capture could go here
            Throwable throwable = result.getThrowable();
            System.out.println("Test failed: " + result.getName()
                    + " -- " + (throwable != null ? throwable : "Unknown failure"));
        }
        DriverManager.quitDriver();
    }

    protected String cfg(String key) {
        return config.getProperty(key);
    }
}
