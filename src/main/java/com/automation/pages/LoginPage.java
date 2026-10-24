package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.constants.FrameworkConstants;
import com.automation.utils.ConfigReader;
import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    private static final By FLASH_MESSAGE = By.id("flash");

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(ConfigReader.get("baseUrl") + FrameworkConstants.LOGIN_PATH);
        return this;
    }

    public LoginPage enterUsername(String username) {
        WaitUtils.visible(driver, By.id("username"));
        usernameInput.clear();
        usernameInput.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WaitUtils.visible(driver, By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys(password);
        return this;
    }

    public void submit() {
        WaitUtils.clickable(driver, By.cssSelector("button[type='submit']"));
        loginButton.click();
    }

    public String getFlashMessage() {
        return WaitUtils.visible(driver, FLASH_MESSAGE).getText().replace("×", "").trim();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
