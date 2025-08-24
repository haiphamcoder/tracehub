package com.haiphamcoder.tracehub.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Search request DTO for audit logs
 */
public class SearchRequest {
    
    @NotBlank(message = "tenantId is required")
    private String tenantId;
    
    @NotNull(message = "from timestamp is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant from;
    
    @NotNull(message = "to timestamp is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant to;
    
    private String action;
    private String status;
    private String userId;
    private String actorIp;
    
    @Size(max = 1000, message = "query text must not exceed 1000 characters")
    private String q; // full-text search
    
    @Positive(message = "size must be positive")
    private Integer size = 100; // default size
    
    private String searchAfter; // base64 encoded sort values for pagination
    
    // Default constructor
    public SearchRequest() {}
    
    // Getters and Setters
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public Instant getFrom() { return from; }
    public void setFrom(Instant from) { this.from = from; }
    
    public Instant getTo() { return to; }
    public void setTo(Instant to) { this.to = to; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getActorIp() { return actorIp; }
    public void setActorIp(String actorIp) { this.actorIp = actorIp; }
    
    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    
    public String getSearchAfter() { return searchAfter; }
    public void setSearchAfter(String searchAfter) { this.searchAfter = searchAfter; }
    
    @Override
    public String toString() {
        return "SearchRequest{" +
                "tenantId='" + tenantId + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", action='" + action + '\'' +
                ", status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                ", actorIp='" + actorIp + '\'' +
                ", q='" + q + '\'' +
                ", size=" + size +
                ", searchAfter='" + searchAfter + '\'' +
                '}';
    }
}
