package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.models.OrderSummary;
import com.automation.models.Product;
import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutOverviewPage extends BasePage {
    private static final By OVERVIEW = By.id("checkout_summary_container");
    private static final By CART_ITEMS = By.className("cart_item");
    private static final By PRODUCT_NAME = By.className("inventory_item_name");
    private static final By PRODUCT_PRICE = By.className("inventory_item_price");
    private static final By SUBTOTAL = By.className("summary_subtotal_label");
    private static final By TAX = By.className("summary_tax_label");
    private static final By TOTAL = By.className("summary_total_label");
    private static final By FINISH_BUTTON = By.id("finish");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public OrderSummary getOrderSummary() {
        WaitUtils.visible(driver, OVERVIEW);
        List<Product> products = driver.findElements(CART_ITEMS).stream()
                .map(this::toProduct)
                .toList();
        return new OrderSummary(
                products,
                amountFromLabel(SUBTOTAL),
                amountFromLabel(TAX),
                amountFromLabel(TOTAL));
    }

    public void finishOrder() {
        click(FINISH_BUTTON);
    }

    private Product toProduct(WebElement item) {
        return new Product(
                item.findElement(PRODUCT_NAME).getText().trim(),
                InventoryPage.parsePrice(item.findElement(PRODUCT_PRICE).getText()));
    }

    private BigDecimal amountFromLabel(By locator) {
        String text = WaitUtils.visible(driver, locator).getText();
        int currencyStart = text.indexOf('$');
        if (currencyStart < 0) {
            throw new IllegalArgumentException("Currency amount not found in checkout label: " + text);
        }
        return InventoryPage.parsePrice(text.substring(currencyStart));
    }
}
