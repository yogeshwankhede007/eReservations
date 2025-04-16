package com.ereservations.runner;

import com.ereservations.utils.ReportUtils;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        
        // Get suite file from command line arguments or use default
        String suiteFile = args.length > 0 ? args[0] : "src/test/resources/test-suites/all-tests.xml";
        
        // Create list of suite files
        List<String> suiteFiles = new ArrayList<>();
        suiteFiles.add(suiteFile);
        
        // Set suite files
        testNG.setTestSuites(suiteFiles);
        
        // Set output directory with dynamic timestamp
        String reportDir = ReportUtils.getReportDirectory();
        testNG.setOutputDirectory(reportDir);
        
        // Run tests
        testNG.run();
        
        // Generate Allure report
        ReportUtils.generateAllureReport(reportDir);
    }
} 