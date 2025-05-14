package org.example.algorithms;

import org.example.utils.BoardPrinter;

/**
 * Implements a Sudoku solver using the recursive backtracking algorithm.
 * This algorithm tries each possible value for each empty cell and backtracks
 * when a contradiction is found.
 */
public class RecursiveBacktracking {

    /**
     * Solves the given Sudoku board using recursive backtracking.
     * 
     * @param board The Sudoku board to solve, represented as a 2D array
     * @return true if a solution is found, false if no solution exists
     */
    public boolean solve(int[][] board) {
        int N = board.length;
        int SRN = (int)Math.sqrt(N);

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= N; num++) {
                        if (isSafe(row, col, num, board, N, SRN)) {
                            board[row][col] = num;
                            if (solve(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if it's safe to place a number at a given position in the board.
     * 
     * @param row The row index
     * @param col The column index
     * @param num The number to check
     * @param board The Sudoku board
     * @param N The size of the board (N×N)
     * @param SRN Square root of N, representing the size of a sub-grid
     * @return true if it's safe to place the number, false otherwise
     */
    private boolean isSafe(int row, int col, int num, int[][] board, int N, int SRN) {
        for (int x = 0; x < N; x++) {
            if (board[row][x] == num || board[x][col] == num) {
                return false;
            }
        }

        int startRow = row - row % SRN;
        int startCol = col - col % SRN;

        for (int i = 0; i < SRN; i++) {
            for (int j = 0; j < SRN; j++) {
                if (board[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }

        return true;
    }
}
