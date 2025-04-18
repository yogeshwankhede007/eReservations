package com.ereservations.tests;

import com.ereservations.api.BaseApiClient;
import com.ereservations.api.BookingApiClient;
import com.ereservations.api.HealthCheckApiClient;
import com.ereservations.api.PingApiClient;
import com.ereservations.api.SystemApiClient;
import com.ereservations.models.Booking;
import com.ereservations.models.BookingDates;
import com.ereservations.models.BookingResponse;
import com.ereservations.models.HealthCheckResponse;
import com.ereservations.models.PingResponse;
import com.ereservations.models.SystemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class BaseBookingTest {
    protected static final Logger log = LogManager.getLogger(BaseBookingTest.class);
    protected static final String CONFIG_FILE = "config.properties";
    protected static final String TEST_DATA_DIR = "src/test/resources/test-data";
    protected static final String TEST_SUITES_DIR = "src/test/resources/test-suites";
    
    protected static final Map<String, Object> testContext = new ConcurrentHashMap<>();
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    
    protected static Properties config;
    protected static BaseApiClient baseApiClient;
    protected static BookingApiClient bookingApiClient;
    protected static HealthCheckApiClient healthCheckApiClient;
    protected static PingApiClient pingApiClient;
    protected static SystemApiClient systemApiClient;

    @BeforeClass
    public void setup() throws IOException {
        // Load configuration
        config = new Properties();
        config.load(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE));
        
        // Initialize API clients
        baseApiClient = new BaseApiClient(config.getProperty("api.base.url"));
        bookingApiClient = new BookingApiClient(config.getProperty("api.base.url"));
        healthCheckApiClient = new HealthCheckApiClient(config.getProperty("api.base.url"));
        pingApiClient = new PingApiClient(config.getProperty("api.base.url"));
        systemApiClient = new SystemApiClient(config.getProperty("api.base.url"));
        
        // Initialize test context
        testContext.clear();
    }

    protected void validateResponse(Response response, String description, int expectedStatus) {
        log.info("Validating response for: {}", description);
        log.info("Expected status: {}, Actual status: {}", expectedStatus, response.getStatusCode());
        
        if (response.getStatusCode() != expectedStatus) {
            log.error("Response validation failed for: {}", description);
            log.error("Response body: {}", response.getBody().asString());
        }
        
        assert response.getStatusCode() == expectedStatus : 
            String.format("Expected status %d but got %d for %s", 
                expectedStatus, response.getStatusCode(), description);
    }

    protected void validateSecurity(Response response, String description) {
        log.info("Validating security for: {}", description);
        
        // Check for common security headers
        assert response.getHeader("X-Content-Type-Options") != null : 
            "Missing X-Content-Type-Options header";
        assert response.getHeader("X-Frame-Options") != null : 
            "Missing X-Frame-Options header";
        assert response.getHeader("X-XSS-Protection") != null : 
            "Missing X-XSS-Protection header";
        
        log.info("Security validation passed for: {}", description);
    }

    protected void validateInput(Booking booking, String description) {
        log.info("Validating input for: {}", description);
        
        // Validate required fields
        assert booking.getFirstname() != null && !booking.getFirstname().isEmpty() : 
            "Firstname is required";
        assert booking.getLastname() != null && !booking.getLastname().isEmpty() : 
            "Lastname is required";
        assert booking.getTotalprice() >= 0 : 
            "Totalprice must be non-negative";
        assert booking.getDepositpaid() != null : 
            "Depositpaid is required";
        assert booking.getBookingdates() != null : 
            "Bookingdates is required";
        assert booking.getBookingdates().getCheckin() != null : 
            "Checkin date is required";
        assert booking.getBookingdates().getCheckout() != null : 
            "Checkout date is required";
        
        log.info("Input validation passed for: {}", description);
    }

    // Test data providers
    @DataProvider(name = "validBookingData")
    public Object[][] provideValidBookingData() {
        return new Object[][] {
            {createValidBooking("John", "Doe")},
            {createValidBooking("Jane", "Smith")},
            {createValidBooking("Test", "User")}
        };
    }

    @DataProvider(name = "invalidBookingData")
    public Object[][] provideInvalidBookingData() {
        return new Object[][] {
            {createInvalidBooking(null, "Doe")},
            {createInvalidBooking("John", null)},
            {createInvalidBooking("", "Doe")},
            {createInvalidBooking("John", "")}
        };
    }

    @DataProvider(name = "securityTestData")
    public Object[][] provideSecurityTestData() {
        return new Object[][] {
            {createSecurityTestBooking("<script>alert('xss')</script>", "Doe")},
            {createSecurityTestBooking("John", "'; DROP TABLE bookings; --")},
            {createSecurityTestBooking("John", "OR '1'='1")}
        };
    }

    // Helper methods for creating test data
    protected Booking createValidBooking(String firstname, String lastname) {
        BookingDates dates = new BookingDates();
        dates.setCheckin("2024-01-01");
        dates.setCheckout("2024-01-05");
        
        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        booking.setTotalprice(100);
        booking.setDepositpaid(true);
        booking.setBookingdates(dates);
        booking.setAdditionalneeds("Breakfast");
        
        return booking;
    }

    protected Booking createInvalidBooking(String firstname, String lastname) {
        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        // Other fields are intentionally left null/invalid
        return booking;
    }

    protected Booking createSecurityTestBooking(String firstname, String lastname) {
        BookingDates dates = new BookingDates();
        dates.setCheckin("2024-01-01");
        dates.setCheckout("2024-01-05");
        
        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        booking.setTotalprice(100);
        booking.setDepositpaid(true);
        booking.setBookingdates(dates);
        booking.setAdditionalneeds("Security Test");
        
        return booking;
    }

    // Context management methods
    protected void storeInContext(String key, Object value) {
        testContext.put(key, value);
        log.debug("Stored in context - Key: {}, Value: {}", key, value);
    }

    protected Object getFromContext(String key) {
        Object value = testContext.get(key);
        log.debug("Retrieved from context - Key: {}, Value: {}", key, value);
        return value;
    }

    protected void clearContext() {
        testContext.clear();
        log.debug("Cleared test context");
    }
}