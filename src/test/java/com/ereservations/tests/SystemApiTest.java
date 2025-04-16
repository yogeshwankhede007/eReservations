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
        executePing();
    }

    @Step("Check API availability")
    private void executePing() {
        Response response = pingApiClient.ping().getLastResponse();
        ReportUtils.addStepToReport("Ping API", io.qameta.allure.model.Status.PASSED,
                "API is available and responding");
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }

    @Test
    @Description("Test API health status")
    @Severity(SeverityLevel.CRITICAL)
    @Story("System Health")
    public void testHealthCheck() {
        executeHealthCheck();
    }

    @Step("Check API health status")
    private void executeHealthCheck() {
        Response response = healthCheckApiClient.checkHealth().getLastResponse();
        ReportUtils.addStepToReport("Health Check", io.qameta.allure.model.Status.PASSED,
                "API health check passed");
        ReportUtils.attachText("Response Body", response.getBody().asString());
    }
} 