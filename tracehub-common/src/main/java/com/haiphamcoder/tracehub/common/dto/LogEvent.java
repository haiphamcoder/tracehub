package com.haiphamcoder.tracehub.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;

/**
 * Core audit log event DTO
 */
public class LogEvent {
    
    @NotNull(message = "timestamp is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant timestamp;
    
    @NotBlank(message = "tenantId is required")
    @Size(min = 1, max = 50, message = "tenantId must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "tenantId must contain only alphanumeric characters, hyphens, and underscores")
    private String tenantId;
    
    @NotBlank(message = "userId is required")
    @Size(min = 1, max = 100, message = "userId must be between 1 and 100 characters")
    private String userId;
    
    @NotBlank(message = "action is required")
    @Size(min = 1, max = 100, message = "action must be between 1 and 100 characters")
    private String action;
    
    @NotBlank(message = "status is required")
    @Pattern(regexp = "^(SUCCESS|FAILURE|WARN|INFO|ERROR)$", message = "status must be one of: SUCCESS, FAILURE, WARN, INFO, ERROR")
    private String status;
    
    @NotBlank(message = "actorIp is required")
    @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$|^[0-9a-fA-F:]+$", 
             message = "actorIp must be a valid IPv4 or IPv6 address")
    private String actorIp;
    
    @NotBlank(message = "message is required")
    @Size(max = 10000, message = "message must not exceed 10000 characters")
    private String message;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
    // Default constructor
    public LogEvent() {}
    
    // Constructor with required fields
    public LogEvent(Instant timestamp, String tenantId, String userId, String action, 
                   String status, String actorIp, String message) {
        this.timestamp = timestamp;
        this.tenantId = tenantId;
        this.userId = userId;
        this.action = action;
        this.status = status;
        this.actorIp = actorIp;
        this.message = message;
    }
    
    // Getters and Setters
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getActorIp() { return actorIp; }
    public void setActorIp(String actorIp) { this.actorIp = actorIp; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    @Override
    public String toString() {
        return "LogEvent{" +
                "timestamp=" + timestamp +
                ", tenantId='" + tenantId + '\'' +
                ", userId='" + userId + '\'' +
                ", action='" + action + '\'' +
                ", status='" + status + '\'' +
                ", actorIp='" + actorIp + '\'' +
                ", message='" + message + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
