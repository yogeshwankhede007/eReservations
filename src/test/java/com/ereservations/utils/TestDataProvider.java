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
    private static final String TEST_DATA_DIR = "src/test/resources/test-data";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Booking getBookingData(String dataFileName) {
        try {
            File file = Paths.get(TEST_DATA_DIR, dataFileName).toFile();
            log.info("Loading test data from file: {}", dataFileName);
            return objectMapper.readValue(file, Booking.class);
        } catch (IOException e) {
            log.error("Error loading test data from file: {}", dataFileName, e);
            throw new RuntimeException("Failed to load test data", e);
        }
    }

    @DataProvider(name = "bookingData")
    public static Object[][] getBookingTestData() {
        return new Object[][] {
            {"create_booking.json"}
        };
    }

    public static Booking getBookingData(String dataFileName, String scenario) {
        try {
            File file = Paths.get(TEST_DATA_DIR, scenario, dataFileName).toFile();
            log.info("Loading test data from file: {} in scenario: {}", dataFileName, scenario);
            return objectMapper.readValue(file, Booking.class);
        } catch (IOException e) {
            log.error("Error loading test data from file: {} in scenario: {}", dataFileName, scenario, e);
            throw new RuntimeException("Failed to load test data", e);
        }
    }
} 