package com.ereservations.tests;

import com.ereservations.api.BookingApiClient;
import com.ereservations.utils.TestDataProvider;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

@Slf4j
public class BookingApiTest extends BaseBookingTest {

    @Test(description = "Create a new booking and store its ID", groups = {"sanity", "smoke"})
    public void testCreateBooking() {
        log.info("Creating a new booking");
        JsonNode bookingData = TestDataProvider.getValidBookingData();
        Response response = apiClient.createBooking(bookingData);
        
        validateResponse(response, 200, "Create Booking");
        validateSecurity(response, "Create Booking");
        
        int bookingId = response.jsonPath().getInt("bookingid");
        Assert.assertTrue(bookingId > 0, "Booking ID should be positive");
        
        // Store booking data and ID for subsequent tests
        storeBookingId(bookingId);
        storeBookingData(bookingData);
        log.info("Created booking with ID: {}", bookingId);
    }

    @Test(dependsOnMethods = "testCreateBooking", description = "Get all booking IDs", groups = {"sanity", "smoke"})
    public void testGetBookingIds() {
        log.info("Getting all booking IDs");
        Response response = apiClient.getBookingIds();
        
        validateResponse(response, 200, "Get Booking IDs");
        validateSecurity(response, "Get Booking IDs");
        
        int storedBookingId = getStoredBookingId();
        Assert.assertTrue(response.jsonPath().getList("").contains(storedBookingId),
            "Created booking ID should be in the list");
        log.info("Verified booking ID {} exists in the list", storedBookingId);
    }

    @Test(dependsOnMethods = "testGetBookingIds", description = "Get specific booking details", groups = {"sanity", "smoke"})
    public void testGetBooking() {
        int bookingId = getStoredBookingId();
        log.info("Getting booking details for ID: {}", bookingId);
        
        Response response = apiClient.getBooking(bookingId);
        validateResponse(response, 200, "Get Booking");
        validateSecurity(response, "Get Booking");
        
        JsonNode storedData = getStoredBookingData();
        Assert.assertEquals(response.jsonPath().getString("firstname"), 
            storedData.get("firstname").asText(), "Firstname should match");
        Assert.assertEquals(response.jsonPath().getString("lastname"), 
            storedData.get("lastname").asText(), "Lastname should match");
        log.info("Verified booking details match for ID: {}", bookingId);
    }

    @Test(dependsOnMethods = "testGetBooking", description = "Update booking details", groups = {"sanity", "smoke"})
    public void testUpdateBooking() {
        int bookingId = getStoredBookingId();
        log.info("Updating booking with ID: {}", bookingId);
        
        JsonNode updatedData = TestDataProvider.getValidBookingData();
        Response response = apiClient.updateBooking(bookingId, updatedData);
        
        validateResponse(response, 200, "Update Booking");
        validateSecurity(response, "Update Booking");
        
        // Store updated data
        storeBookingData(updatedData);
        log.info("Updated booking with ID: {}", bookingId);
    }

    @Test(dependsOnMethods = "testUpdateBooking", description = "Delete booking", groups = {"sanity", "smoke"})
    public void testDeleteBooking() {
        int bookingId = getStoredBookingId();
        log.info("Deleting booking with ID: {}", bookingId);
        
        Response response = apiClient.deleteBooking(bookingId);
        validateResponse(response, 201, "Delete Booking");
        validateSecurity(response, "Delete Booking");
        
        // Verify booking is deleted
        Response getResponse = apiClient.getBooking(bookingId);
        validateResponse(getResponse, 404, "Get Deleted Booking");
        log.info("Successfully deleted booking with ID: {}", bookingId);
    }
} 