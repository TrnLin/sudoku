package org.example.controllers;

import org.example.models.ErrorResponse;
import org.example.models.SudokuException;
import org.example.models.SudokuFormResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final long TIMEOUT_NANOS = 120_000_000_000L; // 120 seconds in nanoseconds
    private static final int DEFAULT_SIZE = 9; 
    
    /**
     * Handle SudokuException specifically to extract the board size
     */
    @ExceptionHandler(SudokuException.class)
    public ResponseEntity<Object> handleSudokuException(SudokuException ex) {
        logger.error("Sudoku exception caught: {}", ex.getMessage(), ex);
        
        int size = ex.getBoardSize();
        if (size <= 0) {
            size = DEFAULT_SIZE;
        }
        
        int[][] emptyBoard = new int[size][size];
        
        // Return both the error message and the empty board response
        ErrorResponse errorResponse = new ErrorResponse("Sudoku validation error", ex.getMessage());
        SudokuFormResponse boardResponse = new SudokuFormResponse(emptyBoard, 0);
        
        return new ResponseEntity<>(
            new Object[] { errorResponse, boardResponse }, 
            HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * Override the method from ResponseEntityExceptionHandler to handle JSON parse errors
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        logger.error("JSON parse error: {}", ex.getMessage());
        
        String message = "Invalid JSON format";
        String details = "Please ensure your request follows the format: {\"board\": [[],[],[]...]}";
        
        if (ex.getMessage() != null && ex.getMessage().contains("Cannot deserialize")) {
            details = "Cannot parse the Sudoku board. Make sure it's a 2D array of integers.";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(message, details);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Fallback handler for all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        logger.error("Global exception handler caught: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "An unexpected error occurred", 
            ex.getMessage() != null ? ex.getMessage() : "Unknown error"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 