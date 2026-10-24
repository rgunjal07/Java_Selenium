package com.automation.base;

import com.automation.drivers.BrowserType;
import com.automation.drivers.DriverManager;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

public abstract class BaseTest {
    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(@Optional("") String browserParameter) {
        String browserName = browserParameter.isBlank()
                ? ConfigReader.get("browser") : browserParameter;
        DriverManager.initializeDriver(BrowserType.from(browserName));
        WebDriver driver = DriverManager.getDriver();
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("pageLoadTimeoutSeconds")));
        driver.manage().timeouts().scriptTimeout(
                Duration.ofSeconds(ConfigReader.getInt("scriptTimeoutSeconds")));
        driver.manage().window().maximize();
        driver.get(ConfigReader.get("baseUrl"));
    }

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
