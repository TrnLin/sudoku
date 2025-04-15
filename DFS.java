import java.util.Arrays;
public class DFS {
/**
 * Solves the given Sudoku board using backtracking algorithm.
 *
 * @param board The Sudoku board to solve.
 * @return true if the board is solvable, false otherwise.
 */
        public boolean solveSudoku(char[][] board) {
            long startTime = System.nanoTime();
            boolean result = backtrack(board);
            long endTime = System.nanoTime();
            System.out.println("Running time: " + (endTime - startTime) / 1000000.0 + " ms");
            return result;
        }

        private boolean backtrack(char[][] board) {
            // Iterate through each cell in the board
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    // If the cell is empty
                    if (board[i][j] == '.') {
                        // Try filling it with a number from 1 to 9
                        for (char c = '1'; c <= '9'; c++) {
                            // If the number is valid in this position
                            if (isValid(board, i, j, c)) {
                                board[i][j] = c;
                                // Recursively try solving the rest of the board
                                if (backtrack(board))
                                    return true;
                                // If the recursive call does not solve the board, backtrack and try a different number
                                board[i][j] = '.';
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

        private boolean isValid(char[][] board, int row, int col, char c) {
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
                char[][] board1 = {
                    {'5', '3', '.', '.', '7', '.', '.', '.', '.'},
                    {'6', '.', '.', '1', '9', '5', '.', '.', '.'},
                    {'.', '9', '8', '.', '.', '.', '.', '6', '.'},
                    {'8', '.', '.', '.', '6', '.', '.', '.', '3'},
                    {'4', '.', '.', '8', '.', '3', '.', '.', '1'},
                    {'7', '.', '.', '.', '2', '.', '.', '.', '6'},
                    {'.', '6', '.', '.', '.', '.', '2', '8', '.'},
                    {'.', '.', '.', '4', '1', '9', '.', '.', '5'},
                    {'.', '.', '.', '.', '8', '.', '.', '7', '9'}
                };

                char[][] board2 = {
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'}
                };

                char[][] board3 = {
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'},
                    {'.', '.', '.', '.', '.', '.', '.', '.', '.'}
                };

                        // Test the algorithm with provided array
                        DFS dfs = new DFS();
                        boolean result1 = dfs.solveSudoku(board1);
                        boolean result2 = dfs.solveSudoku(board2);
                        boolean result3 = dfs.solveSudoku(board3);

                        System.out.println("Board 1 solvable: " + result1);
                        System.out.println("Board 2 solvable: " + result2);
                        System.out.println("Board 3 solvable: " + result3);

    }
}
