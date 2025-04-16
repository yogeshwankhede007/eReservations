#!/bin/bash

# Default values
RUNNER="testng.xml"
GENERATE_REPORT=true

# Function to display usage
usage() {
    echo "Usage: $0 [options]"
    echo "Options:"
    echo "  -r, --runner <file>    Specify test runner file (default: testng.xml)"
    echo "  -n, --no-report       Don't generate report after tests"
    echo "  -h, --help            Display this help message"
    exit 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -r|--runner)
            RUNNER="$2"
            shift 2
            ;;
        -n|--no-report)
            GENERATE_REPORT=false
            shift
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo "Unknown option: $1"
            usage
            ;;
    esac
done

# Validate runner file exists
if [ ! -f "$RUNNER" ]; then
    echo "Error: Test runner file '$RUNNER' not found!"
    exit 1
fi

echo "Running tests using runner: $RUNNER"

# Clean previous results
rm -rf target/allure-results
rm -rf target/allure-report

# Run tests
mvn clean test -Dtestng.suiteXmlFile="$RUNNER"

# Generate report if requested
if [ "$GENERATE_REPORT" = true ]; then
    echo "Generating Allure report..."
    allure generate target/allure-results --clean -o target/allure-report
    
    # Serve the report using Allure CLI
    echo "Starting Allure server..."
    allure serve target/allure-results
fi 