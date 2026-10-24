package com.automation.listeners;

import com.automation.drivers.DriverManager;
import com.automation.utils.ScreenshotUtils;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.nio.file.Path;

public class TestListener implements ITestListener, ISuiteListener {
    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ISuite suite) {
        ExtentReportManager.initialize();
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentReportManager.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportManager.createTest(result.getMethod().getQualifiedName())
                .assignCategory(result.getTestContext().getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getTest().pass("Test passed");
        LOGGER.info("Test passed: {}", result.getName());
        ExtentReportManager.clearTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentReportManager.getOrCreateTest(result.getMethod().getQualifiedName()).fail(result.getThrowable());
        LOGGER.error("Test failed: {}", result.getName(), result.getThrowable());
        try {
            Path screenshot = ScreenshotUtils.capture(DriverManager.getDriver(), result.getName());
            ExtentReportManager.getTest().fail("Failure screenshot",
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshot.toString()).build());
        } catch (Exception exception) {
            LOGGER.error("Unable to capture or attach failure screenshot for {}", result.getName(), exception);
            ExtentReportManager.getTest().warning("Failure screenshot unavailable: "
                    + exception.getMessage());
        } finally {
            ExtentReportManager.clearTest();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (result.getThrowable() == null) {
            ExtentReportManager.getOrCreateTest(result.getMethod().getQualifiedName()).skip("Test skipped");
        } else {
            ExtentReportManager.getOrCreateTest(result.getMethod().getQualifiedName()).skip(result.getThrowable());
        }
        LOGGER.warn("Test skipped: {}", result.getName());
        ExtentReportManager.clearTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("Finished test context: {}", context.getName());
    }
}
