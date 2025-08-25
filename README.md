# TraceHub - Central Audit Logging Platform

TraceHub is a central audit logging platform that can ingest realtime, perform fast searches, and provide basic analytics. The project is built with Spring Boot 3.5.5, Java 17, and Maven multi-module architecture.

## 🎯 Objectives

- **Realtime Ingest**: HTTP POST or Kafka with low latency
- **Fast Search**: Filter, aggregation and full-text search on messages
- **Lifecycle Storage**: Hot → Warm → Archive with horizontal scalability
- **Basic Alerting**: Threshold/condition-based with webhook/Telegram/Email support

## 🏗️ System Architecture

```
Client → Ingest Service → Kafka → Processor Service → OpenSearch → Query Service
                                    ↓
                              Notifier Service (Alerting)
```

### Modules

1. **tracehub-common**: DTOs, utilities, constants
2. **tracehub-ingest**: REST API for receiving logs, sending to Kafka
3. **tracehub-processor**: Kafka consumer, processing and indexing to OpenSearch
4. **tracehub-query**: Search API with filtering and pagination
5. **tracehub-notifier**: Basic alerting with scheduled rules

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose

### 1. Start Infrastructure

```bash
# Start Kafka and OpenSearch
docker-compose up -d

# Wait for services to fully start (about 2-3 minutes)
docker-compose ps
```

### 2. Build and Run Services

```bash
# Build entire project
mvn clean package -DskipTests

# Run each service (in separate terminals)
cd tracehub-ingest && mvn spring-boot:run
cd tracehub-processor && mvn spring-boot:run  
cd tracehub-query && mvn spring-boot:run
cd tracehub-notifier && mvn spring-boot:run
```

### 3. Verify Services

- **Ingest Service**: http://localhost:8081/health
- **Processor Service**: http://localhost:8082/health
- **Query Service**: http://localhost:8083/health
- **Notifier Service**: http://localhost:8084/health
- **OpenSearch**: http://localhost:9200
- **OpenSearch Dashboards**: http://localhost:5601

## 📡 API Usage

### Ingest Logs

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
    "message": "User login successful",
    "metadata": {"country": "VN", "ua": "Firefox"}
  }'
```

### Search Logs

```bash
curl -X POST http://localhost:8083/api/v1/search \
  -H "Content-Type: application/json" \
  -d '{
    "tenantId": "t1",
    "from": "2025-01-24T09:00:00Z",
    "to": "2025-01-24T11:00:00Z",
    "action": "LOGIN",
    "size": 100
  }'
```

### View Alert Rules

```bash
curl http://localhost:8084/api/v1/alerts/rules
```

## 🔧 Configuration

### Kafka Topics

- `audit-logs`: Main topic for log events
- `audit-logs-dlq`: Dead letter queue for failed events

### OpenSearch Indices

- Pattern: `logs-tracehub-yyyy.MM.dd`
- Alias: `logs-tracehub-*`
- Shards: 3, Replicas: 1

### Ports

- Ingest: 8081
- Processor: 8082  
- Query: 8083
- Notifier: 8084
- Kafka: 9092
- OpenSearch: 9200
- OpenSearch Dashboards: 5601

## 📊 Monitoring

### Metrics

- **Ingest**: RPS, produce latency, 4xx/5xx errors
- **Processor**: Consumer lag, bulk size, index latency
- **Query**: Query latency p50/95/99, timeout rate
- **Notifier**: Alert firing rate, webhook success rate

### Health Checks

All services have `/health` endpoints and Prometheus metrics at `/actuator/prometheus`.

## 🚧 TODO & Limitations

### Current Phase (Stub Implementations)

- [x] Basic DTOs and validation
- [x] Kafka producer/consumer setup
- [x] OpenSearch client configuration (stub)
- [x] REST API endpoints
- [x] Basic alerting framework

### To Implement

- [ ] Authentication & Authorization (JWT/HMAC)
- [ ] PII redaction logic
- [ ] OpenSearch search implementation
- [ ] Webhook delivery
- [ ] Rate limiting
- [ ] Proper error handling and retry logic

### Future Phases

- [ ] UI frontend
- [ ] Advanced correlation rules
- [ ] SIEM features
- [ ] Archive to S3/MinIO
- [ ] Schema Registry (Avro)

## 🧪 Testing

```bash
# Unit tests
mvn test

# Integration tests (requires Kafka and OpenSearch)
mvn test -Dspring.profiles.active=integration

# Smoke test
# Send 1k logs → query back within 1-5s
```

## 📝 Development

### Project Structure

```
tracehub/
├── tracehub-common/          # Shared DTOs, utilities
├── tracehub-ingest/          # Log ingestion service
├── tracehub-processor/       # Log processing service  
├── tracehub-query/           # Search service
├── tracehub-notifier/        # Alerting service
├── docker-compose.yml        # Infrastructure
└── README.md
```

### Adding New Features

1. Update DTOs in `tracehub-common` if needed
2. Implement business logic in respective service
3. Add tests
4. Update documentation

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Add tests
5. Submit pull request

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details.

## 🆘 Troubleshooting

### Common Issues

1. **Kafka connection refused**: Ensure Zookeeper has started
2. **OpenSearch connection refused**: Wait for OpenSearch to fully start
3. **Port conflicts**: Check ports are not used by other services

### Logs

```bash
# View service logs
docker-compose logs -f kafka
docker-compose logs -f opensearch

# View application logs
tail -f tracehub-ingest/logs/application.log
```
