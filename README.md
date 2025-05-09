# Regression
A Spring Boot server for collecting, storing, and analyzing error logs with REST APIs and AI-powered log analysis

### **Regression – Requirements Specification**

**Overview:**
**Regression** is a Spring Boot-based server for collecting, storing, and analyzing application error logs. It provides RESTful APIs for log ingestion and retrieval, with AI-driven analysis to detect anomalies, trends, and root causes.

### **Functional Requirements:**

1. **Log Ingestion API**

   * Accept error logs in JSON format via HTTP POST.
   * Support metadata: timestamp, severity, application ID, source, etc.

2. **Log Storage**

   * Persist logs in a scalable database (e.g., PostgreSQL, MongoDB).
   * Index logs for fast search and filtering.

3. **Log Retrieval API**

   * Fetch logs via GET endpoints with filters (time range, severity, keyword).

4. **AI-Powered Analysis**

   * Analyze logs using machine learning models to:

     * Detect anomalies and outliers.
     * Group similar error patterns.
     * Suggest probable root causes.

5. **Authentication & Authorization**

   * Secure API endpoints using JWT or OAuth2.
   * Role-based access control (e.g., viewer, admin).

6. **Monitoring & Metrics**

   * Expose system metrics (e.g., log rate, error types) via an endpoint (e.g., `/metrics`).

---

### **Non-Functional Requirements:**

* **Performance:** Handle high log volumes with minimal latency.
* **Scalability:** Support containerized deployment and clustering.
* **Extensibility:** Pluggable architecture for custom AI models or storage backends.
* **Reliability:** Fault-tolerant with retries and backup mechanisms.


## Setup Guide

### Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- SQLite 3 (or your preferred database)
- Docker (optional, for containerized deployment)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/kenya-jug/regression.git
   cd regression
   ```

2. **Configure the database**
   - SQLite database will be automatically created in the project directory
   - Update `src/main/resources/application.properties` with your database configuration:
     ```properties
     spring.datasource.url=jdbc:sqlite:regression.db
     spring.datasource.driver-class-name=org.sqlite.JDBC
     ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The server will start on `http://localhost:8080`

### Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t regression .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 regression
   ```

### API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8080/v3/api-docs`

### Environment Variables

The following environment variables can be configured:

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Port to run the application | 8080 |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | dev |
| `JWT_SECRET` | Secret key for JWT tokens | (required in production) |

### Troubleshooting

- If you encounter database issues, ensure you have write permissions in the project directory
- For port conflicts, change the `SERVER_PORT` environment variable
- Check the application logs for detailed error messages

### Development Workflow

1. **Running Tests**
   ```bash
   mvn test
   ```

2. **Code Style Check**
   ```bash
   mvn checkstyle:check
   ```

3. **Generating Documentation**
   ```bash
   mvn javadoc:javadoc
   ```

### Contributing

1. Create a new branch for your feature
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make your changes and commit them
   ```bash
   git add .
   git commit -m "Description of your changes"
   ```

3. Push your changes and create a pull request

### Support

For additional help or questions:
- Open an issue on GitHub
- Check the [Wiki](https://github.com/kenya-jug/regression/wiki) for detailed documentation
- Join our [Discord community](https://discord.gg/kenya-jug)

## Description
This PR addresses issue #16 by adding a comprehensive setup guide to the README.md file. The changes provide clear instructions for developers to set up and run the project locally.

## Changes Made
- Added **Prerequisites** section listing required software and tools
- Added **Local Development Setup** with step-by-step instructions:
  - Repository cloning
  - Database configuration
  - Build process
  - Running the application
- Added **Docker Deployment** instructions
- Added **API Documentation** access information
- Added **Environment Variables** configuration table
- Added **Troubleshooting** section for common issues
- Added **Development Workflow** section covering:
  - Running tests
  - Code style checks
  - Documentation generation
- Added **Contributing** guidelines
- Added **Support** section with community resources

## Testing
- [x] Verified all code blocks render correctly in GitHub
- [x] Tested all commands in the guide
- [x] Ensured proper formatting and markdown syntax

## Related Issues
Closes #16

## Screenshots
N/A - Documentation changes only

## Additional Notes
- The setup guide follows best practices for Spring Boot applications
- Instructions are clear and suitable for both new and experienced developers
- Added both local development and Docker deployment options





















<!-- coverage start -->
## 📊 Code Coverage Report

**Overall Coverage: 100.00% ✅**

| Metric      | Covered | Missed | Total | Coverage |
|-------------|---------|--------|--------|----------|
| INSTRUCTION | 1015 | 0 | 1015 | 100.00% ✅ |
| LINE | 299 | 0 | 299 | 100.00% ✅ |
| BRANCH | 8 | 0 | 8 | 100.00% ✅ |
| METHOD | 71 | 0 | 71 | 100.00% ✅ |
| CLASS | 11 | 0 | 11 | 100.00% ✅ |
| COMPLEXITY | 75 | 0 | 75 | 100.00% ✅ |
<!-- coverage end -->
