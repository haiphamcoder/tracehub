# TraceHub Query Service

High-performance search and analytics service that provides fast access to audit logs stored in OpenSearch with advanced filtering, pagination, and aggregation capabilities.

## 🎯 Purpose

The query service is the **search interface** of TraceHub that enables:

- **Fast log retrieval** with complex filtering
- **Full-text search** across log messages
- **Efficient pagination** using search_after
- **Real-time aggregations** and analytics
- **Multi-tenant isolation** and security

## 🏗️ Architecture

```
HTTP Client → Query Service → OpenSearch → Results
    ↓              ↓              ↓
Search Request  Query Builder   Index Query
                & Validation    & Aggregation
```

### Key Components

- **`SearchController`**: REST API endpoints for search operations
- **`SearchService`**: Business logic and OpenSearch integration
- **Query Builder**: Dynamic query construction from search parameters
- **Result Processor**: Pagination and response formatting

## 🚀 Quick Start

### Prerequisites

- OpenSearch running on `localhost:9200`
- Audit logs indexed in OpenSearch
- Java 17+
- Maven 3.6+

### Running the Service

```bash
# Build the project
mvn clean package

# Run the service
mvn spring-boot:run

# Or run the JAR directly
java -jar target/tracehub-query-0.0.1.jar
```

### Configuration

```properties
# Server Configuration
server.port=8083
server.servlet.context-path=/

# OpenSearch Configuration
opensearch.host=localhost
opensearch.port=9200
opensearch.scheme=http
opensearch.username=admin
opensearch.password=admin

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

## 📡 API Reference

### Search Logs

**Endpoint:** `POST /api/v1/search`

**Request Body:**
```json
{
  "tenantId": "t1",
  "from": "2025-01-24T09:00:00Z",
  "to": "2025-01-24T11:00:00Z",
  "action": "LOGIN",
  "status": "FAILURE",
  "userId": "u123",
  "actorIp": "192.168.1.100",
  "q": "invalid password",
  "size": 100,
  "searchAfter": "base64-encoded-sort-values"
}
```

**Response:**
```json
{
  "hits": [
    {
      "timestamp": "2025-01-24T10:00:00Z",
      "tenantId": "t1",
      "userId": "u123",
      "action": "LOGIN",
      "status": "FAILURE",
      "actorIp": "192.168.1.100",
      "message": "Invalid password for user u123",
      "metadata": {"country": "VN", "ua": "Firefox"}
    }
  ],
  "total": 150,
  "nextPageToken": "base64-encoded-next-page-token",
  "hasMore": true
}
```

**Example:**
```bash
curl -X POST http://localhost:8083/api/v1/search \
  -H "Content-Type: application/json" \
  -d '{
    "tenantId": "t1",
    "from": "2025-01-24T09:00:00Z",
    "to": "2025-01-24T11:00:00Z",
    "action": "LOGIN",
    "status": "FAILURE",
    "size": 100
  }'
```

### Health Check

**Endpoint:** `GET /health`

**Response:** `200 OK` with "OK" message

### Metrics

**Endpoint:** `GET /actuator/prometheus`

**Response:** Prometheus-formatted metrics

## 🔍 Search Features

### Filtering Options

- **Time Range:** `from` and `to` timestamps
- **Tenant Isolation:** `tenantId` (required)
- **User Actions:** `action` field filtering
- **Status Filtering:** `status` field filtering
- **User Context:** `userId` filtering
- **Network Context:** `actorIp` filtering
- **Full-text Search:** `q` parameter for message content

### Pagination

- **Size Control:** Configurable result set size (max 1000)
- **Search After:** Deep pagination using `searchAfter` token
- **Cursor-based:** Efficient for large result sets
- **Consistent Results:** Point-in-time consistency

### Sorting

- **Primary Sort:** `@timestamp` ascending
- **Secondary Sort:** `_id` ascending for consistency
- **Optimized:** Index-level sorting for performance

## 📊 Query Performance

### Index Optimization

- **Daily Indices:** `logs-tracehub-yyyy.MM.dd`
- **Index Aliases:** `logs-tracehub-*` for cross-index queries
- **Shard Strategy:** 3 shards, 1 replica for development
- **Sort Fields:** Optimized for timestamp-based queries

### Query Optimization

- **Filter Pushdown:** Early filtering for performance
- **Aggregation Caching:** Result caching for repeated queries
- **Connection Pooling:** Efficient OpenSearch client usage
- **Batch Processing:** Bulk operations for multiple queries

## 🔒 Security & Access Control

### Tenant Isolation

- **Mandatory Filtering:** `tenantId` required in all queries
- **Cross-tenant Prevention:** No cross-tenant data access
- **Token Validation:** Tenant ID extracted from authentication token

### Planned Security Features

- **JWT Authentication:** Bearer token validation
- **API Key Support:** HMAC-based authentication
- **Rate Limiting:** Per-tenant query throttling
- **Audit Logging:** Query access tracking

## 📈 Performance Tuning

### OpenSearch Client Tuning

```properties
# Connection settings
opensearch.connection.timeout=10s
opensearch.socket.timeout=30s
opensearch.connection.pool.size=20

