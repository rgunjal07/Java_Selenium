package com.automation.drivers;

import java.util.Locale;

public enum BrowserType {
    CHROME,
    EDGE,
    FIREFOX;

    public static BrowserType from(String browser) {
        try {
            return valueOf(browser.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported browser '" + browser
                    + "'. Supported values: chrome, edge, firefox", exception);
        }
    }
}
