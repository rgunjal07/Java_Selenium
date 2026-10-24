package com.automation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class JsonDataReader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() {
        throw new AssertionError("Utility class");
    }

    public static List<Map<String, String>> readRecords(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("JSON data file does not exist: " + path.toAbsolutePath());
        }
        List<Map<String, String>> records = MAPPER.readValue(path.toFile(),
                new TypeReference<>() { });
        if (records.isEmpty()) {
            throw new IllegalArgumentException("JSON data file contains no records: " + path);
        }
        return records;
    }
}
