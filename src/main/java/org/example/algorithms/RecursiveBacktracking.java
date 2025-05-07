package org.example.algorithms;

import org.example.utils.BoardPrinter;

public class RecursiveBacktracking {

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
                            board[row][col] = 0; // Backtrack
                        }
                    }
                    return false; // No valid number found, backtrack
                }
            }
        }
        return true; // Puzzle solved
    }

    private boolean isSafe(int row, int col, int num, int[][] board, int N, int SRN) {
        // Check row and column
        for (int x = 0; x < N; x++) {
            if (board[row][x] == num || board[x][col] == num) {
                return false;
            }
        }

        // Check subgrid
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
