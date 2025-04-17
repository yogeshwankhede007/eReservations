package com.ereservations.utils;

import com.ereservations.models.Booking;
import com.ereservations.models.Booking.BookingDates;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestDataProvider {
    private static final String TEST_DATA_DIR = "src/test/resources/test-data/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TestDataProvider() {
        // Private constructor to prevent instantiation
    }

    public static JsonNode getValidBookingData() {
        try {
            return objectMapper.readTree(new File(TEST_DATA_DIR + "create_booking.json"));
        } catch (IOException e) {
            log.error("Error loading valid booking data", e);
            throw new RuntimeException("Failed to load valid booking data", e);
        }
    }

    public static Object[][] getInvalidBookingData() {
        try {
            JsonNode testData = objectMapper.readTree(new File(TEST_DATA_DIR + "negative-test-data.json"));
            return getTestCases(testData, "invalidBookingData");
        } catch (IOException e) {
            log.error("Error loading invalid booking data", e);
            throw new RuntimeException("Failed to load invalid booking data", e);
        }
    }

    public static Object[][] getEdgeCaseData() {
        try {
            JsonNode testData = objectMapper.readTree(new File(TEST_DATA_DIR + "negative-test-data.json"));
            return getTestCases(testData, "edgeCaseData");
        } catch (IOException e) {
            log.error("Error loading edge case data", e);
            throw new RuntimeException("Failed to load edge case data", e);
        }
    }

    public static Object[][] getSecurityTestData() {
        try {
            JsonNode testData = objectMapper.readTree(new File(TEST_DATA_DIR + "negative-test-data.json"));
            return getTestCases(testData, "securityTestData");
        } catch (IOException e) {
            log.error("Error loading security test data", e);
            throw new RuntimeException("Failed to load security test data", e);
        }
    }

    private static Object[][] getTestCases(JsonNode testData, String category) {
        List<Object[]> testCases = new ArrayList<>();
        JsonNode categoryData = testData.get(category);
        
        if (categoryData != null && categoryData.isArray()) {
            for (JsonNode testCase : categoryData) {
                testCases.add(new Object[]{
                    testCase.get("description").asText(),
                    testCase.get("data"),
                    testCase.get("expectedStatusCode").asInt()
                });
            }
        }
        
        return testCases.toArray(new Object[0][]);
    }
} 