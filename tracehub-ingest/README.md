# TraceHub Ingest Service

High-performance log ingestion service that receives audit logs via HTTP REST API and forwards them to Kafka for asynchronous processing.

## 🎯 Purpose

The ingest service acts as the **entry point** for all audit logs in the TraceHub platform. It provides:

- **Fast ingestion** with 202 Accepted responses
- **Input validation** and sanitization
- **Kafka integration** for reliable message delivery
- **Metrics and monitoring** for operational visibility
- **Rate limiting** (planned feature)

## 🏗️ Architecture

```
HTTP Client → Ingest Service → Kafka Producer → audit-logs Topic
                ↓
            Validation, Metrics, Logging
```

### Key Components

- **`LogIngestController`**: REST API endpoints
- **`LogIngestService`**: Business logic and Kafka integration
- **Kafka Producer**: Async message publishing
- **Actuator**: Health checks and metrics

## 🚀 Quick Start

### Prerequisites

- Kafka running on `localhost:9092`
- Java 17+
- Maven 3.6+

### Running the Service

```bash
# Build the project
mvn clean package

# Run the service
mvn spring-boot:run

# Or run the JAR directly
java -jar target/tracehub-ingest-0.0.1.jar
```

### Configuration

```properties
# Server Configuration
server.port=8081
server.servlet.context-path=/

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

## 📡 API Reference

### Ingest Logs

**Endpoint:** `POST /api/v1/logs`

**Request Body:**
```json
{
  "timestamp": "2025-01-24T10:00:00Z",
  "tenantId": "t1",
  "userId": "u123",
  "action": "LOGIN",
  "status": "SUCCESS",
  "actorIp": "192.168.1.100",
  "message": "User login successful",
  "metadata": {
    "country": "VN",
    "ua": "Firefox"
  }
}
```

**Response:**
- `202 Accepted`: Log event accepted for processing
- `400 Bad Request`: Validation errors
- `500 Internal Server Error`: Server errors

**Example:**
```bash
curl -X POST http://localhost:8081/api/v1/logs \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": "2025-01-24T10:00:00Z",
    "tenantId": "t1",
    "userId": "u123",
    "action": "LOGIN",
    "status": "SUCCESS",
    "actorIp": "192.168.1.100",
    "message": "User login successful"
  }'
```

### Health Check

**Endpoint:** `GET /health`

**Response:** `200 OK` with "OK" message

### Metrics

**Endpoint:** `GET /actuator/prometheus`

**Response:** Prometheus-formatted metrics

## 🔧 Kafka Integration

### Topic Configuration

- **Topic Name:** `audit-logs`
- **Partitioning:** By `tenantId` for tenant isolation
- **Serialization:** JSON with type mapping
- **Producer Config:** Async with callback handling

### Message Flow

1. **Validation**: Input validation using DTO annotations
2. **Processing**: Generate idempotency key
3. **Publishing**: Send to Kafka with tenantId as key
4. **Response**: Return 202 Accepted immediately
5. **Monitoring**: Log success/failure metrics

### Idempotency

Each log event gets a unique idempotency key:
```
hash(tenantId|timestamp|userId|action|producerId|seq)
```

## 📊 Monitoring & Metrics

### Health Endpoints

- `/health`: Basic health check
- `/actuator/health`: Detailed health information
- `/actuator/info`: Service information

### Prometheus Metrics

- **`log.ingest`**: Request timing and counts
- **Kafka producer metrics**: Latency, throughput, errors
- **HTTP metrics**: Request counts, response codes, latency

### Logging

- **Level:** DEBUG for development, INFO for production
- **Format:** Structured logging with tenant and action context
- **Output:** Console and file (configurable)

## 🔒 Security Features

### Planned Features

- **JWT Authentication**: Bearer token validation
- **HMAC API Keys**: Alternative authentication method
- **Rate Limiting**: Per-tenant request throttling
- **Input Sanitization**: XSS and injection prevention

### Current Implementation

- Input validation using Jakarta Validation
- Tenant isolation through DTO constraints
- Secure error handling (no sensitive data exposure)

## 🚧 Development

### Project Structure

```
src/main/java/com/haiphamcoder/tracehub/ingest/
├── controller/
│   └── LogIngestController.java      # REST API endpoints
├── service/
│   └── LogIngestService.java         # Business logic
└── TracehubIngestApplication.java    # Main application class
```

### Adding New Features

1. **New Endpoints**: Add to `LogIngestController`
2. **Business Logic**: Implement in `LogIngestService`
3. **Configuration**: Update `application.properties`
4. **Tests**: Add unit and integration tests

### Testing

```bash
# Unit tests
mvn test

# Integration tests (requires Kafka)
mvn test -Dspring.profiles.active=integration

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## 🔍 Troubleshooting

### Common Issues

1. **Kafka Connection Failed**
   - Check Kafka is running: `docker-compose ps`
   - Verify bootstrap servers configuration
   - Check network connectivity

2. **Validation Errors**
   - Review request payload format
   - Check required fields and constraints
   - Verify timestamp format (ISO 8601)

3. **High Latency**
   - Monitor Kafka producer metrics
   - Check network latency to Kafka
   - Review producer configuration

### Debug Mode

```properties
logging.level.com.haiphamcoder.tracehub=DEBUG
logging.level.org.springframework.kafka=DEBUG
```

## 📈 Performance Tuning

### Kafka Producer Tuning

```properties
# Batch size and timing
spring.kafka.producer.batch-size=16384
spring.kafka.producer.buffer-memory=33554432
spring.kafka.producer.linger.ms=5

# Compression
spring.kafka.producer.compression-type=gzip
```

### JVM Tuning

```bash
java -Xms512m -Xmx2g -XX:+UseG1GC -jar target/tracehub-ingest-0.0.1.jar
```

## 🔗 Dependencies

- **Spring Boot Web**: REST API framework
- **Spring Kafka**: Kafka integration
- **Spring Boot Actuator**: Monitoring and metrics
- **Micrometer Prometheus**: Metrics export
- **Jakarta Validation**: Input validation
- **Jackson**: JSON processing
