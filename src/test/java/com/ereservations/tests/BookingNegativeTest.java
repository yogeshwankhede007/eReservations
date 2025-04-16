package com.ereservations.tests;

import com.ereservations.api.BookingApiClient;
import com.ereservations.utils.ReportUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.qameta.allure.model.Status;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Epic("Booking API Negative Tests")
public class BookingNegativeTest {
    private BookingApiClient apiClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public void setup() {
        apiClient = new BookingApiClient();
    }

    @DataProvider(name = "invalidBookingData")
    public Object[][] getInvalidBookingData() throws IOException {
        JsonNode testData = objectMapper.readTree(new File("src/test/resources/test-data/negative-test-data.json"));
        List<Object[]> testCases = new ArrayList<>();
        
        for (JsonNode data : testData.get("invalidBookingData")) {
            testCases.add(new Object[]{
                data.get("description").asText(),
                data.get("data"),
                data.get("expectedStatusCode").asInt()
            });
        }
        
        return testCases.toArray(new Object[0][]);
    }

    @DataProvider(name = "edgeCaseData")
    public Object[][] getEdgeCaseData() throws IOException {
        JsonNode testData = objectMapper.readTree(new File("src/test/resources/test-data/negative-test-data.json"));
        List<Object[]> testCases = new ArrayList<>();
        
        for (JsonNode data : testData.get("edgeCaseData")) {
            testCases.add(new Object[]{
                data.get("description").asText(),
                data.get("data"),
                data.get("expectedStatusCode").asInt()
            });
        }
        
        return testCases.toArray(new Object[0][]);
    }

    @DataProvider(name = "securityTestData")
    public Object[][] getSecurityTestData() throws IOException {
        JsonNode testData = objectMapper.readTree(new File("src/test/resources/test-data/negative-test-data.json"));
        List<Object[]> testCases = new ArrayList<>();
        
        for (JsonNode data : testData.get("securityTestData")) {
            testCases.add(new Object[]{
                data.get("description").asText(),
                data.get("data"),
                data.get("expectedStatusCode").asInt()
            });
        }
        
        return testCases.toArray(new Object[0][]);
    }

    @Test(dataProvider = "invalidBookingData", groups = {"negative", "regression"})
    @Story("Invalid Booking Data Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test creating bookings with invalid data")
    public void testInvalidBookingData(String description, JsonNode testData, int expectedStatus) {
        log.info("Testing invalid booking data: {}", description);
        ReportUtils.addStepToReport("Testing invalid booking data", Status.PASSED, description);
        
        Response response = apiClient.createBooking(testData.toString()).getLastResponse();
        
        ReportUtils.addStepToReport("Verifying response", Status.PASSED,
            String.format("Expected status: %d, Actual status: %d", expectedStatus, response.getStatusCode()));
        
        Assert.assertEquals(response.getStatusCode(), expectedStatus,
            "Expected status code " + expectedStatus + " for test case: " + description);
        
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dataProvider = "edgeCaseData", groups = {"negative", "regression"})
    @Story("Edge Case Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test booking creation with edge case data")
    public void testEdgeCases(String description, JsonNode testData, int expectedStatus) {
        log.info("Testing edge case: {}", description);
        ReportUtils.addStepToReport("Testing edge case", Status.PASSED, description);
        
        Response response = apiClient.createBooking(testData.toString()).getLastResponse();
        
        ReportUtils.addStepToReport("Verifying response", Status.PASSED,
            String.format("Expected status: %d, Actual status: %d", expectedStatus, response.getStatusCode()));
        
        Assert.assertEquals(response.getStatusCode(), expectedStatus,
            "Expected status code " + expectedStatus + " for edge case: " + description);
        
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dataProvider = "securityTestData", groups = {"security", "negative", "regression"})
    @Story("Security Validation")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test booking creation with potentially malicious data")
    public void testSecurityCases(String description, JsonNode testData, int expectedStatus) {
        log.info("Testing security case: {}", description);
        ReportUtils.addStepToReport("Testing security case", Status.PASSED, description);
        
        Response response = apiClient.createBooking(testData.toString()).getLastResponse();
        
        ReportUtils.addStepToReport("Verifying response", Status.PASSED,
            String.format("Expected status: %d, Actual status: %d", expectedStatus, response.getStatusCode()));
        
        Assert.assertEquals(response.getStatusCode(), expectedStatus,
            "Expected status code " + expectedStatus + " for security test: " + description);
        
        String responseBody = response.getBody().asString();
        ReportUtils.attachText("Response Body", responseBody);
        
        // Additional security checks
        Assert.assertFalse(responseBody.contains("<script>"),
            "Response contains potential XSS payload");
        Assert.assertFalse(responseBody.contains("DROP TABLE"),
            "Response contains potential SQL injection payload");
    }
} 