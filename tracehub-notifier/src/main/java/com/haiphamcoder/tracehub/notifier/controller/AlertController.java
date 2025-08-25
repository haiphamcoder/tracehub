package com.haiphamcoder.tracehub.notifier.controller;

import com.haiphamcoder.tracehub.notifier.service.AlertService;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for alert management
 */
@RestController
@RequestMapping("/api/v1")
@Timed(value = "alert.management", description = "Alert management metrics")
public class AlertController {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);
    
    private final AlertService alertService;
    
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }
    
    /**
     * Get all alert rules
     * 
     * @return map of alert rules
     */
    @GetMapping("/alerts/rules")
    public ResponseEntity<Map<String, AlertService.AlertRule>> getAlertRules() {
        logger.debug("Getting all alert rules");
        return ResponseEntity.ok(alertService.getAlertRules());
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
