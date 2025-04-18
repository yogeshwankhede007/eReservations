package com.ereservations.api;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public class SystemApiClient extends BaseApiClient {
    private static final Logger log = LoggerFactory.getLogger(SystemApiClient.class);
    private Response lastResponse;

    public Response ping() {
        log.info("Checking API availability");
        lastResponse = given()
                .spec(getRequestSpecification())
                .when()
                .get("/ping");
        
        validateResponse(lastResponse, 201);
        log.info("API is available and responding");
        return lastResponse;
    }

    public Response health() {
        log.info("Checking API health status");
        lastResponse = given()
                .spec(getRequestSpecification())
                .when()
                .get("/health");
        
        validateResponse(lastResponse, 201);
        log.info("API health check passed");
        return lastResponse;
    }

    public Response getLastResponse() {
        return lastResponse;
    }
} 