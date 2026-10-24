package com.automation.utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExcelDataReader {
    private ExcelDataReader() {
        throw new AssertionError("Utility class");
    }

    public static List<Map<String, String>> readSheet(Path path, String sheetName) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("Excel data file does not exist: " + path.toAbsolutePath());
        }
        try (Workbook workbook = WorkbookFactory.create(path.toFile())) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Worksheet not found: " + sheetName);
            }
            Iterator<Row> rows = sheet.rowIterator();
            if (!rows.hasNext()) {
                throw new IllegalArgumentException("Worksheet has no header row: " + sheetName);
            }
            DataFormatter formatter = new DataFormatter();
            Row header = rows.next();
            List<String> columns = new ArrayList<>();
            header.forEach(cell -> columns.add(formatter.formatCellValue(cell).trim()));
            if (columns.isEmpty() || columns.stream().anyMatch(String::isBlank)) {
                throw new IllegalArgumentException("Worksheet contains an empty column header: " + sheetName);
            }

            List<Map<String, String>> records = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Map<String, String> record = new LinkedHashMap<>();
                for (int index = 0; index < columns.size(); index++) {
                    String value = row.getCell(index) == null ? "" : formatter.formatCellValue(row.getCell(index));
                    record.put(columns.get(index), value);
                }
                if (record.values().stream().anyMatch(value -> !value.isBlank())) {
                    records.add(record);
                }
            }
            if (records.isEmpty()) {
                throw new IllegalArgumentException("Worksheet has no data rows: " + sheetName);
            }
            return records;
        }
    }
}
