package com.ereservations.api;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;

@Slf4j
public class PingApiClient extends BaseApiClient {
    private Response lastResponse;

    public Response ping() {
        log.info("Sending ping request");
        lastResponse = given()
                .spec(getRequestSpecification())
                .when()
                .get("/ping");
        validateResponse(lastResponse, 201);
        log.info("Ping request successful");
        return lastResponse;
    }

    public Response getLastResponse() {
        return lastResponse;
    }
} 