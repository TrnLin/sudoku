package org.example.models;

/**
 * Custom exception for Sudoku-related errors that preserves information about the board dimensions.
 */
public class SudokuException extends RuntimeException {
    private final int boardSize;
    
    /**
     * Constructs a new SudokuException with the specified detail message and board size.
     * 
     * @param message the detail message
     * @param boardSize the size of the Sudoku board (number of rows/columns)
     */
    public SudokuException(String message, int boardSize) {
        super(message);
        this.boardSize = boardSize;
    }
    
    /**
     * Constructs a new SudokuException with the specified detail message, cause, and board size.
     * 
     * @param message the detail message
     * @param cause the cause
     * @param boardSize the size of the Sudoku board (number of rows/columns)
     */
    public SudokuException(String message, Throwable cause, int boardSize) {
        super(message, cause);
        this.boardSize = boardSize;
    }
    
    /**
     * Returns the size of the Sudoku board.
     * 
     * @return the board size
     */
    public int getBoardSize() {
        return boardSize;
    }
} 