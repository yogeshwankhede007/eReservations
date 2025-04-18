package com.ereservations.tests;

import com.ereservations.api.BookingApiClient;
import com.ereservations.utils.TestDataProvider;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Slf4j
public class BookingNegativeTest extends BaseBookingTest {

    @DataProvider(name = "invalidBookingData")
    public Object[][] getInvalidBookingData() {
        return TestDataProvider.getInvalidBookingData();
    }

    @DataProvider(name = "edgeCaseData")
    public Object[][] getEdgeCaseData() {
        return TestDataProvider.getEdgeCaseData();
    }

    @DataProvider(name = "securityTestData")
    public Object[][] getSecurityTestData() {
        return TestDataProvider.getSecurityTestData();
    }

    @Test(dataProvider = "invalidBookingData", description = "Test invalid booking data", groups = {"negative"})
    public void testInvalidBookingData(String testCase, JsonNode data, int expectedStatusCode) {
        log.info("Testing invalid booking data: {}", testCase);
        validateInput(data, testCase);
        
        Response response = apiClient.createBooking(data).getLastResponse();
        validateResponse(response, expectedStatusCode, testCase);
        validateSecurity(response, testCase);
        
        // Store error response for analysis if needed
        if (expectedStatusCode >= 400) {
            log.warn("Error response for {}: {}", testCase, response.getBody().asString());
        }
    }

    @Test(dataProvider = "edgeCaseData", description = "Test edge cases", groups = {"negative"})
    public void testEdgeCases(String testCase, JsonNode data, int expectedStatusCode) {
        log.info("Testing edge case: {}", testCase);
        validateInput(data, testCase);
        
        Response response = apiClient.createBooking(data).getLastResponse();
        validateResponse(response, expectedStatusCode, testCase);
        validateSecurity(response, testCase);
        
        // Store error response for analysis if needed
        if (expectedStatusCode >= 400) {
            log.warn("Error response for {}: {}", testCase, response.getBody().asString());
        }
    }

    @Test(dataProvider = "securityTestData", description = "Test security cases", groups = {"security"})
    public void testSecurityCases(String testCase, JsonNode data, int expectedStatusCode) {
        log.info("Testing security case: {}", testCase);
        validateInput(data, testCase);
        
        Response response = apiClient.createBooking(data).getLastResponse();
        validateResponse(response, expectedStatusCode, testCase);
        validateSecurity(response, testCase);
        
        // Additional security validations
        String responseBody = response.getBody().asString();
        Assert.assertFalse(responseBody.contains("error"), 
            "Error messages should not expose internal details");
        Assert.assertFalse(responseBody.contains("stack trace"), 
            "Stack traces should not be exposed");
        
        // Store error response for analysis if needed
        if (expectedStatusCode >= 400) {
            log.warn("Error response for {}: {}", testCase, response.getBody().asString());
        }
    }
} 