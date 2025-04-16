package com.ereservations.api;

import com.ereservations.models.Booking;
import com.ereservations.utils.TestDataProvider;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;

@Slf4j
public class BookingApiClient extends BaseApiClient {
    private Response lastResponse;
    
    public BookingApiClient createBooking(String testDataFile) {
        Booking booking = TestDataProvider.getBookingData(testDataFile);
        log.info("Creating new booking for: {} {}", booking.getFirstName(), booking.getLastName());
        
        lastResponse = given()
                .spec(getRequestSpecification())
                .body(booking)
                .when()
                .post("/booking");
        
        validateResponse(lastResponse, 200);
        validateResponseBody(lastResponse, "booking.firstname", booking.getFirstName());
        validateResponseBody(lastResponse, "booking.lastname", booking.getLastName());
        validateResponseBody(lastResponse, "booking.totalprice", booking.getTotalPrice());
        validateResponseBody(lastResponse, "booking.depositpaid", booking.getDepositPaid());
        validateResponseBody(lastResponse, "booking.bookingdates.checkin", booking.getBookingDates().getCheckIn());
        validateResponseBody(lastResponse, "booking.bookingdates.checkout", booking.getBookingDates().getCheckOut());
        validateResponseBody(lastResponse, "booking.additionalneeds", booking.getAdditionalNeeds());
        
        int bookingId = lastResponse.jsonPath().getInt("bookingid");
        log.info("Booking created successfully with ID: {}", bookingId);
        return this;
    }

    public BookingApiClient getBooking(int bookingId) {
        log.info("Retrieving booking with ID: {}", bookingId);
        lastResponse = given()
                .spec(getRequestSpecification())
                .when()
                .get("/booking/" + bookingId);
        
        validateResponse(lastResponse, 200);
        log.info("Successfully retrieved booking details for ID: {}", bookingId);
        return this;
    }

    public BookingApiClient updateBooking(int bookingId, String testDataFile) {
        Booking booking = TestDataProvider.getBookingData(testDataFile);
        log.info("Updating booking with ID: {} for: {} {}", 
                bookingId, booking.getFirstName(), booking.getLastName());
        
        lastResponse = given()
                .spec(getAuthenticatedRequestSpec())
                .body(booking)
                .when()
                .put("/booking/" + bookingId);
        
        validateResponse(lastResponse, 200);
        validateResponseBody(lastResponse, "firstname", booking.getFirstName());
        validateResponseBody(lastResponse, "lastname", booking.getLastName());
        validateResponseBody(lastResponse, "totalprice", booking.getTotalPrice());
        validateResponseBody(lastResponse, "depositpaid", booking.getDepositPaid());
        validateResponseBody(lastResponse, "bookingdates.checkin", booking.getBookingDates().getCheckIn());
        validateResponseBody(lastResponse, "bookingdates.checkout", booking.getBookingDates().getCheckOut());
        validateResponseBody(lastResponse, "additionalneeds", booking.getAdditionalNeeds());
        
        log.info("Booking updated successfully for ID: {}", bookingId);
        return this;
    }

    public BookingApiClient deleteBooking(int bookingId) {
        log.info("Deleting booking with ID: {}", bookingId);
        lastResponse = given()
                .spec(getAuthenticatedRequestSpec())
                .when()
                .delete("/booking/" + bookingId);
        
        validateResponse(lastResponse, 201);
        log.info("Booking deleted successfully for ID: {}", bookingId);
        return this;
    }

    public BookingApiClient getBookingIds() {
        log.info("Retrieving all booking IDs");
        lastResponse = given()
                .spec(getRequestSpecification())
                .when()
                .get("/booking");
        
        validateResponse(lastResponse, 200);
        int bookingCount = lastResponse.jsonPath().getList("$").size();
        log.info("Successfully retrieved {} booking IDs", bookingCount);
        return this;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    @Override
    protected void validateResponse(Response response, int expectedStatusCode) {
        if (response.getStatusCode() != expectedStatusCode) {
            log.error("API call failed with status code: {}", response.getStatusCode());
            throw new RuntimeException("API call failed with status code: " + response.getStatusCode());
        }
        
        if (expectedStatusCode == 200 || expectedStatusCode == 201) {
            String responseBody = response.getBody().asString();
            if (responseBody == null || responseBody.isEmpty()) {
                log.error("Empty response body received");
                throw new RuntimeException("Empty response body received");
            }
        }
    }
} 