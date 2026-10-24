package com.automation.base;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public abstract class BasePage {
    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    protected void click(By locator) {
        WaitUtils.clickable(driver, locator).click();
    }

    protected void type(By locator, String value) {
        WebElement element = WaitUtils.visible(driver, locator);
        element.clear();
        element.sendKeys(value);
    }
}
