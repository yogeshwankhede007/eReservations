package com.ereservations.runner;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        
        // Create test suite
        XmlSuite suite = new XmlSuite();
        suite.setName("Booking API Test Suite");
        
        // Create test
        XmlTest test = new XmlTest(suite);
        test.setName("Booking API Tests");
        test.setPreserveOrder(true);
        
        // Add test classes
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("com.ereservations.tests.BookingApiTest"));
        classes.add(new XmlClass("com.ereservations.tests.BookingNegativeTest"));
        classes.add(new XmlClass("com.ereservations.tests.SystemApiTest"));
        test.setXmlClasses(classes);
        
        // Add suite to TestNG
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testNG.setXmlSuites(suites);
        
        // Run tests
        testNG.run();
    }
} 