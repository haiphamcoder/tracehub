# TraceHub Common

Common library module for TraceHub platform containing shared DTOs, utilities, and constants.

## 📦 Contents

### DTOs (Data Transfer Objects)
- **`LogEvent`**: Core audit log event with validation annotations
- **`SearchRequest`**: Search query parameters for log retrieval
- **`SearchResponse`**: Search results with pagination support

### Utilities
- **`IdempotencyUtil`**: Generates idempotency keys for log events
- **`TracehubConstants`**: System-wide constants and configuration values

### Validation
- Input validation using Jakarta Validation annotations
- Field constraints (required, length, pattern, etc.)
- Custom validation messages

## 🔧 Usage

### Adding to Dependencies

```xml
<dependency>
    <groupId>com.haiphamcoder</groupId>
    <artifactId>tracehub-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Using LogEvent DTO

```java
import com.haiphamcoder.tracehub.common.dto.LogEvent;

LogEvent event = new LogEvent(
    Instant.now(),
    "tenant1",
    "user123",
    "LOGIN",
    "SUCCESS",
    "192.168.1.100",
    "User login successful"
);
event.setMetadata(Map.of("country", "VN", "ua", "Firefox"));
```

### Using IdempotencyUtil

```java
import com.haiphamcoder.tracehub.common.util.IdempotencyUtil;

String idempotencyKey = IdempotencyUtil.generateIdempotencyKey(
    event, 
    "producer-001", 
    System.currentTimeMillis()
);
```

## 📋 Field Constraints

### LogEvent Validation Rules
- `timestamp`: Required, ISO 8601 format
- `tenantId`: Required, 1-50 chars, alphanumeric + hyphen + underscore
- `userId`: Required, 1-100 chars
- `action`: Required, 1-100 chars
- `status`: Required, must be SUCCESS/FAILURE/WARN/INFO/ERROR
- `actorIp`: Required, valid IPv4 or IPv6 format
- `message`: Required, max 10000 chars
- `metadata`: Optional, key-value pairs

### SearchRequest Validation Rules
- `tenantId`: Required
- `from`: Required, ISO 8601 timestamp
- `to`: Required, ISO 8601 timestamp
- `size`: Optional, positive integer, default 100, max 1000

## 🏗️ Architecture

This module is designed as a **library** and should not be run as a standalone application. It provides:

- **Shared contracts** between services
- **Data validation** rules
- **Utility functions** for common operations
- **Constants** for system configuration

## 🔒 Security Considerations

- All DTOs include validation to prevent injection attacks
- Tenant isolation is enforced through validation rules
- Input sanitization is handled at the DTO level

## 📚 API Reference

### LogEvent
```java
public class LogEvent {
    public LogEvent(Instant timestamp, String tenantId, String userId, 
                   String action, String status, String actorIp, String message);
    
    // Getters and setters for all fields
    // toString() method for logging
}
```

### SearchRequest
```java
public class SearchRequest {
    public SearchRequest();
    
    // Getters and setters for all fields
    // Validation annotations
}
```

### IdempotencyUtil
```java
public final class IdempotencyUtil {
    public static String generateIdempotencyKey(LogEvent event, 
                                              String producerId, long seq);
    public static String generateDocumentId(LogEvent event, 
                                          String producerId, long seq);
}
```

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run specific test class
mvn test -Dtest=LogEventTest

# Run with coverage
mvn test jacoco:report
```

## 📝 Contributing

When adding new DTOs or utilities:

1. Follow existing naming conventions
2. Add comprehensive validation annotations
3. Include unit tests
4. Update this README
5. Ensure backward compatibility

## 🔗 Dependencies

- Spring Boot Starter (for validation support)
- Jackson (for JSON processing)
- Jakarta Validation API
