package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.models.Product;
import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {
    private static final By CART_ITEMS = By.className("cart_item");
    private static final By PRODUCT_NAME = By.className("inventory_item_name");
    private static final By PRODUCT_PRICE = By.className("inventory_item_price");
    private static final By CHECKOUT_BUTTON = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<Product> getProducts() {
        WaitUtils.visible(driver, CART_ITEMS);
        return driver.findElements(CART_ITEMS).stream()
                .map(this::toProduct)
                .toList();
    }

    public void proceedToCheckout() {
        click(CHECKOUT_BUTTON);
    }

    private Product toProduct(WebElement item) {
        return new Product(
                item.findElement(PRODUCT_NAME).getText().trim(),
                InventoryPage.parsePrice(item.findElement(PRODUCT_PRICE).getText()));
    }
}
