package org.example.models;

/**
 * Custom exception for Sudoku-related errors that preserves information about the board dimensions.
 * <p>
 * This exception is thrown when operations on a Sudoku board encounter errors such as
 * invalid moves, unsolvable puzzles, or constraint violations. It retains the board size
 * to provide context for error handling and reporting.
 * </p>
 * 
 * @see org.example.models.SudokuBoard
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