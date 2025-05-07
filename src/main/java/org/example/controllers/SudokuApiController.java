package org.example.controllers;

import org.example.models.SudokuException;
import org.example.models.SudokuFormResponse;
import org.example.services.ConstraintPropagationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api")
public class SudokuApiController {
    private static final Logger logger = LoggerFactory.getLogger(SudokuApiController.class);

    private final ConstraintPropagationService solverService;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private static final long TIMEOUT_MILLIS = 120000; // 2 minutes in milliseconds

    @Autowired
    public SudokuApiController(ConstraintPropagationService solverService) {
        this.solverService = solverService;
    }

    @PostMapping("/solve")
    public SudokuFormResponse solveSudoku(@RequestBody int[][] board) {
        logger.info("Received Sudoku solve request");
        
        // Validate and get size before attempting to solve
        int size = validateBoardSize(board);
        
        // Create empty response with same dimensions as input board
        int[][] emptyBoard = new int[size][size];
        
        long startTime = System.currentTimeMillis();
        Future<int[][]> future = null;
        
        try {
            // Complete validation
            validateSudokuBoard(board);
            logger.info("Board validation passed");
            
            // Submit solving task to executor with timeout
            logger.info("Starting Sudoku solver with 2-minute timeout");
            future = executor.submit(new Callable<int[][]>() {
                @Override
                public int[][] call() throws Exception {
                    return solverService.solve(board);
                }
            });
            
            // Get result with 2-minute timeout
            int[][] solvedBoard = future.get(2, TimeUnit.MINUTES);
            long executionTime = System.currentTimeMillis() - startTime;
            
            logger.info("Sudoku solved successfully in {} ms", executionTime);
            return new SudokuFormResponse(solvedBoard, executionTime);
            
        } catch (TimeoutException e) {
            // Cancel the task to prevent it from continuing to run
            if (future != null) {
                future.cancel(true);
            }
            logger.warn("Solver timed out after 2 minutes");
            // For timeout case, return exactly 2 minutes (as requested) with properly sized board
            return new SudokuFormResponse(emptyBoard, TIMEOUT_MILLIS);
        } catch (Exception e) {
            // Cancel the task if it's still running
            if (future != null) {
                future.cancel(true);
            }
            logger.error("Error solving Sudoku: {}", e.getMessage());
            // For other errors, return the actual elapsed time with properly sized board
            long executionTime = System.currentTimeMillis() - startTime;
            return new SudokuFormResponse(emptyBoard, executionTime);
        }
    }
    
    /**
     * Validates basic board size constraints and returns the board size
     * 
     * @param board the Sudoku board to validate
     * @return the size of the board
     * @throws SudokuException if the board is invalid
     */
    private int validateBoardSize(int[][] board) {
        // Check if board exists
        if (board == null || board.length == 0) {
            throw new SudokuException("Board cannot be null or empty", 0);
        }
        
        // Get board size
        int size = board.length;
        
        // Verify it's a valid Sudoku (NxN with perfect square)
        double sqrtSize = Math.sqrt(size);
        if (sqrtSize != Math.floor(sqrtSize)) {
            throw new SudokuException(
                "Board size must be a perfect square (e.g., 4x4, 9x9, 16x16). Got: " + size + "x" + size, size);
        }
        
        // Verify it's a square grid
        for (int i = 0; i < size; i++) {
            if (board[i] == null || board[i].length != size) {
                throw new SudokuException("Board must be a square grid: " + size + "x" + size, size);
            }
        }
        
        return size;
    }
    
    /**
     * Validates that a Sudoku board meets basic requirements before solving.
     * Checks dimensions and rule compliance dynamically based on board size.
     * 
     * @param board the Sudoku board to validate
     * @throws SudokuException if the board is invalid
     */
    private void validateSudokuBoard(int[][] board) {
        int size = board.length;
        int subgrid = (int) Math.sqrt(size);
        
        // Check value range and rule compliance
        boolean[][] rowSeen = new boolean[size][size+1]; // +1 because valid values are 1-size
        boolean[][] colSeen = new boolean[size][size+1];
        boolean[][] boxSeen = new boolean[size][size+1];
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int value = board[row][col];
                
                // Check value range
                if (value < 0 || value > size) {
                    throw new SudokuException("Invalid value " + value + 
                        " at position [" + row + "," + col + "]. Values must be 0-" + size, size);
                }
                
                // Skip checking empty cells (value 0)
                if (value == 0) continue;
                
                // Check row uniqueness
                if (rowSeen[row][value]) {
                    throw new SudokuException("Duplicate value " + value + " in row " + row, size);
                }
                rowSeen[row][value] = true;
                
                // Check column uniqueness
                if (colSeen[col][value]) {
                    throw new SudokuException("Duplicate value " + value + " in column " + col, size);
                }
                colSeen[col][value] = true;
                
                // Check box uniqueness
                int boxIdx = (row / subgrid) * subgrid + (col / subgrid);
                if (boxSeen[boxIdx][value]) {
                    throw new SudokuException("Duplicate value " + value + 
                        " in box at section [" + (row / subgrid) + "," + (col / subgrid) + "]", size);
                }
                boxSeen[boxIdx][value] = true;
            }
        }
    }
} 