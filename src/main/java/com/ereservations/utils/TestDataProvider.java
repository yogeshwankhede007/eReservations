package com.ereservations.utils;

import com.ereservations.models.Booking;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
public class TestDataProvider {
    private static final String TEST_DATA_DIR = "src/test/resources/testdata";
    private static final ObjectMapper objectMapper = new ObjectMapper();

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