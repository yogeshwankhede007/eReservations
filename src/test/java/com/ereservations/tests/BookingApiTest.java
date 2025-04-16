package com.ereservations.tests;

import com.ereservations.api.BookingApiClient;
import com.ereservations.utils.ReportUtils;
import com.ereservations.utils.TestDataProvider;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BookingApiTest {
    private BookingApiClient bookingApiClient;
    private int createdBookingId;

    @BeforeClass
    public void setup() {
        bookingApiClient = new BookingApiClient();
    }

    @Test(dataProvider = "bookingData", dataProviderClass = TestDataProvider.class)
    @Description("Test creating a new booking")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Booking Management")
    public void testCreateBooking(String testDataFile) {
        executeCreateBooking(testDataFile);
    }

    @Step("Create booking with data from {testDataFile}")
    private void executeCreateBooking(String testDataFile) {
        Response response = bookingApiClient.createBooking(testDataFile).getLastResponse();
        createdBookingId = response.jsonPath().getInt("bookingid");
        
        ReportUtils.addStepToReport("Create Booking", io.qameta.allure.model.Status.PASSED,
                String.format("Created booking with ID: %d", createdBookingId));
        ReportUtils.attachText("Request Body", TestDataProvider.getBookingData(testDataFile).toString());
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dependsOnMethods = "testCreateBooking")
    @Description("Test retrieving all booking IDs")
    @Severity(SeverityLevel.NORMAL)
    @Story("Booking Management")
    public void testGetBookingIds() {
        executeGetBookingIds();
    }

    @Step("Get all booking IDs")
    private void executeGetBookingIds() {
        Response response = bookingApiClient.getBookingIds().getLastResponse();
        int bookingCount = response.jsonPath().getList("$").size();
        
        ReportUtils.addStepToReport("Get Booking IDs", io.qameta.allure.model.Status.PASSED,
                String.format("Retrieved %d booking IDs", bookingCount));
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dependsOnMethods = "testGetBookingIds")
    @Description("Test updating an existing booking")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Booking Management")
    public void testUpdateBooking() {
        executeUpdateBooking();
    }

    @Step("Update booking with ID {createdBookingId}")
    private void executeUpdateBooking() {
        Response response = bookingApiClient.updateBooking(createdBookingId, "update_booking.json").getLastResponse();
        
        ReportUtils.addStepToReport("Update Booking", io.qameta.allure.model.Status.PASSED,
                String.format("Updated booking for ID: %d", createdBookingId));
        ReportUtils.attachText("Request Body", TestDataProvider.getBookingData("update_booking.json").toString());
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test(dependsOnMethods = "testUpdateBooking")
    @Description("Test deleting a booking")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Booking Management")
    public void testDeleteBooking() {
        executeDeleteBooking();
    }

    @Step("Delete booking with ID {createdBookingId}")
    private void executeDeleteBooking() {
        Response response = bookingApiClient.deleteBooking(createdBookingId).getLastResponse();
        
        ReportUtils.addStepToReport("Delete Booking", io.qameta.allure.model.Status.PASSED,
                String.format("Deleted booking with ID: %d", createdBookingId));
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }
} 