package com.automation.utils;

import com.automation.models.OrderSummary;
import com.automation.models.Product;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class InvoicePdfGenerator {
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private InvoicePdfGenerator() {
        throw new AssertionError("Utility class");
    }

    public static Path generate(OrderSummary summary) throws IOException {
        Path outputDirectory = Path.of(ConfigReader.get("invoicePath"));
        Files.createDirectories(outputDirectory);
        Path output = outputDirectory.resolve(
                "saucedemo-order-" + LocalDateTime.now().format(FILE_TIMESTAMP)
                        + "-" + UUID.randomUUID() + ".pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                int y = 750;
                y = writeLine(content, "SauceDemo - Order Proof of Purchase", 20, y, true);
                y = writeLine(content, "Order confirmation: " + LocalDateTime.now(), 11, y - 8, false);
                y = writeLine(content, "Products:", 12, y - 20, true);
                for (Product product : summary.products()) {
                    y = writeLine(content, product.name() + " - " + formatMoney(product.price()), 11, y - 18, false);
                }
                y = writeLine(content, "Subtotal: " + formatMoney(summary.subtotal()), 11, y - 24, false);
                y = writeLine(content, "Tax: " + formatMoney(summary.tax()), 11, y - 18, false);
                writeLine(content, "Bill Amount: " + formatMoney(summary.total()), 13, y - 22, true);
            }
            document.save(output.toFile());
        }
        return output;
    }

    public static String formatMoney(BigDecimal amount) {
        return "$" + amount.setScale(2, java.math.RoundingMode.UNNECESSARY);
    }

    private static int writeLine(PDPageContentStream content, String text, int fontSize, int y, boolean bold)
            throws IOException {
        content.beginText();
        content.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, fontSize);
        content.newLineAtOffset(50, y);
        content.showText(text);
        content.endText();
        return y;
    }
}
