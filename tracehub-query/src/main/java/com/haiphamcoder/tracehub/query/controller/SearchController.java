package com.haiphamcoder.tracehub.query.controller;

import com.haiphamcoder.tracehub.common.dto.SearchRequest;
import com.haiphamcoder.tracehub.common.dto.SearchResponse;
import com.haiphamcoder.tracehub.query.service.SearchService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for log search
 */
@RestController
@RequestMapping("/api/v1")
@Timed(value = "log.search", description = "Log search metrics")
public class SearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    
    private final SearchService searchService;
    
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    /**
     * Search for log events
     * 
     * @param request the search request
     * @return search response with hits and pagination
     */
    @PostMapping("/search")
    public ResponseEntity<SearchResponse> searchLogs(@Valid @RequestBody SearchRequest request) {
        logger.info("Received search request: tenantId={}, from={}, to={}, size={}", 
                   request.getTenantId(), request.getFrom(), request.getTo(), request.getSize());
        
        // TODO: Extract tenantId from JWT token or API key for authorization
        // TODO: Validate tenantId matches the one in request
        
        try {
            SearchResponse response = searchService.searchLogs(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing search request", e);
            return ResponseEntity.internalServerError().build();
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
