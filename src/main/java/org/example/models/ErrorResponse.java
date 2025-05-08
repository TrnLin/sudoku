package org.example.models;

/**
 * Standard error response for API errors
 */
public class ErrorResponse {
    private String message;
    private String details;
    private long timestamp;
    
    public ErrorResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ErrorResponse(String message) {
        this();
        this.message = message;
    }
    
    public ErrorResponse(String message, String details) {
        this();
        this.message = message;
        this.details = details;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 