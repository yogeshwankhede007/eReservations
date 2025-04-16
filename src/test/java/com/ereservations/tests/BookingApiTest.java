package com.ereservations.tests;

import com.ereservations.api.BookingApiClient;
import com.ereservations.models.Booking;
import com.ereservations.utils.ReportUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import io.qameta.allure.model.Status;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Epic("Restful Booker API Testing")
@Feature("Booking Operations")
public class BookingApiTest {
    private BookingApiClient apiClient;
    private int createdBookingId;
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

    @Test(groups = {"smoke", "sanity", "regression"})
    @Description("Test creating a new booking")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Booking Management")
    public void testCreateBooking() throws IOException {
        String bookingData = objectMapper.readTree(new File("src/test/resources/test-data/create_booking.json")).toString();
        ReportUtils.addStepToReport("Creating new booking", Status.PASSED, "Using test data: " + bookingData);
        
        Response response = apiClient.createBooking(bookingData).getLastResponse();
        createdBookingId = response.jsonPath().getInt("bookingid");
        
        ReportUtils.addStepToReport("Create Booking", Status.PASSED,
                String.format("Created booking with ID: %d", createdBookingId));
        ReportUtils.attachText("Request Body", bookingData);
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dependsOnMethods = "testCreateBooking", groups = {"smoke", "sanity", "regression"})
    @Description("Test retrieving all booking IDs")
    @Severity(SeverityLevel.NORMAL)
    @Story("Booking Management")
    public void testGetBookingIds() {
        ReportUtils.addStepToReport("Getting all booking IDs", Status.PASSED, "");
        
        Response response = apiClient.getBookingIds().getLastResponse();
        int bookingCount = response.jsonPath().getList("$").size();
        
        ReportUtils.addStepToReport("Get Booking IDs", Status.PASSED,
                String.format("Retrieved %d booking IDs", bookingCount));
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dependsOnMethods = "testGetBookingIds", groups = {"smoke", "sanity", "regression"})
    @Description("Test updating an existing booking")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Booking Management")
    public void testUpdateBooking() throws IOException {
        String updateData = objectMapper.readTree(new File("src/test/resources/test-data/update_booking.json")).toString();
        ReportUtils.addStepToReport("Updating booking", Status.PASSED, "Using test data: " + updateData);
        
        Response response = apiClient.updateBooking(createdBookingId, updateData).getLastResponse();
        
        ReportUtils.addStepToReport("Update Booking", Status.PASSED,
                String.format("Updated booking for ID: %d", createdBookingId));
        ReportUtils.attachText("Request Body", updateData);
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dependsOnMethods = "testUpdateBooking", groups = {"smoke", "sanity", "regression"})
    @Description("Test deleting a booking")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Booking Management")
    public void testDeleteBooking() {
        ReportUtils.addStepToReport("Deleting booking", Status.PASSED, 
            String.format("Deleting booking with ID: %d", createdBookingId));
        
        Response response = apiClient.deleteBooking(createdBookingId).getLastResponse();
        
        ReportUtils.addStepToReport("Delete Booking", Status.PASSED,
                String.format("Deleted booking with ID: %d", createdBookingId));
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dataProvider = "invalidBookingData", groups = {"negative", "regression"})
    @Description("Verify API behavior with invalid booking data")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validate API response for invalid booking data")
    public void testInvalidBookingData(String description, JsonNode testData, int expectedStatusCode) {
        ReportUtils.addStepToReport("Testing invalid booking data: " + description, Status.PASSED, "");
        Response response = apiClient.createBooking(testData.toString()).getLastResponse();
        
        ReportUtils.addStepToReport("Verifying response status code", Status.PASSED, 
            "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
        
        if (response.getStatusCode() == expectedStatusCode) {
            ReportUtils.addStepToReport("Invalid booking test passed", Status.PASSED, 
                "API correctly rejected invalid data with status " + expectedStatusCode);
        } else {
            ReportUtils.addStepToReport("Invalid booking test failed", Status.FAILED, 
                "API returned unexpected status code: " + response.getStatusCode());
            throw new AssertionError("Expected status code " + expectedStatusCode + 
                " but got " + response.getStatusCode());
        }
    }

    @Test(dataProvider = "edgeCaseData", groups = {"negative", "regression"})
    @Description("Verify API behavior with edge case scenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Validate API response for edge cases")
    public void testEdgeCases(String description, JsonNode testData, int expectedStatusCode) {
        ReportUtils.addStepToReport("Testing edge case: " + description, Status.PASSED, "");
        Response response = apiClient.createBooking(testData.toString()).getLastResponse();
        
        ReportUtils.addStepToReport("Verifying response status code", Status.PASSED, 
            "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
        
        if (response.getStatusCode() == expectedStatusCode) {
            ReportUtils.addStepToReport("Edge case test passed", Status.PASSED, 
                "API correctly handled edge case with status " + expectedStatusCode);
        } else {
            ReportUtils.addStepToReport("Edge case test failed", Status.FAILED, 
                "API returned unexpected status code: " + response.getStatusCode());
            throw new AssertionError("Expected status code " + expectedStatusCode + 
                " but got " + response.getStatusCode());
        }
    }

    @Test(dataProvider = "securityTestData", groups = {"security", "negative", "sanity", "regression"})
    @Description("Verify API security measures")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Validate API security against common attacks")
    public void testSecurityScenarios(String description, JsonNode testData, int expectedStatusCode) {
        ReportUtils.addStepToReport("Testing security scenario: " + description, Status.PASSED, "");
        Response response = apiClient.createBooking(testData.toString()).getLastResponse();
        
        ReportUtils.addStepToReport("Verifying response status code", Status.PASSED, 
            "Expected: " + expectedStatusCode + ", Actual: " + response.getStatusCode());
        
        if (response.getStatusCode() == expectedStatusCode) {
            ReportUtils.addStepToReport("Security test passed", Status.PASSED, 
                "API correctly rejected security threat with status " + expectedStatusCode);
        } else {
            ReportUtils.addStepToReport("Security test failed", Status.FAILED, 
                "API returned unexpected status code: " + response.getStatusCode());
            throw new AssertionError("Expected status code " + expectedStatusCode + 
                " but got " + response.getStatusCode());
        }
        
        // Additional security checks
        ReportUtils.addStepToReport("Checking response for potential security vulnerabilities", 
            Status.PASSED, "");
        String responseBody = response.getBody().asString();
        if (responseBody.contains("<script>") || responseBody.contains("SQL")) {
            ReportUtils.addStepToReport("Security vulnerability detected", Status.FAILED, 
                "Response contains potentially dangerous content");
            throw new AssertionError("Security vulnerability detected in response");
        }
    }
} 