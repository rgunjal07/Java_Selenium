package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.listeners.RetryAnalyzer;
import com.automation.models.OrderSummary;
import com.automation.models.Product;
import com.automation.pages.CartPage;
import com.automation.pages.CheckoutInformationPage;
import com.automation.pages.CheckoutOverviewPage;
import com.automation.pages.InventoryPage;
import com.automation.pages.OrderCompletePage;
import com.automation.pages.SauceDemoLoginPage;
import com.automation.utils.InvoicePdfGenerator;
import com.automation.utils.PdfTextReader;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class ECommerceOrderValidationTest extends BaseTest {
    private static final Logger LOGGER = LogManager.getLogger(ECommerceOrderValidationTest.class);

    @Epic("E-Commerce")
    @Feature("SauceDemo order validation")
    @Description("Selects the live inventory's highest- and lowest-priced products, completes checkout, "
            + "and verifies the generated PDF proof of purchase.")
    @Test(retryAnalyzer = RetryAnalyzer.class,
            description = "Buys dynamically discovered highest- and lowest-priced SauceDemo products and validates PDF proof")
    public void purchaseHighestAndLowestPricedProductsAndValidateInvoice() throws IOException {
        new SauceDemoLoginPage(getDriver()).login();

        InventoryPage inventory = new InventoryPage(getDriver());
        List<Product> inventoryProducts = inventory.getProducts();
        Assert.assertTrue(inventoryProducts.size() >= 2,
                "Inventory must contain at least two products to select price extremes");

        Comparator<Product> priceOrder = Comparator.comparing(Product::price).thenComparing(Product::name);
        Product leastExpensive = inventoryProducts.stream().min(priceOrder).orElseThrow();
        Product mostExpensive = inventoryProducts.stream().max(priceOrder).orElseThrow();
        Assert.assertNotEquals(leastExpensive.name(), mostExpensive.name(),
                "The inventory must provide two distinct products");

        BigDecimal expectedCartTotal = leastExpensive.price().add(mostExpensive.price());
        LOGGER.info("Selected lowest-priced product {} at {} and highest-priced product {} at {}",
                leastExpensive.name(), leastExpensive.price(), mostExpensive.name(), mostExpensive.price());

        inventory.addToCart(mostExpensive);
        inventory.addToCart(leastExpensive);
        inventory.openCart();

        CartPage cart = new CartPage(getDriver());
        List<Product> cartProducts = cart.getProducts();
        Assert.assertEquals(cartProducts.size(), 2, "Cart should contain exactly the two selected products");
        Assert.assertTrue(cartProducts.containsAll(List.of(mostExpensive, leastExpensive)),
                "Cart products must match the dynamically selected products");
        BigDecimal actualCartTotal = cartProducts.stream()
                .map(Product::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Assert.assertEquals(actualCartTotal.compareTo(expectedCartTotal), 0,
                "Cart total must equal the sum of the highest- and lowest-priced products");

        cart.proceedToCheckout();
        new CheckoutInformationPage(getDriver()).enterInformationAndContinue();

        CheckoutOverviewPage overview = new CheckoutOverviewPage(getDriver());
        OrderSummary orderSummary = overview.getOrderSummary();
        Assert.assertEquals(orderSummary.products().size(), 2,
                "Checkout summary should contain exactly the selected products");
        Assert.assertTrue(orderSummary.products().containsAll(List.of(mostExpensive, leastExpensive)),
                "Checkout summary must contain both selected products");
        Assert.assertEquals(orderSummary.subtotal().compareTo(expectedCartTotal), 0,
                "Checkout subtotal must match the two selected product prices");
        Assert.assertEquals(orderSummary.total().compareTo(orderSummary.subtotal().add(orderSummary.tax())), 0,
                "Checkout bill total must equal subtotal plus displayed tax");

        overview.finishOrder();
        String confirmation = new OrderCompletePage(getDriver()).getConfirmationMessage();
        Assert.assertTrue(confirmation.toLowerCase().contains("thank you"),
                "Order completion page should confirm the order");

        Path invoice = InvoicePdfGenerator.generate(orderSummary);
        String invoiceText = PdfTextReader.readText(invoice);
        Assert.assertTrue(Files.isRegularFile(invoice), "Generated proof-of-purchase PDF must exist");
        try (var invoiceStream = Files.newInputStream(invoice)) {
            Allure.addAttachment("Order proof of purchase", "application/pdf", invoiceStream, ".pdf");
        }
        verifyInvoiceContainsProduct(invoiceText, mostExpensive.name());
        verifyInvoiceContainsProduct(invoiceText, leastExpensive.name());
        verifyInvoiceBillAmount(invoiceText, orderSummary.total());
        LOGGER.info("Order confirmed. Validated generated invoice: {}", invoice.toAbsolutePath());
    }

    @Step("Verify generated invoice contains {productName}")
    private void verifyInvoiceContainsProduct(String invoiceText, String productName) {
        Assert.assertTrue(invoiceText.contains(productName),
                "Invoice must contain product: " + productName);
    }

    @Step("Verify invoice bill amount is {expectedAmount}")
    private void verifyInvoiceBillAmount(String invoiceText, BigDecimal expectedAmount) {
        Assert.assertTrue(invoiceText.contains("Bill Amount: "
                        + InvoicePdfGenerator.formatMoney(expectedAmount)),
                "Invoice bill amount must match checkout total");
    }
}
