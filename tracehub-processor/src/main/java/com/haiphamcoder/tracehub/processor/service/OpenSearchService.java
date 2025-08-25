package com.haiphamcoder.tracehub.processor.service;

import com.haiphamcoder.tracehub.common.constants.TracehubConstants;
import com.haiphamcoder.tracehub.common.dto.LogEvent;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

/**
 * Service for OpenSearch operations
 */
@Service
public class OpenSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchService.class);
    
    private final OpenSearchClient openSearchClient;
    
    @Value("${opensearch.host:localhost}")
    private String host;
    
    @Value("${opensearch.port:9200}")
    private int port;
    
    public OpenSearchService(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }
    
    /**
     * Index a log event to OpenSearch
     * 
     * @param indexName the index name
     * @param documentId the document ID
     * @param event the log event
     */
    public void indexLogEvent(String indexName, String documentId, LogEvent event) {
        // TODO: Implement actual OpenSearch indexing when client is available
        if (openSearchClient == null) {
            logger.warn("OpenSearch client not available - skipping indexing: index={}, documentId={}", 
                       indexName, documentId);
            return;
        }
        
        try {
            // Ensure index exists
            ensureIndexExists(indexName);
            
            // Create index request
            IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index(indexName)
                .id(documentId)
                .opType(org.opensearch.client.opensearch._types.OpType.Create) // Use create to ensure idempotency
                .document(convertToMap(event))
            );
            
            // Execute index request
            var response = openSearchClient.index(request);
            
            logger.debug("Indexed log event: index={}, documentId={}, result={}", 
                       indexName, documentId, response.result());
            
        } catch (IOException e) {
            logger.error("Failed to index log event: index={}, documentId={}, error={}", 
                        indexName, documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to index log event", e);
        }
    }
    
    /**
     * Ensure index exists with proper mapping
     * 
     * @param indexName the index name
     */
    private void ensureIndexExists(String indexName) throws IOException {
        if (openSearchClient == null) {
            logger.warn("OpenSearch client not available - skipping index creation: {}", indexName);
            return;
        }
        
        boolean exists = openSearchClient.indices().exists(ExistsRequest.of(e -> e.index(indexName))).value();
        
        if (!exists) {
            logger.info("Creating index: {}", indexName);
            createIndex(indexName);
        }
    }
    
    /**
     * Create index with proper mapping
     * 
     * @param indexName the index name
     */
    private void createIndex(String indexName) throws IOException {
        if (openSearchClient == null) {
            logger.warn("OpenSearch client not available - skipping index creation: {}", indexName);
            return;
        }
        
        CreateIndexRequest request = CreateIndexRequest.of(i -> i
            .index(indexName)
            .settings(s -> s
                .numberOfShards(String.valueOf(TracehubConstants.DEFAULT_SHARDS))
                .numberOfReplicas(String.valueOf(TracehubConstants.DEFAULT_REPLICAS))
            )
            .mappings(m -> m
                .properties("@timestamp", p -> p.date(d -> d))
                .properties("tenantId", p -> p.keyword(k -> k))
                .properties("userId", p -> p.keyword(k -> k))
                .properties("action", p -> p.keyword(k -> k))
                .properties("status", p -> p.keyword(k -> k))
                .properties("actorIp", p -> p.ip(ip -> ip))
                .properties("message", p -> p.text(t -> t))
                .properties("metadata", p -> p.flattened(f -> f))
            )
        );
        
        openSearchClient.indices().create(request);
        logger.info("Successfully created index: {}", indexName);
    }
    
    /**
     * Convert LogEvent to Map for OpenSearch indexing
     * 
     * @param event the log event
     * @return map representation
     */
    private Map<String, Object> convertToMap(LogEvent event) {
        return Map.of(
            "@timestamp", event.getTimestamp().toString(),
            "tenantId", event.getTenantId(),
            "userId", event.getUserId(),
            "action", event.getAction(),
            "status", event.getStatus(),
            "actorIp", event.getActorIp(),
            "message", event.getMessage(),
            "metadata", event.getMetadata() != null ? event.getMetadata() : Map.of()
        );
    }
}
