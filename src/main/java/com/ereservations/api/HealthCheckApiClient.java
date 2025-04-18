package com.ereservations.api;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public class HealthCheckApiClient extends BaseApiClient {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckApiClient.class);

    private Response lastResponse;

    public HealthCheckApiClient checkHealth() {
        log.info("Checking API health status");
        lastResponse = given()
                .spec(getRequestSpecification())
                .when()
                .get("/health");
        
        validateResponse(lastResponse, 200);
        validateResponseBody(lastResponse, "status", "OK");
        log.info("API health check passed");
        return this;
    }

    public Response getLastResponse() {
        return lastResponse;
    }
} 