# eReservations - Enterprise Travel Booking System

![eReservations Logo](https://img.shields.io/badge/eReservations-Enterprise%20Travel-blue?style=for-the-badge)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg?style=flat)](https://github.com/yogeshwankhede007/eReservations)
[![Test Coverage](https://img.shields.io/badge/coverage-95%25-brightgreen.svg?style=flat)](https://github.com/yogeshwankhede007/eReservations)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)

## 🌟 Overview

eReservations is a state-of-the-art travel booking automation framework designed for enterprise-level travel management companies. Built with scalability and reliability in mind, it provides a robust API testing solution for travel booking systems.

### 🎯 Key Features

- **Comprehensive API Testing**: End-to-end testing of booking workflows
- **ChainTest Reporting**: Detailed test execution insights with ChainTest framework
- **Data-Driven Testing**: External test data management for flexible testing scenarios
- **Parallel Execution**: Optimized test execution with multi-threading support
- **Authentication Management**: Secure token-based authentication handling
- **Logging & Monitoring**: Detailed logging with request/response tracking
- **Negative Testing**: Comprehensive validation of error scenarios
- **Security Testing**: Built-in security testing capabilities

## 🚀 Technology Stack

- **Java 11**: Core programming language
- **TestNG**: Test automation framework
- **REST Assured**: API testing library
- **ChainTest**: Test reporting framework
- **Maven**: Build and dependency management
- **Lombok**: Boilerplate code reduction
- **SLF4J**: Logging framework
- **Jackson**: JSON processing library

## 📋 Prerequisites

- Java 11 or higher
- Maven 3.6.3 or higher
- ChainTest Framework

## 🛠️ Setup & Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yogeshwankhede007/eReservations.git
   cd eReservations
   ```

2. **Install Dependencies**
   ```bash
   mvn clean install
   ```

3. **Configure Environment**
   - Update `src/main/resources/config.properties` with your environment settings
   ```properties
   base.url=https://your-api-endpoint.com
   auth.username=your-username
   auth.password=your-password
   ```

## 🏃‍♂️ Running Tests

### Running All Tests
```bash
mvn clean test
```

### Running Specific Test Suites
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

### Generating Reports
```bash
mvn chaintest:report
```

## 📊 Test Reports

The framework generates detailed ChainTest reports including:
- Test execution statistics
- Step-by-step test execution details
- Request/Response logs
- Test execution timeline
- Environment details
- Negative test results
- Security test findings

## 🏗️ Project Structure

```
eReservations/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ereservations/
│   │   │       ├── api/
│   │   │       ├── models/
│   │   │       └── utils/
│   │   └── resources/
│   └── test/
│       ├── java/
│       │   └── com/ereservations/
│       │       ├── tests/
│       │       └── utils/
│       └── resources/
│           ├── test-data/
│           │   ├── negative-test-data.json
│           │   └── positive-test-data.json
│           └── testng.xml
```

## 🔄 Recent Updates

### Version 1.2.0
- Replaced Allure reporting with ChainTest framework
- Updated codebase to comply with SonarCloud code scanning standards
- Enhanced error handling and logging mechanisms
- Improved test execution performance
- Implemented strict code quality checks

### Version 1.1.0
- Enhanced reporting with improved attachment handling
- Added comprehensive negative test scenarios
- Implemented security testing capabilities
- Fixed JSON parsing issues in test data
- Improved error handling and logging
- Updated test execution configuration

### Version 1.0.0
- Initial release with basic booking API testing
- Basic reporting integration
- Core API client implementation
- Basic test data management

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🌐 Support

For support and queries, please contact:
- Email: yogi.wankhede007@gmail.com
- Documentation: [docs.ereservations.com](https://restful-booker.herokuapp.com/apidoc/index.html#api-Ping)
- Issue Tracker: [GitHub Issues](https://github.com/yogeshwankhede007/eReservations/issues)

---

<p align="center">Made with ❤️ by Yogesh W.</p>
