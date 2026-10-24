package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SauceDemoLoginPage extends BasePage {
    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By INVENTORY = By.id("inventory_container");

    public SauceDemoLoginPage(WebDriver driver) {
        super(driver);
    }

    public void login() {
        type(USERNAME, ConfigReader.get("username"));
        type(PASSWORD, ConfigReader.get("password"));
        click(LOGIN_BUTTON);
        WaitUtils.visible(driver, INVENTORY);
    }
}
