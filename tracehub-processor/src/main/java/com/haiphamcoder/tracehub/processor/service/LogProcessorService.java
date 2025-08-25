package com.haiphamcoder.tracehub.processor.service;

import com.haiphamcoder.tracehub.common.constants.TracehubConstants;
import com.haiphamcoder.tracehub.common.dto.LogEvent;
import com.haiphamcoder.tracehub.common.util.IdempotencyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service for processing log events from Kafka and indexing to OpenSearch
 */
@Service
public class LogProcessorService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogProcessorService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    
    private final OpenSearchService openSearchService;
    private final String processorId;
    
    public LogProcessorService(OpenSearchService openSearchService) {
        this.openSearchService = openSearchService;
        this.processorId = UUID.randomUUID().toString();
        logger.info("LogProcessorService initialized with processorId: {}", processorId);
    }
    
    /**
     * Process log events from Kafka
     * 
     * @param event the log event to process
     * @param key the Kafka message key (tenantId)
     * @param partition the Kafka partition
     * @param offset the Kafka offset
     */
    @KafkaListener(
        topics = TracehubConstants.AUDIT_LOGS_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void processLogEvent(
            @Payload LogEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.debug("Processing log event: tenantId={}, action={}, partition={}, offset={}", 
                   event.getTenantId(), event.getAction(), partition, offset);
        
        try {
            // Generate document ID for idempotency
            String documentId = IdempotencyUtil.generateDocumentId(event, processorId, offset);
            
            // Determine index name based on timestamp
            String indexName = getIndexName(event.getTimestamp());
            
            // TODO: Add PII redaction logic here
            // TODO: Add enrichment logic here
            
            // Index to OpenSearch
            openSearchService.indexLogEvent(indexName, documentId, event);
            
            logger.debug("Successfully processed log event: tenantId={}, documentId={}", 
                       event.getTenantId(), documentId);
            
        } catch (Exception e) {
            logger.error("Failed to process log event: tenantId={}, error={}", 
                        event.getTenantId(), e.getMessage(), e);
            
            // TODO: Send to DLQ topic
            // TODO: Implement retry logic
        }
    }
    
    /**
     * Generate index name based on timestamp
     * Format: logs-tracehub-yyyy.MM.dd
     * 
     * @param timestamp the log event timestamp
     * @return index name
     */
    private String getIndexName(java.time.Instant timestamp) {
        LocalDate date = timestamp.atZone(java.time.ZoneOffset.UTC).toLocalDate();
        return TracehubConstants.INDEX_PREFIX + "-" + date.format(DATE_FORMATTER);
    }
    
    /**
     * Get processor ID for this service instance
     * 
     * @return processor ID
     */
    public String getProcessorId() {
        return processorId;
    }
}
