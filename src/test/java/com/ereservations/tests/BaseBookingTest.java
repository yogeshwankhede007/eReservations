package com.ereservations.tests;

import com.ereservations.api.BookingApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class BaseBookingTest {
    protected BookingApiClient apiClient;
    protected static final String XSS_PATTERN = "<script>.*</script>";
    protected static final String SQL_INJECTION_PATTERN = ".*([';]|(--)|(DROP)|(DELETE)|(UPDATE)|(INSERT)).*";
    
    // Store test context data
    protected Map<String, Object> testContext;
    protected static final String BOOKING_ID_KEY = "bookingId";
    protected static final String BOOKING_DATA_KEY = "bookingData";
    protected static final String AUTH_TOKEN_KEY = "authToken";

    @BeforeClass
    public void setup() {
        apiClient = new BookingApiClient();
        testContext = new HashMap<>();
    }

    @BeforeMethod
    public void setupTest() {
        log.info("Setting up test context");
        testContext.clear();
    }

    protected void validateResponse(Response response, int expectedStatusCode, String description) {
        log.info("Validating response for: {}", description);
        log.info("Expected status: {}, Actual status: {}", expectedStatusCode, response.getStatusCode());
        
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
            String.format("Expected status code %d for test case: %s", expectedStatusCode, description));
        
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            log.info("Response Body: {}", response.getBody().asString());
        } else {
            log.warn("Error Response: {}", response.getBody().asString());
        }
    }

    protected void validateSecurity(Response response, String description) {
        log.info("Validating security for: {}", description);
        String responseBody = response.getBody().asString();
        
        // Check for XSS
        Assert.assertFalse(responseBody.matches(XSS_PATTERN),
            "Response contains potential XSS payload");
        
        // Check for SQL injection
        Assert.assertFalse(responseBody.matches(SQL_INJECTION_PATTERN),
            "Response contains potential SQL injection payload");
        
        // Check response headers
        Assert.assertFalse(response.getHeader("Server").contains("version"),
            "Server version information should not be exposed");
        
        log.info("Security validation passed for: {}", description);
    }

    protected void validateInput(JsonNode data, String description) {
        log.info("Validating input for: {}", description);
        
        // Validate required fields
        Assert.assertTrue(data.has("firstname"), "Firstname is required");
        Assert.assertTrue(data.has("lastname"), "Lastname is required");
        Assert.assertTrue(data.has("totalprice"), "Totalprice is required");
        Assert.assertTrue(data.has("depositpaid"), "Depositpaid is required");
        Assert.assertTrue(data.has("bookingdates"), "Bookingdates is required");
        
        // Validate data types and formats
        JsonNode bookingDates = data.get("bookingdates");
        Assert.assertTrue(bookingDates.has("checkin"), "Checkin date is required");
        Assert.assertTrue(bookingDates.has("checkout"), "Checkout date is required");
        
        // Validate date format
        String checkin = bookingDates.get("checkin").asText();
        String checkout = bookingDates.get("checkout").asText();
        Assert.assertTrue(checkin.matches("\\d{4}-\\d{2}-\\d{2}"), "Invalid checkin date format");
        Assert.assertTrue(checkout.matches("\\d{4}-\\d{2}-\\d{2}"), "Invalid checkout date format");
        
        // Validate price is positive
        Assert.assertTrue(data.get("totalprice").asInt() > 0, "Total price must be positive");
        
        // Validate deposit is boolean
        Assert.assertTrue(data.get("depositpaid").isBoolean(), "Deposit paid must be boolean");
        
        log.info("Input validation passed for: {}", description);
    }

    protected void storeBookingId(int bookingId) {
        testContext.put(BOOKING_ID_KEY, bookingId);
        log.info("Stored booking ID: {}", bookingId);
    }

    protected int getStoredBookingId() {
        Integer bookingId = (Integer) testContext.get(BOOKING_ID_KEY);
        Assert.assertNotNull(bookingId, "No booking ID found in test context");
        return bookingId;
    }

    protected void storeBookingData(JsonNode bookingData) {
        testContext.put(BOOKING_DATA_KEY, bookingData);
        log.info("Stored booking data");
    }

    protected JsonNode getStoredBookingData() {
        JsonNode bookingData = (JsonNode) testContext.get(BOOKING_DATA_KEY);
        Assert.assertNotNull(bookingData, "No booking data found in test context");
        return bookingData;
    }

    protected void storeAuthToken(String token) {
        testContext.put(AUTH_TOKEN_KEY, token);
        log.info("Stored auth token");
    }

    protected String getAuthToken() {
        String token = (String) testContext.get(AUTH_TOKEN_KEY);
        Assert.assertNotNull(token, "No auth token found in test context");
        return token;
    }
} 