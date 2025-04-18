# eReservations API Testing Framework

A robust and scalable API testing framework for the Restful Booker API, built with Java, TestNG, and Gatling.

## 🏗️ Framework Architecture

```mermaid
graph TD
    subgraph "Test Framework"
        A[TestNG Test Suite] --> B[Base Test Classes]
        B --> C[API Client Layer]
        C --> D[RestAssured]
        B --> E[Test Data Management]
        B --> F[Reporting]
    end

    subgraph "Performance Testing"
        G[Gatling Simulations] --> H[Load Testing]
        G --> I[Stress Testing]
        G --> J[Performance Reports]
    end

    subgraph "Reporting & Analysis"
        K[ExtentReports] --> L[HTML Reports]
        M[TestNG Reports] --> N[XML Reports]
        O[Gatling Reports] --> P[Performance Metrics]
    end

    subgraph "CI/CD Integration"
        Q[Maven] --> R[Build Pipeline]
        R --> S[Test Execution]
        S --> T[Report Generation]
    end

    style A fill:#f9f,stroke:#333,stroke-width:2px
    style G fill:#bbf,stroke:#333,stroke-width:2px
    style K fill:#bfb,stroke:#333,stroke-width:2px
    style Q fill:#fbb,stroke:#333,stroke-width:2px
```

## 🛠️ Technical Stack

- **Core Framework**: Java 11
- **Testing**: TestNG 7.7.1
- **API Testing**: RestAssured 5.3.0
- **Performance Testing**: Gatling 3.9.5
- **Reporting**: ExtentReports 5.1.1
- **Build Tool**: Maven
- **Logging**: SLF4J 2.0.7
- **JSON Processing**: Jackson 2.15.2

## 📁 Project Structure

```
eReservations/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/ereservations/
│   │           ├── api/              # API Client Layer
│   │           │   ├── BaseApiClient.java
│   │           │   ├── BookingApiClient.java
│   │           │   ├── HealthCheckApiClient.java
│   │           │   ├── PingApiClient.java
│   │           │   └── SystemApiClient.java
│   │           └── models/           # Data Models
│   └── test/
│       ├── java/
│       │   └── com/ereservations/
│       │       └── tests/            # Test Classes
│       ├── resources/
│       │   ├── config.properties     # Configuration
│       │   └── test-suites/          # TestNG Suites
│       └── gatling/
│           ├── simulations/          # Gatling Tests
│           └── resources/            # Gatling Resources
├── target/
│   ├── surefire-reports/            # TestNG Reports
│   └── gatling/results/             # Gatling Reports
└── pom.xml                          # Maven Configuration
```

## 🔄 Test Flow Architecture

```mermaid
sequenceDiagram
    participant T as TestNG Test
    participant B as Base Test
    participant A as API Client
    participant R as RestAssured
    participant S as System Under Test

    T->>B: Initialize Test
    B->>A: Create API Client
    A->>R: Configure Request
    R->>S: Send API Request
    S-->>R: Receive Response
    R-->>A: Process Response
    A-->>B: Validate Response
    B-->>T: Report Results
```

## 🚀 Key Features

1. **Modular API Client Layer**
   - Base client with common functionality
   - Specialized clients for each API endpoint
   - Automatic authentication handling
   - Response validation and error handling

2. **Comprehensive Testing**
   - Functional API testing
   - Performance testing with Gatling
   - Security testing capabilities
   - Data validation and verification

3. **Advanced Reporting**
   - ExtentReports integration
   - Gatling performance reports
   - TestNG HTML reports
   - Screenshot capture for failures

4. **Configuration Management**
   - Environment-specific configurations
   - Dynamic test data generation
   - Flexible test suite organization

## 🧪 Test Categories

1. **Functional Tests**
   - Basic CRUD operations
   - Input validation
   - Error handling
   - Edge cases

2. **Performance Tests**
   - Load testing
   - Stress testing
   - Endurance testing
   - Spike testing

3. **Security Tests**
   - Authentication
   - Authorization
   - Input validation
   - Error handling

## 📊 Reporting

```mermaid
graph LR
    A[Test Execution] --> B[ExtentReports]
    A --> C[TestNG Reports]
    A --> D[Gatling Reports]
    B --> E[HTML Dashboard]
    C --> F[XML Results]
    D --> G[Performance Charts]
    E --> H[Test Summary]
    F --> H
    G --> H
```

## 🛠️ Getting Started

1. **Prerequisites**
   - Java 11 or higher
   - Maven 3.6 or higher

2. **Installation**
   ```bash
   git clone git@github.com:yogeshwankhede007/eReservations.git
   cd eReservations
   mvn clean install
   ```

3. **Running Tests**
   ```bash
   # Run all tests
   mvn clean test

   # Run specific test suite
   mvn clean test -DsuiteXmlFile=src/test/resources/test-suites/sanity-suite.xml

   # Run Gatling tests
   mvn clean gatling:test
   ```

## 📈 Performance Metrics

The framework includes comprehensive performance testing capabilities:

- Response time tracking
- Throughput measurement
- Error rate monitoring
- Resource utilization analysis

## 🔒 Security Features

- Secure credential management
- Token-based authentication
- Input sanitization
- Error message handling

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
