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

    protected String getAuthToken() {
        if (authToken == null) {
            logger.info("Requesting new authentication token");
            Response response = given()
                    .spec(getRequestSpecification())
                    .body("{ \"username\": \"" + config.getProperty("auth.username") + "\", " +
                          "\"password\": \"" + config.getProperty("auth.password") + "\" }")
                    .when()
                    .post("/auth");
            
            validateResponse(response, 200);
            authToken = response.jsonPath().getString("token");
            logger.info("Successfully obtained authentication token");
        }
        return authToken;
    }

    protected RequestSpecification getAuthenticatedRequestSpec() {
        return getRequestSpecification()
                .cookie("token", getAuthToken());
    }

    protected void validateResponse(Response response, int expectedStatusCode) {
        long responseTime = response.getTime();
        logger.info("Response time: {} ms", responseTime);
        
        if (responseTime > MAX_RESPONSE_TIME) {
            logger.error("Response time exceeded maximum limit of {} ms. Actual time: {} ms", 
                    MAX_RESPONSE_TIME, responseTime);
            throw new RuntimeException("Response time exceeded maximum limit");
        }

        if (response.getStatusCode() != expectedStatusCode) {
            logger.error("Expected status code: {}, Actual status code: {}", 
                    expectedStatusCode, response.getStatusCode());
            logger.error("Response body: {}", response.getBody().asString());
            throw new RuntimeException("API call failed with status code: " + response.getStatusCode());
        }

        logger.info("Response validation successful. Status code: {}", response.getStatusCode());
    }

    protected void validateResponseBody(Response response, String field, Object expectedValue) {
        Object actualValue = response.jsonPath().get(field);
        logger.info("Validating field: {}, Expected: {}, Actual: {}", 
                field, expectedValue, actualValue);
        
        if (!expectedValue.equals(actualValue)) {
            logger.error("Field validation failed. Field: {}, Expected: {}, Actual: {}", 
                    field, expectedValue, actualValue);
            throw new RuntimeException("Field validation failed for: " + field);
        }
        logger.info("Field validation successful: {}", field);
    }
} 