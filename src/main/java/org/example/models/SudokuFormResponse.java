package org.example.models;

/**
 * Response format for the Sudoku solver API
 */
public class SudokuFormResponse {
    private int[][] solveBoard;
    private long time;
    
    public SudokuFormResponse() {
    }
    
    public SudokuFormResponse(int[][] solveBoard, long time) {
        this.solveBoard = solveBoard;
        this.time = time;
    }

    public int[][] getSolveBoard() {
        return solveBoard;
    }

    public void setSolveBoard(int[][] solveBoard) {
        this.solveBoard = solveBoard;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
} 