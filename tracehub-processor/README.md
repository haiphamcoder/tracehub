# TraceHub Processor Service

Log processing service that consumes audit log events from Kafka, processes them, and indexes them into OpenSearch for search and analytics.

## 🎯 Purpose

The processor service is the **core processing engine** of TraceHub that:

- **Consumes** log events from Kafka topics
- **Processes** and enriches log data
- **Indexes** events into OpenSearch with proper mapping
- **Ensures** idempotency and data consistency
- **Provides** monitoring and metrics

## 🏗️ Architecture

```
Kafka Topic → Processor Service → OpenSearch
    ↓              ↓              ↓
audit-logs    Validation &    Index Creation
              Enrichment      & Mapping
```

### Key Components

- **`LogProcessorService`**: Kafka consumer and event processing
- **`OpenSearchService`**: OpenSearch integration and indexing
- **`OpenSearchConfig`**: Client configuration and connection management
- **Kafka Consumer**: Batch processing with error handling

## 🚀 Quick Start

### Prerequisites

- Kafka running with `audit-logs` topic
- OpenSearch running on `localhost:9200`
- Java 17+
- Maven 3.6+

### Running the Service

```bash
# Build the project
mvn clean package

# Run the service
mvn spring-boot:run

# Or run the JAR directly
java -jar target/tracehub-processor-0.0.1.jar
```

### Configuration

```properties
# Server Configuration
server.port=8082

# Kafka Consumer Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=tracehub-processor-group
spring.kafka.consumer.auto-offset-reset=earliest

# OpenSearch Configuration
opensearch.host=localhost
opensearch.port=9200
opensearch.scheme=http
opensearch.username=admin
opensearch.password=admin
```

## 🔧 Kafka Integration

### Consumer Configuration

- **Group ID:** `tracehub-processor-group`
- **Topic:** `audit-logs`
- **Auto Offset Reset:** `earliest` (process all messages)
- **Deserializer:** JSON with type mapping

### Message Processing Flow

1. **Consume**: Read messages from Kafka topic
2. **Validate**: Check message format and content
3. **Process**: Generate document ID and index name
4. **Index**: Store in OpenSearch with proper mapping
5. **Monitor**: Track success/failure metrics

### Error Handling

- **DLQ Topic:** `audit-logs-dlq` for failed messages
- **Retry Logic:** Configurable retry attempts
- **Dead Letter Queue:** Manual review of failed events

## 🔍 OpenSearch Integration

### Index Management

- **Index Pattern:** `logs-tracehub-yyyy.MM.dd`
- **Daily Rollover:** New index for each day
- **Alias:** `logs-tracehub-*` for querying

### Index Settings

```json
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "index.sort.field": "@timestamp",
    "index.sort.order": "asc"
  }
}
```

### Mapping Schema

```json
{
  "mappings": {
    "properties": {
      "@timestamp": { "type": "date" },
      "tenantId": { "type": "keyword" },
      "userId": { "type": "keyword" },
      "action": { "type": "keyword" },
      "status": { "type": "keyword" },
      "actorIp": { "type": "ip" },
      "message": { "type": "text" },
      "metadata": { "type": "flattened" }
    }
  }
}
```

## 📊 Data Processing

### Event Enrichment

**Planned Features:**
- PII redaction (email, phone, tokens)
- Geographic enrichment (IP to country/city)
- User context enrichment
- Threat intelligence correlation

**Current Implementation:**
- Basic validation and sanitization
- Idempotency key generation
- Timestamp normalization

### Idempotency

Each log event gets a unique document ID:
```
hash(tenantId|timestamp|userId|action|producerId|seq)
```

This ensures:
- **No duplicates** in OpenSearch
- **Replay safety** for Kafka consumers
- **Data consistency** across restarts

## 📈 Performance & Scaling

### Consumer Tuning

```properties
# Batch processing
spring.kafka.consumer.max.poll.records=500
spring.kafka.consumer.fetch.min.bytes=1
spring.kafka.consumer.fetch.max.wait.ms=500

# Threading
spring.kafka.listener.concurrency=3
spring.kafka.listener.poll-timeout=3000
```

### OpenSearch Tuning

```properties
# Bulk operations
opensearch.bulk.size=1000
opensearch.bulk.timeout=30s
opensearch.bulk.concurrent.requests=5

# Connection pooling
opensearch.connection.pool.size=20
opensearch.connection.timeout=10s
```

## 📊 Monitoring & Metrics

### Health Endpoints

- `/health`: Basic health check
- `/actuator/health`: Detailed health information
- `/actuator/prometheus`: Metrics export

### Key Metrics

- **Consumer Lag:** Messages behind real-time
- **Processing Rate:** Events per second
- **Index Latency:** OpenSearch write performance
- **Error Rate:** Failed processing attempts
- **Bulk Size:** Batch processing efficiency

### Logging

- **Level:** DEBUG for development, INFO for production
- **Format:** Structured logging with event context
- **Output:** Console and file (configurable)

## 🔒 Security Features

### Planned Features

- **OpenSearch Security:** Authentication and authorization
- **Data Encryption:** At-rest and in-transit encryption
- **Audit Logging:** Access and modification tracking
- **Network Security:** TLS and firewall rules

### Current Implementation

- Basic authentication support (username/password)
- Network isolation through Docker
- Secure error handling

## 🚧 Development

### Project Structure

```
src/main/java/com/haiphamcoder/tracehub/processor/
├── config/
│   └── OpenSearchConfig.java          # OpenSearch client configuration
├── service/
│   ├── LogProcessorService.java       # Kafka consumer and processing
│   └── OpenSearchService.java         # OpenSearch operations
└── TracehubProcessorApplication.java  # Main application class
```

### Adding New Features

1. **New Processors**: Add to `LogProcessorService`
2. **Index Operations**: Extend `OpenSearchService`
3. **Configuration**: Update `OpenSearchConfig`
4. **Tests**: Add unit and integration tests

### Testing

```bash
# Unit tests
mvn test

# Integration tests (requires Kafka and OpenSearch)
mvn test -Dspring.profiles.active=integration

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## 🔍 Troubleshooting

### Common Issues

1. **Kafka Consumer Lag**
   - Check consumer group status
   - Monitor processing performance
   - Scale consumer instances

2. **OpenSearch Connection Issues**
   - Verify OpenSearch is running
   - Check network connectivity
   - Review authentication credentials

3. **Index Creation Failures**
   - Check OpenSearch cluster health
   - Verify index naming conventions
   - Review mapping schema

### Debug Mode

```properties
logging.level.com.haiphamcoder.tracehub=DEBUG
logging.level.org.springframework.kafka=DEBUG
logging.level.org.opensearch=DEBUG
```

## 📈 Scaling Strategies

### Horizontal Scaling

- **Multiple Instances:** Run multiple processor services
- **Partition Strategy:** Ensure proper Kafka partitioning
- **Load Balancing:** Distribute load across instances

### Vertical Scaling

- **Memory:** Increase JVM heap size
- **CPU:** Allocate more CPU cores
- **Network:** Optimize network configuration

## 🔗 Dependencies

- **Spring Boot**: Application framework
- **Spring Kafka**: Kafka integration
- **OpenSearch Java Client**: OpenSearch operations
- **Spring Boot Actuator**: Monitoring and metrics
- **Micrometer Prometheus**: Metrics export
