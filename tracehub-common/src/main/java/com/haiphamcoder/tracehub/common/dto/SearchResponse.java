package com.haiphamcoder.tracehub.common.dto;

import java.util.List;

/**
 * Search response DTO for audit logs
 */
public class SearchResponse {
    
    private List<LogEvent> hits;
    private long total;
    private String nextPageToken; // base64 encoded sort values for next page
    private boolean hasMore;
    
    // Default constructor
    public SearchResponse() {}
    
    // Constructor with required fields
    public SearchResponse(List<LogEvent> hits, long total, String nextPageToken, boolean hasMore) {
        this.hits = hits;
        this.total = total;
        this.nextPageToken = nextPageToken;
        this.hasMore = hasMore;
    }
    
    // Getters and Setters
    public List<LogEvent> getHits() { return hits; }
    public void setHits(List<LogEvent> hits) { this.hits = hits; }
    
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    
    public String getNextPageToken() { return nextPageToken; }
    public void setNextPageToken(String nextPageToken) { this.nextPageToken = nextPageToken; }
    
    public boolean isHasMore() { return hasMore; }
    public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }
    
    @Override
    public String toString() {
        return "SearchResponse{" +
                "hits=" + hits +
                ", total=" + total +
                ", nextPageToken='" + nextPageToken + '\'' +
                ", hasMore=" + hasMore +
                '}';
    }
}
