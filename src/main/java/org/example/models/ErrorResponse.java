/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.models;

/**
 * Standard error response for API errors
 */
public class ErrorResponse {
    /** Error message */
    private String message;
    
    /** Detailed error information */
    private String details;
    
    /** Timestamp when the error occurred (milliseconds since epoch) */
    private long timestamp;
    
    /**
     * Default constructor that initializes timestamp to current time
     */
    public ErrorResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Constructor with error message
     * 
     * @param message The error message
     */
    public ErrorResponse(String message) {
        this();
        this.message = message;
    }
    
    /**
     * Constructor with error message and details
     * 
     * @param message The error message
     * @param details The detailed error information
     */
    public ErrorResponse(String message, String details) {
        this();
        this.message = message;
        this.details = details;
    }
    
    /**
     * Get the error message
     * 
     * @return The error message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Set the error message
     * 
     * @param message The error message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Get the detailed error information
     * 
     * @return The detailed error information
     */
    public String getDetails() {
        return details;
    }
    
    /**
     * Set the detailed error information
     * 
     * @param details The detailed error information
     */
    public void setDetails(String details) {
        this.details = details;
    }
    
    /**
     * Get the timestamp when the error occurred
     * 
     * @return The timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Set the timestamp when the error occurred
     * 
     * @param timestamp The timestamp in milliseconds since epoch
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 