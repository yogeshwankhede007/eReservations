package com.ereservations.tests;

import com.ereservations.api.HealthCheckApiClient;
import com.ereservations.api.PingApiClient;
import com.ereservations.utils.ReportUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class SystemApiTest {
    private PingApiClient pingApiClient;
    private HealthCheckApiClient healthCheckApiClient;

    @BeforeClass
    public void setup() {
        pingApiClient = new PingApiClient();
        healthCheckApiClient = new HealthCheckApiClient();
    }

    @Test
    @Description("Test API availability using ping endpoint")
    @Severity(SeverityLevel.CRITICAL)
    @Story("System Health")
    public void testPing() {
        try {
            executePing();
        } catch (AssertionError e) {
            ReportUtils.addStepToReport("Ping API", io.qameta.allure.model.Status.FAILED,
                    "API ping failed: " + e.getMessage());
            fail("Ping test failed: " + e.getMessage());
        }
    }

    @Step("Check API availability")
    private void executePing() {
        Response response = pingApiClient.ping().getLastResponse();
        ReportUtils.addStepToReport("Ping API", io.qameta.allure.model.Status.PASSED,
                "API is available and responding");
        ReportUtils.attachText("Request Details", "GET /ping");
        ReportUtils.attachText("Response Body", response.getBody().asString());
        ReportUtils.attachText("Response Headers", response.getHeaders().toString());
    }

    @Test
    @Description("Test API health status")
    @Severity(SeverityLevel.CRITICAL)
    @Story("System Health")
    public void testHealthCheck() {
        try {
            executeHealthCheck();
        } catch (AssertionError e) {
            ReportUtils.addStepToReport("Health Check", io.qameta.allure.model.Status.FAILED,
                    "Health check failed: " + e.getMessage());
            fail("Health check test failed: " + e.getMessage());
        }
    }

    @Step("Check API health status")
    private void executeHealthCheck() {
        Response response = healthCheckApiClient.checkHealth().getLastResponse();
        ReportUtils.addStepToReport("Health Check", io.qameta.allure.model.Status.PASSED,
                "API health check passed");
        ReportUtils.attachText("Request Details", "GET /health");
        ReportUtils.attachText("Response Body", response.getBody().asString());
        ReportUtils.attachText("Response Headers", response.getHeaders().toString());
    }
} 