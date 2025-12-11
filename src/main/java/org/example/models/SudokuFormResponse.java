/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.models;

/**
 * Response format for the Sudoku solver API
 */
public class SudokuFormResponse {
    /** The solved Sudoku board represented as a 2D array */
    private int[][] solveBoard;
    
    /** The time taken to solve the board in milliseconds */
    private long time;
    
    /**
     * Default constructor for the Sudoku form response
     */
    public SudokuFormResponse() {
    }
    
    /**
     * Constructor with parameters
     * 
     * @param solveBoard The solved Sudoku board
     * @param time The time taken to solve the board
     */
    public SudokuFormResponse(int[][] solveBoard, long time) {
        this.solveBoard = solveBoard;
        this.time = time;
    }

    /**
     * Gets the solved Sudoku board
     * 
     * @return The solved Sudoku board as a 2D array
     */
    public int[][] getSolveBoard() {
        return solveBoard;
    }

    /**
     * Sets the solved Sudoku board
     * 
     * @param solveBoard The solved Sudoku board to set
     */
    public void setSolveBoard(int[][] solveBoard) {
        this.solveBoard = solveBoard;
    }

    /**
     * Gets the time taken to solve the board
     * 
     * @return The time taken in milliseconds
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the time taken to solve the board
     * 
     * @param time The time taken in milliseconds
     */
    public void setTime(long time) {
        this.time = time;
    }
} 