/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.utils;

/**
 * Utility class for printing Sudoku boards to the console.
 * Provides methods for both simple and formatted board output.
 */
public class BoardPrinter {
    
    /**
     * Prints a Sudoku board to the console
     * 
     * @param board The Sudoku board to print
     */
    public static void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int val : row) {
                System.out.printf("%2d ", val);
            }
            System.out.println();
        }
    }
    
    /**
     * Prints a Sudoku board to the console with formatted display
     * 
     * @param board The Sudoku board to print
     * @param title Optional title to display before the board
     */
    public static void printBoardFormatted(int[][] board, String title) {
        int N = board.length;
        int blockSize = (int) Math.sqrt(N);
        
        if (title != null && !title.isEmpty()) {
            System.out.println(title);
        }
        
        // Print horizontal separator line
        printSeparator(N, blockSize);
        
        for (int i = 0; i < N; i++) {
            if (i % blockSize == 0 && i > 0) {
                printSeparator(N, blockSize);
            }
            
            for (int j = 0; j < N; j++) {
                if (j % blockSize == 0) {
                    System.out.print("| ");
                }
                System.out.printf("%2d ", board[i][j]);
            }
            System.out.println("|");
        }
        
        printSeparator(N, blockSize);
    }
    
    /**
     * Prints a horizontal separator line for the formatted board display.
     * 
     * @param N The size of the board (N x N)
     * @param blockSize The size of each block in the board
     */
    private static void printSeparator(int N, int blockSize) {
        System.out.print("+");
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                System.out.print("---");
            }
            System.out.print("+");
        }
        System.out.println();
    }
}
