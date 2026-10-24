package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrderCompletePage extends BasePage {
    private static final By COMPLETE_HEADER = By.className("complete-header");

    public OrderCompletePage(WebDriver driver) {
        super(driver);
    }

    public String getConfirmationMessage() {
        return WaitUtils.visible(driver, COMPLETE_HEADER).getText().trim();
    }
}
