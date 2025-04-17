package com.ereservations.listeners;

import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class TestChainListener implements ITestListener {
    private static final String REPORT_DIR = "target/test-reports/";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private FileWriter reportWriter;

    @Override
    public void onStart(ITestContext context) {
        log.info("Starting test suite: {}", context.getName());
        createReportFile();
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Finished test suite: {}", context.getName());
        log.info("Passed tests: {}", context.getPassedTests().size());
        log.info("Failed tests: {}", context.getFailedTests().size());
        log.info("Skipped tests: {}", context.getSkippedTests().size());
        closeReportWriter();
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("Starting test: {}", result.getName());
        writeToReport("START", result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Test passed: {}", result.getName());
        writeToReport("PASS", result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("Test failed: {}", result.getName());
        log.error("Failure details: {}", result.getThrowable());
        writeToReport("FAIL", result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("Test skipped: {}", result.getName());
        if (result.getThrowable() != null) {
            log.warn("Skip reason: {}", result.getThrowable().getMessage());
        }
        writeToReport("SKIP", result);
    }

    private void createReportFile() {
        try {
            new File(REPORT_DIR).mkdirs();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File reportFile = new File(REPORT_DIR + "test_report_" + timestamp + ".html");
            reportWriter = new FileWriter(reportFile);
            writeReportHeader();
        } catch (IOException e) {
            log.error("Failed to create report file", e);
        }
    }

    private void writeReportHeader() throws IOException {
        reportWriter.write("<!DOCTYPE html>\n");
        reportWriter.write("<html>\n");
        reportWriter.write("<head>\n");
        reportWriter.write("<title>Test Execution Report</title>\n");
        reportWriter.write("<style>\n");
        reportWriter.write("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        reportWriter.write("table { border-collapse: collapse; width: 100%; }\n");
        reportWriter.write("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        reportWriter.write("th { background-color: #f2f2f2; }\n");
        reportWriter.write(".pass { background-color: #dff0d8; }\n");
        reportWriter.write(".fail { background-color: #f2dede; }\n");
        reportWriter.write(".skip { background-color: #fcf8e3; }\n");
        reportWriter.write("</style>\n");
        reportWriter.write("</head>\n");
        reportWriter.write("<body>\n");
        reportWriter.write("<h1>Test Execution Report</h1>\n");
        reportWriter.write("<table>\n");
        reportWriter.write("<tr><th>Timestamp</th><th>Status</th><th>Test Name</th><th>Duration (ms)</th><th>Details</th></tr>\n");
    }

    private void writeToReport(String status, ITestResult result) {
        try {
            String timestamp = DATE_FORMAT.format(new Date());
            String testName = result.getName();
            long duration = result.getEndMillis() - result.getStartMillis();
            String details = "";

            if (result.getThrowable() != null) {
                details = result.getThrowable().getMessage();
            }

            reportWriter.write(String.format(
                "<tr class='%s'><td>%s</td><td>%s</td><td>%s</td><td>%d</td><td>%s</td></tr>\n",
                status.toLowerCase(), timestamp, status, testName, duration, details
            ));
            reportWriter.flush();
        } catch (IOException e) {
            log.error("Failed to write to report", e);
        }
    }

    private void closeReportWriter() {
        try {
            reportWriter.write("</table>\n");
            reportWriter.write("</body>\n");
            reportWriter.write("</html>\n");
            reportWriter.close();
        } catch (IOException e) {
            log.error("Failed to close report writer", e);
        }
    }
} 