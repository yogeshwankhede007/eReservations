package com.ereservations.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;

@Slf4j
public class BaseApiClient {
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiClient.class);
    protected static Properties config;
    protected static RequestSpecification requestSpec;
    private static String authToken;
    private static final long MAX_RESPONSE_TIME = 5000; // 5 seconds in milliseconds

    static {
        try {
            config = new Properties();
            config.load(new FileInputStream("src/main/resources/config.properties"));
            
            // Initialize RestAssured
            RestAssured.baseURI = config.getProperty("base.url");
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
            
            requestSpec = new RequestSpecBuilder()
                    .setContentType(ContentType.JSON)
                    .addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter())
                    .build();
            
        } catch (IOException e) {
            logger.error("Error loading configuration: {}", e.getMessage());
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    protected RequestSpecification getRequestSpecification() {
        return requestSpec;
    }

    protected RequestSpecification getAuthenticatedRequestSpec() {
        if (authToken == null) {
            authToken = getAuthToken();
        }
        return requestSpec.auth().oauth2(authToken);
    }

    private String getAuthToken() {
        try {
            Response response = given()
                    .spec(requestSpec)
                    .body("{\"username\":\"" + config.getProperty("auth.username") + 
                          "\",\"password\":\"" + config.getProperty("auth.password") + "\"}")
                    .when()
                    .post("/auth");
            
            if (response.getStatusCode() == 200) {
                return response.jsonPath().getString("token");
            } else {
                logger.error("Failed to get auth token. Status code: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting auth token: {}", e.getMessage());
            return null;
        }
    }

    protected void validateResponse(Response response, int expectedStatusCode) {
        if (response.getStatusCode() != expectedStatusCode) {
            String errorMessage = String.format("Expected status code %d but got %d. Response: %s",
                    expectedStatusCode, response.getStatusCode(), response.getBody().asString());
            logger.error(errorMessage);
            throw new AssertionError(errorMessage);
        }
        
        if (expectedStatusCode == 200 || expectedStatusCode == 201) {
            String responseBody = response.getBody().asString();
            if (responseBody == null || responseBody.isEmpty()) {
                String errorMessage = "Empty response body received";
                logger.error(errorMessage);
                throw new AssertionError(errorMessage);
            }
        }
    }

    protected void validateResponseBody(Response response, String jsonPath, Object expectedValue) {
        try {
            Object actualValue = response.jsonPath().get(jsonPath);
            if (!expectedValue.equals(actualValue)) {
                String errorMessage = String.format("Expected %s to be %s but got %s",
                        jsonPath, expectedValue, actualValue);
                logger.error(errorMessage);
                throw new AssertionError(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Error validating response body at path %s: %s",
                    jsonPath, e.getMessage());
            logger.error(errorMessage);
            throw new AssertionError(errorMessage);
        }
    }
} 