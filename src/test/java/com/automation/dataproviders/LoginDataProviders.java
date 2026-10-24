package com.automation.dataproviders;

import com.automation.constants.FrameworkConstants;
import com.automation.utils.ConfigReader;
import com.automation.utils.ExcelDataReader;
import com.automation.utils.JsonDataReader;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class LoginDataProviders {
    @DataProvider(name = "jsonLoginData")
    public static Object[][] jsonLoginData() throws IOException {
        List<Map<String, String>> records = JsonDataReader.readRecords(
                Path.of(ConfigReader.get("jsonDataPath")));
        return toRows(records);
    }

    @DataProvider(name = "excelLoginData")
    public static Object[][] excelLoginData() throws IOException {
        List<Map<String, String>> records = ExcelDataReader.readSheet(
                Path.of(ConfigReader.get("excelDataPath")), FrameworkConstants.LOGIN_DATA_SHEET);
        return toRows(records);
    }

    private static Object[][] toRows(List<Map<String, String>> records) {
        Object[][] rows = new Object[records.size()][1];
        for (int index = 0; index < records.size(); index++) {
            rows[index][0] = records.get(index);
        }
        return rows;
    }
}
