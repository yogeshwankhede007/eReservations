package com.ereservations.api;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;

@Slf4j
public class HealthCheckApiClient extends BaseApiClient {
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