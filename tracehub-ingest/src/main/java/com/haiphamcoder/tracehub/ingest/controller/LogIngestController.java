package com.haiphamcoder.tracehub.ingest.controller;

import com.haiphamcoder.tracehub.common.dto.LogEvent;
import com.haiphamcoder.tracehub.ingest.service.LogIngestService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * REST controller for log ingestion
 */
@RestController
@RequestMapping("/api/v1")
@Timed(value = "log.ingest", description = "Log ingestion metrics")
public class LogIngestController {
    
    private static final Logger logger = LoggerFactory.getLogger(LogIngestController.class);
    
    private final LogIngestService logIngestService;
    
    public LogIngestController(LogIngestService logIngestService) {
        this.logIngestService = logIngestService;
    }
    
    /**
     * Ingest a single log event
     * 
     * @param event the log event to ingest
     * @return 202 Accepted response
     */
    @PostMapping("/logs")
    public ResponseEntity<String> ingestLog(@Valid @RequestBody LogEvent event) {
        logger.info("Received log event: tenantId={}, action={}, userId={}", 
                   event.getTenantId(), event.getAction(), event.getUserId());
        
        // TODO: Extract tenantId from JWT token or API key for authorization
        // TODO: Add rate limiting per tenant
        
        try {
            CompletableFuture<Void> future = logIngestService.ingestLog(event)
                    .thenAccept(result -> logger.debug("Log event sent to Kafka successfully"))
                    .exceptionally(throwable -> {
                        logger.error("Failed to send log event to Kafka", throwable);
                        return null;
                    });
            
            // Return 202 Accepted immediately (async processing)
            return ResponseEntity.accepted()
                    .body("Log event accepted for processing");
                    
        } catch (Exception e) {
            logger.error("Error processing log event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }
    
    /**
     * Health check endpoint
     * 
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
