# API Design Document
###  Regression

## Overview

This document defines the REST API specification for the Regression backend system, including endpoints for log ingestion, retrieval, AI analysis, and metrics.

---

## Authentication

- **Auth Method**: JWT-based Authentication
- **Headers**: `Authorization: Bearer <token>`
- **Roles**:
  - `admin`: Full access
  - `viewer`: Read-only access to logs and analysis
  - `datasource`: Write-only access to submit logs and analysis

---

## 1. Log Ingestion

### `POST /logs`

**Description**: Ingest an application error log into the system.

**Auth Required**: Yes (`admin`, `datasource`)

**Request Body** (JSON):

```json
{
  "timestamp": "2025-05-08T12:00:00Z",
  "severity": "ERROR",
  "applicationId": "app-123",
  "applicationVersion": "v2.3.1",
  "environment": "production",
  "source": "service-A",
  "host": "ip-10-0-1-23.ec2.internal",
  "thread": "http-nio-8080-exec-7",
  "logger": "com.example.user.UserService",
  "message": "NullPointerException in UserService",
  "exception": {
    "type": "java.lang.NullPointerException",
    "stackTrace": [
      "com.example.user.UserService.getUser(UserService.java:45)",
      "com.example.api.UserController.getUser(UserController.java:22)"
    ],
    "cause": "User object was null"
  },
  "metrics": {
    "cpuUsage": 0.75,
    "memoryUsageMB": 1536,
    "diskSpaceRemainingMB": 20480
  },
  "context": {
    "userId": "u789",
    "sessionId": "sess-456",
    "transactionId": "txn-001122",
    "featureFlag": "new-login-flow"
  },
  "tags": ["login", "user-service", "high-priority"],
  "customAttributes": {
    "requestId": "req-998877",
    "deploymentRegion": "us-east-1"
  }
}
````

**Required Fields**:

* `timestamp`
* `severity`
* `applicationId`
* `message`

**Response**:

* `201 Created`
* On error: `400 Bad Request`, `401 Unauthorized`

---

## 2. Log Retrieval

### `GET /logs`

**Description**: Retrieve logs with filtering and pagination support.

**Auth Required**: Yes (`admin`, `viewer`)

**Query Parameters**:

| Param         | Type     | Description                       |
|---------------|----------|-----------------------------------|
| startTime     | ISO Date | Filter logs after this timestamp  |
| endTime       | ISO Date | Filter logs before this timestamp |
| severity      | String   | e.g., `ERROR`, `WARN`, `INFO`     |
| applicationId | String   | Filter by application ID          |
| keyword       | String   | Keyword search in messages        |
| limit         | Int      | Max number of logs to return      |
| offset        | Int      | For pagination                    |

**Example**:

```http
GET /logs?severity=ERROR&startTime=2025-05-01T00:00:00Z&limit=50
```

```bash
curl -X GET "https://api-domain.com/logs?severity=ERROR&startTime=2025-05-01T00:00:00Z&limit=50" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Accept: application/json"

```

**Response** (`200 OK`):

```json
[
  {
    "id": "log-001",
    "timestamp": "2025-05-08T12:00:00Z",
    "severity": "ERROR",
    "applicationId": "app-123",
    "source": "service-A",
    "message": "NullPointerException in UserService",
    "context": { "userId": "u789" }
  }, 
   {
    "id": "log-002",
    "timestamp": "2025-06-11T12:00:00Z",
    "severity": "ERROR",
    "applicationId": "app-123",
    "source": "service-A",
    "message": "SecurityException in UserService",
    "context": { "userId": "u789" }
  }
]
```

---

## 3. AI-Powered Analysis

### `GET /logs/analysis/anomalies/{appId}`

**Description**: Retrieve logs identified as anomalies by the AI engine by app id.

**Auth Required**: Yes (`admin`, `viewer`)

**Query Parameters**:

* Same as `/logs` endpoint (filters)

**Response**:

```json
[
  {
    "id": "log-301",
    "message": "OutOfMemoryError in PaymentService",
    "anomalyScore": 0.95
  }
]
```


### Fetch Regression Logs with AI Analysis

### `GET /logs/analysis/regressions/{appId}`

**Description**: Fetch logs identified as regressions by the AI model. A regression is defined as an error that reappears in a pattern after a fix was applied.

**Auth Required**: Yes (`admin`, `viewer`)
   
**Query Parameters**:

| Param         | Type     | Description                       |
|---------------|----------|-----------------------------------|
| startTime     | ISO Date | Filter logs after this timestamp  |
| endTime       | ISO Date | Filter logs before this timestamp |
| severity      | String   | e.g., `ERROR`, `WARN`, `INFO`     |
| applicationId | String   | Filter by application ID          |
| keyword       | String   | Keyword search in messages        |
| limit         | Int      | Max number of logs to return      |
| offset        | Int      | For pagination                    |


**Example**:

```bash
   GET /logs/analysis/regressions?severity=ERROR&regressionScore=0.85&limit=50
```

```bash
   curl -X GET "https://api-domain.com/logs/analysis/regressions?severity=ERROR&regressionScore=0.85&limit=50" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Accept: application/json"
