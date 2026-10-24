package com.automation.utils;

import com.automation.constants.FrameworkConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
        throw new AssertionError("Utility class");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream(FrameworkConstants.CONFIG_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Missing classpath resource: "
                        + FrameworkConstants.CONFIG_RESOURCE);
            }
            properties.load(input);
            String environment = System.getProperty("environment",
                    System.getenv().getOrDefault("ENVIRONMENT", properties.getProperty("environment", "qa")));
            try (InputStream environmentInput = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("config-" + environment.trim() + ".properties")) {
                if (environmentInput != null) {
                    properties.load(environmentInput);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load framework configuration", exception);
        }
        return properties;
    }

    public static String get(String key) {
        String environmentValue = System.getenv(toEnvironmentKey(key));
        String value = System.getProperty(key,
                environmentValue == null ? PROPERTIES.getProperty(key) : environmentValue);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing configuration value: " + key);
        }
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        String environmentValue = System.getenv(toEnvironmentKey(key));
        String value = System.getProperty(key,
                environmentValue == null ? PROPERTIES.getProperty(key, defaultValue) : environmentValue);
        return value.trim();
    }

    public static int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Configuration value must be an integer: " + key, exception);
        }
    }

    public static boolean getBoolean(String key) {
        String value = get(key);
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException("Configuration value must be true or false: " + key);
        }
        return Boolean.parseBoolean(value);
    }

    private static String toEnvironmentKey(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
}
