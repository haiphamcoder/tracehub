package com.haiphamcoder.tracehub.ingest.service;

import com.haiphamcoder.tracehub.common.constants.TracehubConstants;
import com.haiphamcoder.tracehub.common.dto.LogEvent;
import com.haiphamcoder.tracehub.common.util.IdempotencyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for ingesting log events into Kafka
 */
@Service
public class LogIngestService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogIngestService.class);
    
    private final KafkaTemplate<String, LogEvent> kafkaTemplate;
    private final String producerId;
    
    public LogIngestService(KafkaTemplate<String, LogEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.producerId = UUID.randomUUID().toString();
        logger.info("LogIngestService initialized with producerId: {}", producerId);
    }
    
    /**
     * Send log event to Kafka
     * 
     * @param event the log event to send
     * @return CompletableFuture with send result
     */
    public CompletableFuture<SendResult<String, LogEvent>> ingestLog(LogEvent event) {
        String key = event.getTenantId(); // Use tenantId as Kafka key for partitioning
        String idempotencyKey = IdempotencyUtil.generateIdempotencyKey(event, producerId, System.currentTimeMillis());
        
        logger.debug("Sending log event to Kafka: tenantId={}, action={}, idempotencyKey={}", 
                   event.getTenantId(), event.getAction(), idempotencyKey);
        
        // TODO: Add headers for idempotency and tracing
        return kafkaTemplate.send(TracehubConstants.AUDIT_LOGS_TOPIC, key, event)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        logger.error("Failed to send log event to Kafka: tenantId={}, error={}", 
                                   event.getTenantId(), throwable.getMessage(), throwable);
                    } else {
                        logger.debug("Successfully sent log event to Kafka: tenantId={}, partition={}, offset={}", 
                                   event.getTenantId(), result.getRecordMetadata().partition(), 
                                   result.getRecordMetadata().offset());
                    }
                });
    }
    
    /**
     * Get producer ID for this service instance
     * 
     * @return producer ID
     */
    public String getProducerId() {
        return producerId;
    }
}
