package com.automation.drivers;

import org.openqa.selenium.WebDriver;

public final class DriverManager {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
        throw new AssertionError("Utility class");
    }

    public static void initializeDriver(BrowserType browser) {
        if (DRIVER.get() != null) {
            throw new IllegalStateException("A WebDriver is already initialized on this thread");
        }
        DRIVER.set(DriverFactory.createDriver(browser));
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized on this thread");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }
}
