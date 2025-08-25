# TraceHub Notifier Service

Alerting and notification service that monitors audit logs in real-time and triggers alerts based on configurable rules, supporting webhooks, email, and other notification channels.

## 🎯 Purpose

The notifier service is the **alerting engine** of TraceHub that provides:

- **Real-time monitoring** of audit log patterns
- **Configurable alert rules** with flexible conditions
- **Multi-channel notifications** (webhook, email, Telegram)
- **Alert throttling** and spam prevention
- **Scheduled rule evaluation** and monitoring

## 🏗️ Architecture

```
OpenSearch → Notifier Service → Alert Rules → Notifications
    ↓              ↓              ↓              ↓
Log Data    Rule Evaluation   Condition Check   Webhook/Email
```

### Key Components

- **`AlertService`**: Core alerting logic and rule management
- **`AlertController`**: REST API for rule management
- **Rule Engine**: Configurable alert condition evaluation
- **Notification System**: Multi-channel alert delivery

## 🚀 Quick Start

### Prerequisites

- OpenSearch running with audit log data
- Java 17+
- Maven 3.6+

### Running the Service

```bash
# Build the project
mvn clean package

# Run the service
mvn spring-boot:run

# Or run the JAR directly
java -jar target/tracehub-notifier-0.0.1.jar
```

### Configuration

```properties
# Server Configuration
server.port=8084
server.servlet.context-path=/

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus

# Alert Rules Configuration (planned)
alert.rules.file=config/alert-rules.json
alert.evaluation.interval=30s
alert.cooldown.default=5m
```

## 📡 API Reference

### View Alert Rules

**Endpoint:** `GET /api/v1/alerts/rules`

**Response:**
```json
{
  "high-failure-rate": {
    "name": "high-failure-rate",
    "description": "High failure rate detected",
    "filterField": "status",
    "filterValue": "FAILURE",
    "actionFilter": "action",
    "timeWindowMinutes": 5,
    "threshold": 10,
    "webhookUrl": "http://localhost:8084/webhook/sample",
    "cooldownMinutes": 5
  }
}
```

**Example:**
```bash
curl -X GET http://localhost:8084/api/v1/alerts/rules
```

### Health Check

**Endpoint:** `GET /health`

**Response:** `200 OK` with "OK" message

### Metrics

**Endpoint:** `GET /actuator/prometheus`

**Response:** Prometheus-formatted metrics

## 🚨 Alert Rules

### Rule Structure

Each alert rule consists of:

- **`name`**: Unique identifier for the rule
- **`description`**: Human-readable description
- **`filterField`**: Field to filter on (e.g., "status", "action")
- **`filterValue`**: Value to match (e.g., "FAILURE", "LOGIN")
- **`actionFilter`**: Additional action filter (optional)
- **`timeWindowMinutes`**: Time window for evaluation
- **`threshold`**: Count threshold to trigger alert
- **`webhookUrl`**: Notification endpoint
- **`cooldownMinutes`**: Minimum time between alerts

### Sample Rules

#### High Failure Rate
```json
{
  "name": "high-failure-rate",
  "description": "High failure rate detected",
  "filterField": "status",
  "filterValue": "FAILURE",
  "actionFilter": "action",
  "timeWindowMinutes": 5,
  "threshold": 10,
  "webhookUrl": "https://webhook.site/abc123",
  "cooldownMinutes": 5
}
```

#### Suspicious Login Activity
```json
{
  "name": "suspicious-logins",
  "description": "Multiple failed login attempts",
  "filterField": "action",
  "filterValue": "LOGIN",
  "actionFilter": "status",
  "timeWindowMinutes": 10,
  "threshold": 5,
  "webhookUrl": "https://security-team.slack.com/webhook",
  "cooldownMinutes": 15
}
```

## ⏰ Rule Evaluation

### Scheduling

- **Evaluation Interval**: Every 30 seconds (configurable)
- **Time Windows**: Configurable per rule (minutes)
- **Cooldown Periods**: Prevent alert spam

### Evaluation Process

1. **Data Query**: Query OpenSearch for matching events
2. **Condition Check**: Count events within time window
3. **Threshold Comparison**: Compare count against threshold
4. **Alert Triggering**: Send notification if threshold exceeded
5. **Cooldown Management**: Respect cooldown periods

### Query Examples

#### High Failure Rate Query
```json
{
  "query": {
    "bool": {
      "must": [
        {"term": {"status": "FAILURE"}},
        {"range": {"@timestamp": {"gte": "now-5m"}}}
      ]
    }
  },
  "size": 0,
  "aggs": {
    "failure_count": {"value_count": {"field": "status"}}
  }
}
```

