package com.ereservations.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public class ReportUtils {
    private static final String ALLURE_RESULTS_DIR = "target/allure-results";
    private static final String ALLURE_REPORT_DIR = "target/allure-report";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ReportUtils() {
        // Private constructor to prevent instantiation
    }

    public static String getReportDirectory() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String reportDir = ALLURE_RESULTS_DIR + "/test-report_" + timestamp;
        createDirectory(reportDir);
        return reportDir;
    }

    public static void generateAllureReport(String reportDir) {
        log.info("Generating Allure report in directory: {}", reportDir);
        try {
            Files.createDirectories(Paths.get(reportDir));
            log.debug("Created report directory: {}", reportDir);
            
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
            throw new RuntimeException("Failed to generate Allure report", e);
        }
    }

    public static void addStepToReport(String name, Status status, String description) {
        log.info("Adding step to report: {} - {}", name, description);
        StepResult result = new StepResult()
            .setName(name)
            .setStatus(status)
            .setDescription(description);
        Allure.getLifecycle().startStep(UUID.randomUUID().toString(), result);
        Allure.getLifecycle().stopStep();
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
        log.info("Attaching screenshot: {}", name);
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
    }

    public static void attachText(String name, String content) {
        log.info("Attaching text to report: {}", name);
        Allure.addAttachment(name, "text/plain", content);
    }

    public static void attachJson(String name, String content) {
        log.info("Attaching JSON to report: {}", name);
        Allure.addAttachment(name, "application/json", content);
    }

    public static void attachFile(String name, Path filePath) {
        log.info("Attaching file to report: {}", name);
        try {
            String content = new String(Files.readAllBytes(filePath));
            String contentType = Files.probeContentType(filePath);
            Allure.addAttachment(name, contentType, content);
        } catch (IOException e) {
            log.error("Failed to attach file: {}", e.getMessage());
        }
    }

    private static String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }

    public static void addDescription(String description) {
        log.info("Adding description to report: {}", description);
        Allure.description(description);
    }

    public static void addSeverity(io.qameta.allure.SeverityLevel severity) {
        log.info("Adding severity to report: {}", severity);
        Allure.label("severity", severity.value());
    }

    public static void addStory(String story) {
        log.info("Adding story to report: {}", story);
        Allure.label("story", story);
    }

    public static void addFeature(String feature) {
        log.info("Adding feature to report: {}", feature);
        Allure.label("feature", feature);
    }

    public static void addEpic(String epic) {
        log.info("Adding epic to report: {}", epic);
        Allure.label("epic", epic);
    }
} 