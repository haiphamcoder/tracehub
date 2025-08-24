package com.haiphamcoder.tracehub.common.constants;

/**
 * Constants for TraceHub system
 */
public final class TracehubConstants {
    
    private TracehubConstants() {
        // Utility class
    }
    
    // Kafka Topics
    public static final String AUDIT_LOGS_TOPIC = "audit-logs";
    public static final String AUDIT_LOGS_DLQ_TOPIC = "audit-logs-dlq";
    
    // OpenSearch Index
    public static final String INDEX_PREFIX = "logs-tracehub";
    public static final String INDEX_ALIAS = "logs-tracehub-*";
    public static final String INDEX_PATTERN = "logs-tracehub-yyyy.MM.dd";
    
    // API Endpoints
    public static final String API_V1_BASE = "/api/v1";
    public static final String LOGS_ENDPOINT = API_V1_BASE + "/logs";
    public static final String SEARCH_ENDPOINT = API_V1_BASE + "/search";
    public static final String AGGREGATIONS_ENDPOINT = API_V1_BASE + "/aggs";
    
    // Security
    public static final String JWT_BEARER_PREFIX = "Bearer ";
    public static final String API_KEY_HEADER = "X-API-Key";
    
    // Status Values
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";
    public static final String STATUS_WARN = "WARN";
    public static final String STATUS_INFO = "INFO";
    public static final String STATUS_ERROR = "ERROR";
    
    // Default Values
    public static final int DEFAULT_SEARCH_SIZE = 100;
    public static final int MAX_SEARCH_SIZE = 1000;
    public static final int MAX_MESSAGE_LENGTH = 10000;
    
    // Index Settings
    public static final int DEFAULT_SHARDS = 3;
    public static final int DEFAULT_REPLICAS = 1;
    public static final String DEFAULT_REFRESH_INTERVAL = "1s";
}
