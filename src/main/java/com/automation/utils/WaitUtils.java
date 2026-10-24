package com.automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {
    private WaitUtils() {
        throw new AssertionError("Utility class");
    }

    public static WebElement visible(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds")))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement clickable(WebDriver driver, By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds")))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean textPresent(WebDriver driver, By locator, String text) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds")))
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
}
