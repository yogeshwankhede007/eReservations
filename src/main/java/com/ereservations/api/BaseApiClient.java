package com.ereservations.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class BaseApiClient {
    protected static final Logger log = LoggerFactory.getLogger(BaseApiClient.class);
    protected static Properties config;
    protected static RequestSpecification requestSpec;
    private static String authToken;
    private static final long MAX_RESPONSE_TIME = 5000; // 5 seconds in milliseconds

    static {
        try {
            config = new Properties();
            // Try to load from classpath first
            try (InputStream inputStream = BaseApiClient.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (inputStream != null) {
                    config.load(inputStream);
                } else {
                    // Fallback to file system path
                    config.load(new FileInputStream("src/main/resources/config.properties"));
                }
            }
            
            // Initialize RestAssured
            RestAssured.baseURI = config.getProperty("base.url");
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
            
            requestSpec = new RequestSpecBuilder()
                    .setContentType(ContentType.JSON)
                    .addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter())
                    .build();
            
            log.info("Successfully initialized BaseApiClient with base URL: {}", RestAssured.baseURI);
            
        } catch (IOException e) {
            log.error("Error loading configuration: {}", e.getMessage());
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
                String token = response.jsonPath().getString("token");
                log.info("Successfully obtained auth token");
                return token;
            } else {
                log.error("Failed to get auth token. Status code: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Error getting auth token: {}", e.getMessage());
            return null;
        }
    }

    protected void validateResponse(Response response, int expectedStatusCode) {
        if (response.getStatusCode() != expectedStatusCode) {
            String errorMessage = String.format("Expected status code %d but got %d. Response: %s",
                    expectedStatusCode, response.getStatusCode(), response.getBody().asString());
            log.error(errorMessage);
            throw new AssertionError(errorMessage);
        }
        
        if (expectedStatusCode == 200 || expectedStatusCode == 201) {
            String responseBody = response.getBody().asString();
            if (responseBody == null || responseBody.isEmpty()) {
                String errorMessage = "Empty response body received";
                log.error(errorMessage);
                throw new AssertionError(errorMessage);
            }
        }
        
        // Validate response time
        long responseTime = response.getTime();
        if (responseTime > MAX_RESPONSE_TIME) {
            log.warn("Response time ({} ms) exceeded maximum allowed time ({} ms)", 
                    responseTime, MAX_RESPONSE_TIME);
        }
    }

    protected void validateResponseBody(Response response, String jsonPath, Object expectedValue) {
        try {
            Object actualValue = response.jsonPath().get(jsonPath);
            if (expectedValue == null && actualValue == null) {
                return;
            }
            if (expectedValue != null && expectedValue.equals(actualValue)) {
                log.debug("Successfully validated response body at path {}", jsonPath);
            } else {
                String errorMessage = String.format("Expected %s to be %s but got %s",
                        jsonPath, expectedValue, actualValue);
                log.error(errorMessage);
                throw new AssertionError(errorMessage);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Error validating response body at path %s: %s",
                    jsonPath, e.getMessage());
            log.error(errorMessage);
            throw new AssertionError(errorMessage);
        }
    }
} 