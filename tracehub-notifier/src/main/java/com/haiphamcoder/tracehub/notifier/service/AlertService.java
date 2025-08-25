package com.haiphamcoder.tracehub.notifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for handling basic alerting
 */
@Service
public class AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    // Simple in-memory storage for alert rules and last-fired timestamps
    // TODO: Replace with proper database storage
    private final ConcurrentHashMap<String, AlertRule> alertRules = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> lastFired = new ConcurrentHashMap<>();
    
    public AlertService() {
        // Initialize with some sample rules
        initializeSampleRules();
    }
    
    /**
     * Initialize sample alert rules
     */
    private void initializeSampleRules() {
        // Sample rule: Alert if more than 10 FAILURE actions in 5 minutes
        AlertRule failureRule = new AlertRule(
            "high-failure-rate",
            "High failure rate detected",
            "status", "FAILURE",
            "action", // actionFilter
            5, // timeWindowMinutes
            10, // threshold
            "http://localhost:8084/webhook/sample" // webhookUrl
        );
        
        alertRules.put("high-failure-rate", failureRule);
        logger.info("Initialized sample alert rule: {}", failureRule.getName());
    }
    
    /**
     * Scheduled task to check alert rules
     * Runs every 30 seconds
     */
    @Scheduled(fixedRate = 30000)
    public void checkAlertRules() {
        logger.debug("Checking alert rules...");
        
        for (AlertRule rule : alertRules.values()) {
            try {
                checkAlertRule(rule);
            } catch (Exception e) {
                logger.error("Error checking alert rule: {}", rule.getName(), e);
            }
        }
    }
    
    /**
     * Check a specific alert rule
     * 
     * @param rule the alert rule to check
     */
    private void checkAlertRule(AlertRule rule) {
        // Check cooldown period
        Instant lastFiredTime = lastFired.get(rule.getName());
        if (lastFiredTime != null) {
            long minutesSinceLastFired = java.time.Duration.between(lastFiredTime, Instant.now()).toMinutes();
            if (minutesSinceLastFired < rule.getCooldownMinutes()) {
                logger.debug("Alert rule {} is in cooldown period", rule.getName());
                return;
            }
        }
        
        // TODO: Implement actual query to check conditions
        // For now, simulate a check
        boolean shouldFire = simulateAlertCheck(rule);
        
        if (shouldFire) {
            fireAlert(rule);
            lastFired.put(rule.getName(), Instant.now());
        }
    }
    
    /**
     * Simulate alert condition check
     * TODO: Replace with actual query to OpenSearch
     * 
     * @param rule the alert rule
     * @return true if alert should fire
     */
    private boolean simulateAlertCheck(AlertRule rule) {
        // TODO: Implement actual query logic
        // For now, randomly return true/false for demonstration
        double random = Math.random();
        return random < 0.1; // 10% chance of firing
    }
    
    /**
     * Fire an alert
     * 
     * @param rule the alert rule that was triggered
     */
    private void fireAlert(AlertRule rule) {
        logger.info("Firing alert: {} - {}", rule.getName(), rule.getDescription());
        
        // TODO: Implement webhook call
        // TODO: Implement other notification methods (email, Telegram, etc.)
        
        logger.info("Alert fired successfully: {}", rule.getName());
    }
    
    /**
     * Get all alert rules
     * 
     * @return map of alert rules
     */
    public ConcurrentHashMap<String, AlertRule> getAlertRules() {
        return alertRules;
    }
    
    /**
     * Add a new alert rule
     * 
     * @param rule the alert rule to add
     */
    public void addAlertRule(AlertRule rule) {
        alertRules.put(rule.getName(), rule);
        logger.info("Added alert rule: {}", rule.getName());
    }
    
    /**
     * Remove an alert rule
     * 
     * @param ruleName the name of the rule to remove
     */
    public void removeAlertRule(String ruleName) {
        AlertRule removed = alertRules.remove(ruleName);
        if (removed != null) {
            logger.info("Removed alert rule: {}", ruleName);
        }
    }
    
    /**
     * Simple alert rule representation
     */
    public static class AlertRule {
        private final String name;
        private final String description;
        private final String filterField;
        private final String filterValue;
        private final String actionFilter;
        private final int timeWindowMinutes;
        private final int threshold;
        private final String webhookUrl;
        private final int cooldownMinutes;
        
        public AlertRule(String name, String description, String filterField, String filterValue,
                        String actionFilter, int timeWindowMinutes, int threshold, String webhookUrl) {
            this.name = name;
            this.description = description;
            this.filterField = filterField;
            this.filterValue = filterValue;
            this.actionFilter = actionFilter;
            this.timeWindowMinutes = timeWindowMinutes;
            this.threshold = threshold;
            this.webhookUrl = webhookUrl;
            this.cooldownMinutes = 5; // Default 5 minute cooldown
        }
        
        // Getters
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getFilterField() { return filterField; }
        public String getFilterValue() { return filterValue; }
        public String getActionFilter() { return actionFilter; }
        public int getTimeWindowMinutes() { return timeWindowMinutes; }
        public int getThreshold() { return threshold; }
        public String getWebhookUrl() { return webhookUrl; }
        public int getCooldownMinutes() { return cooldownMinutes; }
    }
}
