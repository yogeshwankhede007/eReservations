package com.ereservations.tests;

import com.ereservations.api.BookingApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseBookingTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseBookingTest.class);
    protected BookingApiClient apiClient;
    protected SoftAssert softAssert;
    
    // Store test context data
    protected Map<String, Object> testContext;
    protected static final String BOOKING_ID_KEY = "bookingId";
    protected static final String BOOKING_DATA_KEY = "bookingData";
    protected static final String AUTH_TOKEN_KEY = "authToken";

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        try {
            softAssert = new SoftAssert();
            apiClient = new BookingApiClient();
            if (apiClient == null) {
                throw new RuntimeException("Failed to initialize BookingApiClient");
            }
            logger.info("Successfully initialized BookingApiClient");
            testContext = new HashMap<>();
        } catch (Exception e) {
            logger.error("Error during setup: {}", e.getMessage());
            throw new RuntimeException("Failed to setup test environment", e);
        }
    }

    protected void validateResponse(Response response, int expectedStatusCode, String description) {
        logger.info("Validating response for: {}", description);
        logger.info("Expected status: {}, Actual status: {}", expectedStatusCode, response.getStatusCode());
        
        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode,
            String.format("Expected status code %d for test case: %s", expectedStatusCode, description));
        
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            logger.info("Response Body: {}", response.getBody().asString());
        } else {
            logger.warn("Error Response: {}", response.getBody().asString());
        }
    }

    protected void validateSecurity(Response response, String description) {
        logger.info("Validating security for: {}", description);
        String responseBody = response.getBody().asString();
        
        // Check for XSS
        softAssert.assertFalse(responseBody.contains("<script>"), 
            "Potential XSS vulnerability detected");
        
        // Check for SQL injection
        softAssert.assertFalse(responseBody.contains("SELECT"), 
            "Potential SQL injection vulnerability detected");
        
        // Check response headers
        softAssert.assertFalse(response.getHeader("Server").contains("version"),
            "Server version information should not be exposed");
        
        logger.info("Security validation passed for: {}", description);
    }

    protected void validateInput(JsonNode data, String description) {
        logger.info("Validating input for: {}", description);
        
        // Validate required fields
        softAssert.assertTrue(data.has("firstname"), "Firstname is required");
        softAssert.assertTrue(data.has("lastname"), "Lastname is required");
        softAssert.assertTrue(data.has("totalprice"), "Totalprice is required");
        softAssert.assertTrue(data.has("depositpaid"), "Depositpaid is required");
        softAssert.assertTrue(data.has("bookingdates"), "Bookingdates is required");
        
        // Validate data types and formats
        JsonNode bookingDates = data.get("bookingdates");
        softAssert.assertTrue(bookingDates.has("checkin"), "Checkin date is required");
        softAssert.assertTrue(bookingDates.has("checkout"), "Checkout date is required");
        
        // Validate date format
        String checkin = bookingDates.get("checkin").asText();
        String checkout = bookingDates.get("checkout").asText();
        softAssert.assertTrue(checkin.matches("\\d{4}-\\d{2}-\\d{2}"), "Invalid checkin date format");
        softAssert.assertTrue(checkout.matches("\\d{4}-\\d{2}-\\d{2}"), "Invalid checkout date format");
        
        // Validate price is positive
        softAssert.assertTrue(data.get("totalprice").asInt() > 0, "Total price must be positive");
        
        // Validate deposit is boolean
        softAssert.assertTrue(data.get("depositpaid").isBoolean(), "Deposit paid must be boolean");
        
        logger.info("Input validation passed for: {}", description);
    }

    protected void storeBookingId(int bookingId) {
        testContext.put(BOOKING_ID_KEY, bookingId);
        logger.info("Stored booking ID: {}", bookingId);
    }

    protected int getStoredBookingId() {
        Integer bookingId = (Integer) testContext.get(BOOKING_ID_KEY);
        softAssert.assertNotNull(bookingId, "No booking ID found in test context");
        return bookingId;
    }

    protected void storeBookingData(JsonNode bookingData) {
        testContext.put(BOOKING_DATA_KEY, bookingData);
        logger.info("Stored booking data");
    }

    protected JsonNode getStoredBookingData() {
        JsonNode bookingData = (JsonNode) testContext.get(BOOKING_DATA_KEY);
        softAssert.assertNotNull(bookingData, "No booking data found in test context");
        return bookingData;
    }

    protected void storeAuthToken(String token) {
        testContext.put(AUTH_TOKEN_KEY, token);
        logger.info("Stored auth token");
    }

    protected String getAuthToken() {
        String token = (String) testContext.get(AUTH_TOKEN_KEY);
        softAssert.assertNotNull(token, "No auth token found in test context");
        return token;
    }
} 