package org.example.algorithms;

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
            // Iterate through each cell in the board
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    // If the cell is empty
                if (board[i][j] == 0) {
                        // Try filling it with a number from 1 to 9
                    for (int c = 1; c <= 9; c++) {
                            // If the number is valid in this position
                            if (isValid(board, i, j, c)) {
                                board[i][j] = c;
                                // Recursively try solving the rest of the board
                                if (backtrack(board))
                                    return true;
                                // If the recursive call does not solve the board, backtrack and try a different number
                            board[i][j] = 0;
                            }
                        }
                        // If no valid number can be found in this position, return false
                        return false;
                    }
                }
            }
            // If all cells are filled, the board is solved
            return true;
        }

    private boolean isValid(int[][] board, int row, int col, int c) {
            // Check if the number is already in the current row
            for (int i = 0; i < 9; i++) {
                if (board[row][i] == c)
                    return false;
            }

            // Check if the number is already in the current column
            for (int i = 0; i < 9; i++) {
                if (board[i][col] == c)
                    return false;
            }

            // Check if the number is already in the current 3x3 subgrid
            int startRow = row - row % 3;
            int startCol = col - col % 3;
            for (int i = startRow; i < startRow + 3; i++) {
                for (int j = startCol; j < startCol + 3; j++) {
                    if (board[i][j] == c)
                        return false;
                }
            }

            // If the number is not already in any of the above locations, it is valid
            return true;
        }

    public static void main(String[] args) {

        int[][] board1 = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
                };

        int[][] board2 = {
                {0, 0, 0, 3, 7, 4, 0, 0, 0, 0, 0, 0, 2, 0, 0, 10},
                {6, 0, 15, 16, 12, 0, 0, 2, 0, 0, 1, 0, 0, 0, 4, 0},
                {0, 5, 7, 0, 0, 0, 0, 0, 15, 16, 0, 0, 0, 0, 0, 0},
                {10, 0, 0, 0, 13, 0, 0, 8, 3, 0, 0, 0, 0, 15, 0, 1},
                {0, 12, 0, 0, 0, 7, 0, 0, 0, 8, 0, 3, 0, 2, 0, 14},
                {0, 0, 0, 0, 0, 3, 0, 11, 0, 4, 0, 0, 5, 7, 9, 13},
                {0, 0, 13, 0, 14, 0, 16, 5, 0, 15, 12, 0, 1, 0, 8, 0},
                {15, 9, 6, 1, 0, 13, 10, 0, 5, 2, 0, 7, 0, 0, 0, 12},
                {0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 7},
                {13, 15, 0, 0, 5, 14, 1, 16, 11, 10, 0, 0, 3, 0, 12, 0},
                {8, 3, 0, 0, 0, 6, 0, 12, 4, 0, 0, 0, 10, 0, 5, 16},
                {0, 1, 14, 0, 10, 0, 7, 9, 0, 0, 0, 12, 0, 0, 2, 11},
                {0, 0, 16, 15, 0, 11, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0},
                {0, 10, 0, 0, 0, 0, 15, 0, 6, 0, 3, 0, 0, 0, 0, 0},
                {1, 0, 11, 5, 0, 0, 2, 7, 0, 0, 15, 0, 14, 0, 0, 0},
                {0, 0, 0, 13, 0, 0, 14, 0, 2, 0, 8, 0, 9, 0, 7, 0}
        };


        // Test the algorithm with provided array
        DFS dfs = new DFS();
        boolean result1 = dfs.solveSudoku(board1);
        boolean result2 = dfs.solveSudoku(board2);

        System.out.println("Board 1 solvable: " + result1);
        System.out.println("Board 2 solvable: " + result2);

    }
}
