package com.ereservations.api;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;

@Slf4j
public class PingApiClient extends BaseApiClient {
    private Response lastResponse;

    public PingApiClient ping() {
        log.info("Checking API availability");
        lastResponse = given()
                .spec(getRequestSpecification())
                .when()
                .get("/ping");
        
        validateResponse(lastResponse, 201);
        log.info("API is available and responding");
        return this;
    }

    public Response getLastResponse() {
        return lastResponse;
    }
} 