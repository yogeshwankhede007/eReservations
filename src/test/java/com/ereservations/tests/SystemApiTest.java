package com.ereservations.tests;

import com.ereservations.api.SystemApiClient;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

@Slf4j
public class SystemApiTest {

    private final SystemApiClient apiClient = new SystemApiClient();

    @Test(description = "Test API availability using ping endpoint")
    public void testPing() {
        log.info("Testing API ping endpoint");
        Response response = apiClient.ping();
        Assert.assertEquals(response.getStatusCode(), 201, "Ping endpoint should return 201");
        log.info("Ping test completed successfully");
    }

    @Test(description = "Test API health status")
    public void testHealth() {
        log.info("Testing API health endpoint");
        Response response = apiClient.health();
        Assert.assertEquals(response.getStatusCode(), 201, "Health endpoint should return 201");
        log.info("Health test completed successfully");
    }
} 