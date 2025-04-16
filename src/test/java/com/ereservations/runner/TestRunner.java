package com.ereservations.runner;

import com.ereservations.utils.ReportUtils;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        
        // Create XML Suite
        XmlSuite suite = new XmlSuite();
        suite.setName("Restful Booker Test Suite");
        suite.setParallel(XmlSuite.ParallelMode.METHODS);
        suite.setThreadCount(2);
        
        // Create XML Test
        XmlTest test = new XmlTest(suite);
        test.setName("Booking API Tests");
        
        // Create XmlClass objects
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("com.ereservations.tests.BookingApiTest"));
        test.setXmlClasses(classes);
        
        // Add suite to list
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        
        // Set testng.xml
        testNG.setXmlSuites(suites);
        
        // Set output directory with dynamic timestamp
        String reportDir = ReportUtils.getReportDirectory();
        testNG.setOutputDirectory(reportDir);
        
        // Run tests
        testNG.run();
        
        // Generate Allure report
        ReportUtils.generateAllureReport(reportDir);
    }
} 