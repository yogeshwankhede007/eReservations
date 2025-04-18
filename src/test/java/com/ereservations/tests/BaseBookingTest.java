package com.ereservations.tests;

import com.ereservations.api.BookingApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;
import lombok.extern.slf4j.Slf4j;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class BaseBookingTest {
    protected BookingApiClient apiClient;
    protected SoftAssert softAssert;
    protected ExtentTest extentTest;
    protected static final String SCREENSHOT_DIR = "./screenshots";
    
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
            testContext = new HashMap<>();
            
            // Create screenshots directory if it doesn't exist
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
                log.info("Created screenshots directory at: {}", SCREENSHOT_DIR);
            }
            
            log.info("Successfully initialized test environment");
        } catch (Exception e) {
            log.error("Error during setup: {}", e.getMessage());
            throw new RuntimeException("Failed to setup test environment", e);
        }
    }

    protected void captureScreenshot(String testName, String stepName) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = String.format("%s_%s_%s.png", testName, stepName, timestamp);
            File screenshotFile = new File(SCREENSHOT_DIR, fileName);
            
            // Add screenshot to ExtentReports
            if (extentTest != null) {
                extentTest.addScreenCaptureFromPath(screenshotFile.getAbsolutePath());
            }
            log.info("Captured screenshot: {}", screenshotFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
        }
    }

    protected void validateResponse(Response response, int expectedStatusCode, String description) {
        log.info("Validating response for: {}", description);
        log.info("Expected status: {}, Actual status: {}", expectedStatusCode, response.getStatusCode());
        
        boolean isSuccess = response.getStatusCode() == expectedStatusCode;
        Status status = isSuccess ? Status.PASS : Status.FAIL;
        String message = String.format("Expected status code %d for test case: %s", expectedStatusCode, description);
        
        if (extentTest != null) {
            extentTest.log(status, MarkupHelper.createLabel(message, isSuccess ? ExtentColor.GREEN : ExtentColor.RED));
            extentTest.log(Status.INFO, "Response Time: " + response.getTime() + "ms");
            extentTest.log(Status.INFO, "Response Body: " + response.getBody().asString());
        }
        
        // Capture screenshot for response validation
        captureScreenshot(description, "response_validation");
        
        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode, message);
        
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            log.debug("Response Body: {}", response.getBody().asString());
        } else {
            log.warn("Error Response: {}", response.getBody().asString());
            if (extentTest != null) {
                extentTest.warning("Error Response: " + response.getBody().asString());
            }
        }
    }

    protected void validateSecurity(Response response, String description) {
        log.info("Validating security for: {}", description);
        String responseBody = response.getBody().asString();
        
        boolean securityPassed = true;
        StringBuilder securityIssues = new StringBuilder();
        
        // Check for XSS
        if (responseBody.contains("<script>")) {
            securityPassed = false;
            securityIssues.append("Potential XSS vulnerability detected\n");
        }
        
        // Check for SQL injection
        if (responseBody.contains("SELECT")) {
            securityPassed = false;
            securityIssues.append("Potential SQL injection vulnerability detected\n");
        }
        
        // Check response headers
        if (response.getHeader("Server") != null && response.getHeader("Server").contains("version")) {
            securityPassed = false;
            securityIssues.append("Server version information should not be exposed\n");
        }
        
        if (extentTest != null) {
            Status status = securityPassed ? Status.PASS : Status.FAIL;
            extentTest.log(status, MarkupHelper.createLabel("Security Validation: " + description, 
                securityPassed ? ExtentColor.GREEN : ExtentColor.RED));
            
            if (!securityPassed) {
                extentTest.fail(securityIssues.toString());
            }
        }
        
        log.info("Security validation passed for: {}", description);
    }

    protected void validateInput(JsonNode data, String description) {
        log.info("Validating input for: {}", description);
        
        boolean validationPassed = true;
        StringBuilder validationIssues = new StringBuilder();
        
        // Validate required fields
        if (!data.has("firstname")) {
            validationPassed = false;
            validationIssues.append("Firstname is required\n");
        }
        if (!data.has("lastname")) {
            validationPassed = false;
            validationIssues.append("Lastname is required\n");
        }
        if (!data.has("totalprice")) {
            validationPassed = false;
            validationIssues.append("Totalprice is required\n");
        }
        if (!data.has("depositpaid")) {
            validationPassed = false;
            validationIssues.append("Depositpaid is required\n");
        }
        if (!data.has("bookingdates")) {
            validationPassed = false;
            validationIssues.append("Bookingdates is required\n");
        }
        
        if (data.has("bookingdates")) {
            JsonNode bookingDates = data.get("bookingdates");
            if (!bookingDates.has("checkin")) {
                validationPassed = false;
                validationIssues.append("Checkin date is required\n");
            }
            if (!bookingDates.has("checkout")) {
                validationPassed = false;
                validationIssues.append("Checkout date is required\n");
            }
        }
        
        if (extentTest != null) {
            Status status = validationPassed ? Status.PASS : Status.FAIL;
            extentTest.log(status, MarkupHelper.createLabel("Input Validation: " + description, 
                validationPassed ? ExtentColor.GREEN : ExtentColor.RED));
            
            if (!validationPassed) {
                extentTest.fail(validationIssues.toString());
            }
            
            extentTest.log(Status.INFO, "Request Data: " + data.toString());
        }
        
        log.info("Input validation passed for: {}", description);
    }

    protected void storeBookingId(int bookingId) {
        testContext.put(BOOKING_ID_KEY, bookingId);
        log.info("Stored booking ID: {}", bookingId);
    }

    protected int getStoredBookingId() {
        Integer bookingId = (Integer) testContext.get(BOOKING_ID_KEY);
        softAssert.assertNotNull(bookingId, "No booking ID found in test context");
        return bookingId != null ? bookingId : -1;
    }

    protected void storeBookingData(JsonNode bookingData) {
        testContext.put(BOOKING_DATA_KEY, bookingData);
        log.info("Stored booking data");
    }

    protected JsonNode getStoredBookingData() {
        JsonNode bookingData = (JsonNode) testContext.get(BOOKING_DATA_KEY);
        softAssert.assertNotNull(bookingData, "No booking data found in test context");
        return bookingData;
    }

    protected void storeAuthToken(String token) {
        testContext.put(AUTH_TOKEN_KEY, token);
        log.info("Stored auth token");
    }

    protected String getAuthToken() {
        String token = (String) testContext.get(AUTH_TOKEN_KEY);
        softAssert.assertNotNull(token, "No auth token found in test context");
        return token != null ? token : "";
    }
}