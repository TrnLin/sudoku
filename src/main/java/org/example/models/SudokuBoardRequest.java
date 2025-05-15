/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.models;

/**
 * Request format for Sudoku board from frontend
 */
public class SudokuBoardRequest {
    /**
     * The 2D array representing the Sudoku board
     */
    private int[][] board;
    
    /**
     * Default constructor
     */
    public SudokuBoardRequest() {
    }
    
    /**
     * Constructor with board initialization
     * 
     * @param board The 2D array representing the Sudoku board
     */
    public SudokuBoardRequest(int[][] board) {
        this.board = board;
    }
    
    /**
     * Gets the Sudoku board
     * 
     * @return The 2D array representing the Sudoku board
     */
    public int[][] getBoard() {
        return board;
    }
    
    /**
     * Sets the Sudoku board
     * 
     * @param board The 2D array representing the Sudoku board
     */
    public void setBoard(int[][] board) {
        this.board = board;
    }
} 