package com.automation.listeners;

import com.automation.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger LOGGER = LogManager.getLogger(RetryAnalyzer.class);
    private int attempts;

    @Override
    public synchronized boolean retry(ITestResult result) {
        int retryCount = ConfigReader.getInt("retryCount");
        if (attempts < retryCount) {
            attempts++;
            LOGGER.warn("Retrying {} (attempt {} of {})", result.getName(), attempts, retryCount);
            return true;
        }
        return false;
    }
}
