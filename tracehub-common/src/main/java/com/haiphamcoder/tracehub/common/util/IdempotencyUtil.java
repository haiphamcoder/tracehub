package com.haiphamcoder.tracehub.common.util;

import com.haiphamcoder.tracehub.common.dto.LogEvent;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility for generating idempotency keys
 */
public final class IdempotencyUtil {
    
    private IdempotencyUtil() {
        // Utility class
    }
    
    /**
     * Generate idempotency key for a log event
     * Format: hash(tenantId|timestamp|userId|action|producerId|seq)
     * 
     * @param event the log event
     * @param producerId unique identifier for the producer
     * @param seq sequence number within the producer
     * @return base64 encoded idempotency key
     */
    public static String generateIdempotencyKey(LogEvent event, String producerId, long seq) {
        String input = String.join("|", 
            event.getTenantId(),
            event.getTimestamp().toString(),
            event.getUserId(),
            event.getAction(),
            producerId,
            String.valueOf(seq)
        );
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash if SHA-256 is not available
            return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    /**
     * Generate document ID for OpenSearch
     * This ensures idempotency when using op_type=create
     * 
     * @param event the log event
     * @param producerId unique identifier for the producer
     * @param seq sequence number within the producer
     * @return document ID
     */
    public static String generateDocumentId(LogEvent event, String producerId, long seq) {
        return generateIdempotencyKey(event, producerId, seq);
    }
}
