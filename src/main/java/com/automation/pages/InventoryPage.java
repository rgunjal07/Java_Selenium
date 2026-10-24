package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.models.Product;
import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

public class InventoryPage extends BasePage {
    private static final By INVENTORY_ITEMS = By.cssSelector(".inventory_item");
    private static final By CART_LINK = By.className("shopping_cart_link");
    private static final By PRODUCT_NAME = By.className("inventory_item_name");
    private static final By PRODUCT_PRICE = By.className("inventory_item_price");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public List<Product> getProducts() {
        WaitUtils.visible(driver, INVENTORY_ITEMS);
        return driver.findElements(INVENTORY_ITEMS).stream()
                .map(item -> new Product(
                        item.findElement(PRODUCT_NAME).getText().trim(),
                        parsePrice(item.findElement(PRODUCT_PRICE).getText())))
                .toList();
    }

    public void addToCart(Product product) {
        WebElement item = findInventoryItem(product.name());
        item.findElement(By.cssSelector("button[data-test^='add-to-cart']")).click();
    }

    public void openCart() {
        click(CART_LINK);
    }

    private WebElement findInventoryItem(String productName) {
        return driver.findElements(INVENTORY_ITEMS).stream()
                .filter(item -> item.findElement(PRODUCT_NAME).getText().trim().equals(productName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product is not present in inventory: " + productName));
    }

    static BigDecimal parsePrice(String value) {
        try {
            return new BigDecimal(value.replace("$", "").replace(",", "").trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Unable to parse product price: " + value, exception);
        }
    }
}
