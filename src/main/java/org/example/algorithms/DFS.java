package org.example.algorithms;

import org.example.utils.BoardPrinter;

public class DFS {
    /**
     * Solves the given Sudoku board using backtracking algorithm.
     *
     * @param board The Sudoku board to solve.
     * @return true if the board is solvable, false otherwise.
     */
    public boolean solveSudoku(int[][] board) {
        long startTime = System.nanoTime();
        boolean result = backtrack(board);
        long endTime = System.nanoTime();
        System.out.println("Running time: " + (endTime - startTime) / 1000000.0 + " ms");
        return result;
    }

    private boolean backtrack(int[][] board) {
        int N = board.length;
        int SRN = (int) Math.sqrt(N);
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 0) {
                    for (int c = 1; c <= N; c++) {
                        if (isValid(board, i, j, c)) {
                            board[i][j] = c;
                            if (backtrack(board))
                                return true;
                            board[i][j] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int col, int c) {
        int N = board.length;
        int SRN = (int) Math.sqrt(N);
        
        for (int i = 0; i < N; i++) {
            if (board[row][i] == c)
                return false;
        }

        for (int i = 0; i < N; i++) {
            if (board[i][col] == c)
                return false;
        }

        int startRow = row - row % SRN;
        int startCol = col - col % SRN;
        for (int i = startRow; i < startRow + SRN; i++) {
            for (int j = startCol; j < startCol + SRN; j++) {
                if (board[i][j] == c)
                    return false;
            }
        }

        return true;
    }
}

