package com.automation.listeners;

import com.automation.utils.ConfigReader;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ExtentReportManager {
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();
    private static ExtentReports reports;

    private ExtentReportManager() {
        throw new AssertionError("Utility class");
    }

    public static synchronized void initialize() {
        if (reports != null) {
            return;
        }
        Path reportPath = Path.of(ConfigReader.get("reportPath"));
        try {
            Path parent = reportPath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Unable to create report directory", exception);
        }
        ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath.toString());
        reporter.config().setDocumentTitle("Selenium Automation Results");
        reporter.config().setReportName("Test Execution Report");
        reports = new ExtentReports();
        reports.attachReporter(reporter);
        reports.setSystemInfo("Environment", ConfigReader.get("environment", "qa"));
    }

    public static synchronized ExtentTest createTest(String name) {
        initialize();
        ExtentTest test = reports.createTest(name);
        TEST.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        ExtentTest test = TEST.get();
        if (test == null) {
            throw new IllegalStateException("Extent test has not been created for this thread");
        }
        return test;
    }

    public static ExtentTest getOrCreateTest(String name) {
        ExtentTest test = TEST.get();
        return test == null ? createTest(name) : test;
    }

    public static void clearTest() {
        TEST.remove();
    }

    public static synchronized void flush() {
        if (reports != null) {
            reports.flush();
        }
    }
}