# Query settings
opensearch.query.timeout=30s
opensearch.query.max.result.window=10000
```

### JVM Tuning

```bash
java -Xms1g -Xmx2g -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar target/tracehub-query-0.0.1.jar
```

### Query Optimization Tips

1. **Use Specific Time Ranges:** Narrow time windows for better performance
2. **Limit Result Size:** Use appropriate `size` parameters
3. **Leverage Indexing:** Ensure proper field indexing
4. **Monitor Query Performance:** Use OpenSearch slow query logs

## 📊 Monitoring & Metrics

### Health Endpoints

- `/health`: Basic health check
- `/actuator/health`: Detailed health information
- `/actuator/prometheus`: Metrics export

### Key Metrics

- **Query Latency:** P50, P95, P99 response times
- **Query Throughput:** Queries per second
- **Error Rate:** Failed query attempts
- **Cache Hit Rate:** Aggregation cache efficiency
- **OpenSearch Performance:** Index query performance

### Logging

- **Level:** DEBUG for development, INFO for production
- **Format:** Structured logging with query context
- **Output:** Console and file (configurable)

## 🚧 Development

### Project Structure

```
src/main/java/com/haiphamcoder/tracehub/query/
├── controller/
│   └── SearchController.java           # REST API endpoints
├── service/
│   └── SearchService.java              # Search logic and OpenSearch integration
└── TracehubQueryApplication.java       # Main application class
```

### Adding New Features

1. **New Endpoints**: Add to `SearchController`
2. **Search Logic**: Extend `SearchService`
3. **Query Builders**: Implement new query types
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

1. **Slow Query Performance**
   - Check OpenSearch cluster health
   - Review query complexity and filters
   - Monitor index performance
   - Verify proper indexing

2. **OpenSearch Connection Issues**
   - Verify OpenSearch is running
   - Check network connectivity
   - Review authentication credentials
   - Monitor connection pool status

3. **Memory Issues**
   - Monitor JVM heap usage
   - Check OpenSearch memory allocation
   - Review query result sizes
   - Optimize pagination parameters

### Debug Mode

```properties
logging.level.com.haiphamcoder.tracehub=DEBUG
logging.level.org.opensearch=DEBUG
```

## 📈 Scaling Strategies

### Horizontal Scaling

- **Multiple Instances:** Run multiple query services
- **Load Balancing:** Distribute queries across instances
- **Stateless Design:** Easy horizontal scaling

### Vertical Scaling

- **Memory:** Increase JVM heap size
- **CPU:** Allocate more CPU cores
- **Network:** Optimize network configuration

### OpenSearch Scaling

- **Index Sharding:** Increase shard count for large datasets
- **Replica Scaling:** Add replicas for read performance
- **Cluster Scaling:** Add data nodes for capacity

## 🔗 Dependencies

- **Spring Boot Web**: REST API framework
- **OpenSearch Java Client**: OpenSearch operations
- **Spring Boot Actuator**: Monitoring and metrics
- **Micrometer Prometheus**: Metrics export
- **Jakarta Validation**: Input validation
