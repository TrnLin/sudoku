package org.example.models;

/**
 * Request format for Sudoku board from frontend
 */
public class SudokuBoardRequest {
    private int[][] board;
    
    public SudokuBoardRequest() {
    }
    
    public SudokuBoardRequest(int[][] board) {
        this.board = board;
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    public void setBoard(int[][] board) {
        this.board = board;
    }
} 