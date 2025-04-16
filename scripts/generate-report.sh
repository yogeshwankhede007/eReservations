#!/bin/bash

# Clean previous results
rm -rf target/allure-results
rm -rf target/allure-report

# Generate Allure report
allure generate target/allure-results --clean -o target/allure-report

# Serve the report using Allure CLI
echo "Starting Allure server..."
allure serve target/allure-results 