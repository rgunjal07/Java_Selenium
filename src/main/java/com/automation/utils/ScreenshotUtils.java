package com.automation.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
        throw new AssertionError("Utility class");
    }

    public static Path capture(WebDriver driver, String testName) throws IOException {
        Path directory = Path.of(ConfigReader.get("screenshotPath"));
        Files.createDirectories(directory);
        String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path screenshot = directory.resolve(safeName + "-" + LocalDateTime.now().format(TIMESTAMP) + ".png");
        Files.copy(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                screenshot, StandardCopyOption.REPLACE_EXISTING);
        return screenshot;
    }
}
