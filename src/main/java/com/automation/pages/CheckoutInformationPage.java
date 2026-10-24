package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutInformationPage extends BasePage {
    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By OVERVIEW = By.id("checkout_summary_container");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
    }

    public void enterInformationAndContinue() {
        type(FIRST_NAME, ConfigReader.get("firstName"));
        type(LAST_NAME, ConfigReader.get("lastName"));
        type(POSTAL_CODE, ConfigReader.get("postalCode"));
        click(CONTINUE_BUTTON);
        WaitUtils.visible(driver, OVERVIEW);
    }
}
