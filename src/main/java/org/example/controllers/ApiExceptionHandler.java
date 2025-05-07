package org.example.controllers;

import org.example.models.SudokuException;
import org.example.models.SudokuFormResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final long TIMEOUT_MILLIS = 120000; // 2 minutes in milliseconds
    private static final int DEFAULT_SIZE = 4; // Default size for an error response if we can't determine the input size
    
    /**
     * Handle SudokuException specifically to extract the board size
     */
    @ExceptionHandler(SudokuException.class)
    public ResponseEntity<SudokuFormResponse> handleSudokuException(SudokuException ex) {
        logger.error("Sudoku exception caught: {}", ex.getMessage(), ex);
        
        // Get the board size from the exception
        int size = ex.getBoardSize();
        // If size is 0 or invalid, use the default size
        if (size <= 0) {
            size = DEFAULT_SIZE;
        }
        
        // Create an empty board matching the input dimensions
        int[][] emptyBoard = new int[size][size];
        
        // Return empty board with timeout time
        return new ResponseEntity<>(new SudokuFormResponse(emptyBoard, TIMEOUT_MILLIS), HttpStatus.OK);
    }
    
    /**
     * Fallback handler for all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SudokuFormResponse> handleAllExceptions(Exception ex) {
        // Log the exception
        logger.error("Global exception handler caught: {}", ex.getMessage(), ex);
        
        // Create a default empty board
        int[][] emptyBoard = new int[DEFAULT_SIZE][DEFAULT_SIZE];
        
        // Return empty board with timeout time for any unexpected error
        return new ResponseEntity<>(new SudokuFormResponse(emptyBoard, TIMEOUT_MILLIS), HttpStatus.OK);
    }
} 