## 📧 Notification Channels

### Webhook Support

- **HTTP POST**: Send alert data to webhook endpoints
- **Custom Headers**: Support for authentication and custom metadata
- **Retry Logic**: Automatic retry for failed deliveries
- **Timeout Handling**: Configurable request timeouts

### Planned Channels

- **Email**: SMTP-based email notifications
- **Slack**: Direct Slack channel integration
- **Telegram**: Bot-based Telegram notifications
- **PagerDuty**: Incident management integration
- **SMS**: Text message notifications

### Notification Payload

```json
{
  "alert_id": "high-failure-rate-20250124T100000Z",
  "rule_name": "high-failure-rate",
  "description": "High failure rate detected",
  "triggered_at": "2025-01-24T10:00:00Z",
  "condition": {
    "filter_field": "status",
    "filter_value": "FAILURE",
    "time_window": "5m",
    "threshold": 10,
    "actual_count": 15
  },
  "tenant_id": "t1",
  "severity": "WARNING"
}
```

## 📊 Monitoring & Metrics

### Health Endpoints

- `/health`: Basic health check
- `/actuator/health`: Detailed health information
- `/actuator/prometheus`: Metrics export

### Key Metrics

- **Alert Firing Rate**: Alerts triggered per minute
- **Rule Evaluation Time**: Time to evaluate all rules
- **Notification Success Rate**: Successful deliveries
- **Webhook Response Time**: Notification delivery latency
- **Active Rules Count**: Number of configured rules

### Logging

- **Level:** DEBUG for development, INFO for production
- **Format:** Structured logging with rule context
- **Output:** Console and file (configurable)

## 🔒 Security Features

### Planned Security

- **Authentication**: JWT token validation
- **Authorization**: Role-based access control
- **Webhook Security**: HMAC signature validation
- **Rate Limiting**: Prevent notification abuse

### Current Implementation

- Basic webhook endpoint security
- Input validation and sanitization
- Secure error handling

## 🚧 Development

### Project Structure

```
src/main/java/com/haiphamcoder/tracehub/notifier/
├── controller/
│   └── AlertController.java            # REST API endpoints
├── service/
│   └── AlertService.java               # Alert logic and rule management
└── TracehubNotifierApplication.java    # Main application class
```

### Adding New Features

1. **New Notification Channels**: Extend notification system
2. **Rule Types**: Add new rule evaluation logic
3. **API Endpoints**: Extend `AlertController`
4. **Tests**: Add unit and integration tests

### Testing

```bash
# Unit tests
mvn test

# Integration tests (requires OpenSearch)
mvn test -Dspring.profiles.active=integration

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## 🔍 Troubleshooting

### Common Issues

1. **Alerts Not Firing**
   - Check rule configuration
   - Verify OpenSearch connectivity
   - Monitor rule evaluation logs
   - Check threshold values

2. **Webhook Delivery Failures**
   - Verify webhook endpoint availability
   - Check network connectivity
   - Review webhook response codes
   - Monitor timeout settings

3. **High Resource Usage**
   - Review evaluation frequency
   - Optimize query performance
   - Monitor memory usage
   - Check rule complexity

### Debug Mode

```properties
logging.level.com.haiphamcoder.tracehub=DEBUG
logging.level.org.springframework.scheduling=DEBUG
```

## 📈 Performance Tuning

### Rule Evaluation Tuning

```properties
# Evaluation frequency
alert.evaluation.interval=30s

# Batch processing
alert.evaluation.batch.size=100
alert.evaluation.concurrent.rules=5

# Query optimization
alert.query.timeout=10s
alert.query.max.results=1000
```

### JVM Tuning

```bash
java -Xms512m -Xmx1g -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar target/tracehub-notifier-0.0.1.jar
```

## 🔄 Rule Management

### Dynamic Rule Updates

**Planned Features:**
- Hot-reloadable rule configuration
- REST API for rule CRUD operations
- Rule versioning and rollback
- Rule testing and validation

**Current Implementation:**
- Static rule configuration in code
- In-memory rule storage
- Basic rule management API

### Rule Validation

- **Syntax Validation**: JSON schema validation
- **Logic Validation**: Rule consistency checks
- **Performance Validation**: Query optimization suggestions
- **Security Validation**: Safe query construction

## 🔗 Dependencies

- **Spring Boot**: Application framework
- **Spring Boot Web**: REST API support
- **Spring Boot Actuator**: Monitoring and metrics
- **Micrometer Prometheus**: Metrics export
- **Scheduled Tasks**: Rule evaluation timing
