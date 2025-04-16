package com.ereservations.utils;

import com.ereservations.models.Booking;
import com.ereservations.models.Booking.BookingDates;
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
    private static final String TEST_DATA_DIR = "src/test/resources/testdata";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @DataProvider(name = "bookingData")
    public static Object[][] getBookingData() {
        List<String> testDataFiles = new ArrayList<>();
        testDataFiles.add("valid_booking.json");
        testDataFiles.add("update_booking.json");
        
        Object[][] data = new Object[testDataFiles.size()][1];
        for (int i = 0; i < testDataFiles.size(); i++) {
            data[i][0] = testDataFiles.get(i);
        }
        return data;
    }

    public static Booking getBookingData(String dataFileName) {
        try {
            File file = Paths.get(TEST_DATA_DIR, dataFileName).toFile();
            return objectMapper.readValue(file, Booking.class);
        } catch (IOException e) {
            log.error("Error loading test data from file: {}", dataFileName, e);
            throw new RuntimeException("Failed to load test data", e);
        }
    }

    public static Booking getBookingData(String dataFileName, String scenario) {
        try {
            File file = Paths.get(TEST_DATA_DIR, scenario, dataFileName).toFile();
            return objectMapper.readValue(file, Booking.class);
        } catch (IOException e) {
            log.error("Error loading test data from file: {} in scenario: {}", dataFileName, scenario, e);
            throw new RuntimeException("Failed to load test data", e);
        }
    }
} 