```

***Response**

```json
{
  "regressions": [
    {
      "id": "log-301",
      "timestamp": "2025-05-08T12:00:00Z",
      "severity": "ERROR",
      "applicationId": "app-123",
      "source": "service-A",
      "message": "OutOfMemoryError in PaymentService",
      "anomalyScore": 0.92,
      "regressionScore": 0.88,
      "relatedLogs": [
        "log-100",
        "log-102",
        "log-104"
      ],
      "rootCauseSuggestions": [
        "Memory leak in PaymentProcessor",
        "Insufficient server resources"
      ],
      "affectedAppVersions": [
        "v2.1.0",
        "v2.2.3",
        "v2.3.1"
      ]
    },
    {
      "id": "log-302",
      "timestamp": "2025-05-08T14:00:00Z",
      "severity": "ERROR",
      "applicationId": "app-456",
      "source": "service-B",
      "message": "NullPointerException in UserService",
      "anomalyScore": 0.90,
      "regressionScore": 0.86,
      "relatedLogs": [
        "log-150",
        "log-155",
        "log-160"
      ],
      "rootCauseSuggestions": [
        "Issue with UserService initialization",
        "Recent deployment bug"
      ],
      "affectedAppVersions": [
        "v1.4.2",
        "v1.5.0"
      ]
    }
  ]
}

```
---

### `GET /logs/analysis/clusters/{appId}`

**Description**: Group logs by similarity to identify patterns.

**Response**:

```json
[
  {
    "clusterId": "c1",
    "representativeMessage": "NullPointerException in UserService",
    "logIds": ["log-101", "log-102", "log-107"]
  },
   {
    "clusterId": "c2",
    "representativeMessage": "SecurityException in UserService",
    "logIds": ["log-333", "log-444", "log-555"]
  },
   {
    "clusterId": "c3",
    "representativeMessage": "StackoverflowException in LogisticsService",
    "logIds": ["log-101", "log-1122", "log-3322"]
  }
]
```

---

## 4. Metrics

### `GET /metrics/{appId}`

**Description**: Expose system-level metrics.

**Auth Required**: Yes (`admin` only)

**Response**:

```json
{
   "logIngestionRate": 200,
   "errorCountBySeverity": {
      "ERROR": 120,
      "WARN": 45,
      "INFO": 300
   },
   "activeApplications": 5,
   "highestIncidentTimeOfDay": "Afternoon",
   "highestIncidentDayOfWeek": "Tuesday",
   "mostRecurringMessages": {
      "ERROR": "NullPointerException in UserService",
      "WARN": "Deprecated API usage in NotificationService",
      "INFO": "User login successful"
   },
   "appVersionWithMostErrors": {
      "applicationId": "app-123",
      "version": "v2.3.1",
      "errorCount": 47
   }
}
```
---

## 5. Security and Error Handling

### Common Response Codes:

* `200 OK`: Successful operation
* `201 Created`: Resource created
* `400 Bad Request`: Validation error
* `401 Unauthorized`: Missing/invalid token
* `403 Forbidden`: Insufficient permissions
* `404 Not Found`: Resource doesn't exist
* `500 Internal Server Error`: Unexpected failure

---

## OpenAPI / Swagger

* API is documented via Swagger (`/swagger-ui.html`).
* OpenAPI 3.0 spec is generated with Springdoc.

---

## Future Enhancements

* Support for bulk log ingestion (`POST /logs/bulk`)
* WebSocket-based live log streaming
* Rate limiting and API usage tracking

---

## Appendix: How AI Log Grouping and Anomaly Detection Works

### Overview
The Regression backend leverages AI/ML techniques to automatically analyze incoming logs for grouping similar errors and detecting anomalies. This enables proactive identification of issues, root cause analysis, and trend detection.

### Log Grouping (Clustering)
- **Technique:** The system uses unsupervised machine learning algorithms (such as k-means, DBSCAN, or embedding-based clustering) to group logs with similar messages, stack traces, or metadata.
- **Features Used:**
  - Log message text (vectorized using NLP techniques)
  - Exception types and stack traces
  - Application and source metadata
- **Purpose:**
  - Identify recurring error patterns
  - Reduce noise by grouping duplicate or similar logs
  - Surface representative errors for faster triage

### Anomaly Detection
- **Technique:** The system applies statistical and machine learning models (e.g., Isolation Forest, autoencoders, or time-series analysis) to detect outliers in log data.
- **Features Used:**
  - Frequency and rate of log events
  - Severity and error type distribution
  - Unusual patterns in log content or metadata
- **Purpose:**
  - Alert on rare or unexpected errors
  - Detect spikes or drops in error rates
  - Highlight logs with high anomaly scores for investigation

### Example Workflow
1. **Ingestion:** Logs are ingested and preprocessed (tokenization, normalization).
2. **Feature Extraction:** Relevant features are extracted from each log entry.
3. **Clustering:** Logs are grouped into clusters based on similarity.
4. **Anomaly Scoring:** Each log is assigned an anomaly score; high scores indicate potential issues.
5. **API Exposure:** Results are available via `/logs/analysis/anomalies` and `/logs/analysis/clusters` endpoints.

### Benefits
- Reduces manual effort in log analysis
- Surfaces actionable insights for developers and operators
- Enables early detection of regressions and critical incidents
