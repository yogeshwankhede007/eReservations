package com.ereservations.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ReportUtils {
    private static final String ALLURE_RESULTS_DIR = "target/allure-results";
    private static final String ALLURE_REPORT_DIR = "target/allure-report";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static String getReportDirectory() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String reportDir = ALLURE_RESULTS_DIR + "/test-report_" + timestamp;
        createDirectory(reportDir);
        return reportDir;
    }

    public static void generateAllureReport(String reportDir) {
        try {
            // Ensure Allure results directory exists
            createDirectory(ALLURE_RESULTS_DIR);
            
            // Generate Allure report
            ProcessBuilder processBuilder = new ProcessBuilder(
                "allure", "generate", ALLURE_RESULTS_DIR, "-o", ALLURE_REPORT_DIR, "--clean"
            );
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("Allure report generated successfully at: {}", ALLURE_REPORT_DIR);
                // Open report in browser
                ProcessBuilder serveBuilder = new ProcessBuilder("allure", "open", ALLURE_REPORT_DIR);
                serveBuilder.start();
            } else {
                log.error("Failed to generate Allure report. Exit code: {}", exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error generating Allure report: {}", e.getMessage());
        }
    }

    public static void addStepToReport(String stepName, Status status, String details) {
        Allure.step(stepName, () -> {
            Allure.description(details);
            if (status == Status.FAILED) {
                throw new RuntimeException("Step failed: " + stepName);
            }
        });
    }

    private static void createDirectory(String path) {
        try {
            Path dirPath = Paths.get(path);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                log.info("Created directory: {}", path);
            }
        } catch (IOException e) {
            log.error("Error creating directory {}: {}", path, e.getMessage());
        }
    }

    public static void attachScreenshot(String name, byte[] screenshot) {
        if (screenshot != null) {
            Allure.getLifecycle().addAttachment(
                name,
                "image/png",
                "png",
                screenshot
            );
        }
    }

    public static void attachText(String name, String content) {
        Allure.getLifecycle().addAttachment(
            name,
            "text/plain",
            "txt",
            content.getBytes()
        );
    }
} 