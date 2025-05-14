package org.example.algorithms;

import org.example.utils.BoardPrinter;

/**
 * Implementation of Sudoku solver using backtracking algorithm with bit manipulation.
 * This solver uses a minimum options heuristic to improve performance.
 */
public class BackTracking {
    private final int n;
    private final int blockSize;
    private final int[][] board;
    private final int[] rows;
    private final int[] columns;
    private final int[] blocks;

    /**
     * Constructs a Sudoku solver with the given board.
     *
     * @param board The Sudoku board to solve (0 represents empty cells)
     * @throws IllegalArgumentException if the board size doesn't have an integer square root
     */
    public BackTracking(int[][] board) {
        this.board = board;
        this.n = board.length;
        double sqrt = Math.sqrt(n);
        if (sqrt != Math.floor(sqrt)) {
            throw new IllegalArgumentException(
                    "Board size must have an integer square root (e.g. 4, 9, 16, ...)");
        }
        this.blockSize = (int) sqrt;

        rows = new int[n];
        columns = new int[n];
        blocks = new int[n];

        initialize();
    }

    /**
     * Initializes the bit arrays that track digit placement in rows, columns, and blocks.
     * Each bit in the arrays represents whether a specific digit is already placed.
     */
    private void initialize() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int d = board[i][j];
                if (d != 0) {
                    int bit = 1 << (d - 1);
                    rows[i] |= bit;
                    columns[j] |= bit;
                    blocks[getBlockIndex(i, j)] |= bit;
                }
            }
        }
    }

    /**
     * Calculates the block index for a given cell position.
     *
     * @param row The row index
     * @param col The column index
     * @return The block index (0 to n-1)
     */
    private int getBlockIndex(int row, int col) {
        return (row / blockSize) * blockSize + (col / blockSize);
    }

    /**
     * Public method to solve the Sudoku puzzle.
     *
     * @return true if a solution was found, false otherwise
     */
    public boolean solve() {
        return solveSudoku();
    }

    /**
     * Recursively solves the Sudoku puzzle using backtracking with minimum options heuristic.
     * This method:
     * 1. Finds the empty cell with the fewest possible candidate digits
     * 2. Tries each candidate digit in that cell
     * 3. Recurses until a solution is found or all possibilities are exhausted
     *
     * @return true if a solution was found, false otherwise
     */
    private boolean solveSudoku() {
        int minOptions = Integer.MAX_VALUE;
        int targetRow = -1;
        int targetCol = -1;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 0) {
                    int blockIndex = getBlockIndex(i, j);
                    int available = ((1 << n) - 1) & ~(rows[i] | columns[j] | blocks[blockIndex]);
                    int count = Integer.bitCount(available);
                    if (count == 0) {
                        return false;
                    }
                    if (count < minOptions) {
                        minOptions = count;
                        targetRow = i;
                        targetCol = j;
                        if (minOptions == 1) {
                            break;
                        }
                    }
                }
            }
            if (minOptions == 1) {
                break;
            }
        }

        if (targetRow == -1) {
            return true;
        }

        int blockIndex = getBlockIndex(targetRow, targetCol);
        int available = ((1 << n) - 1) & ~(rows[targetRow]
                | columns[targetCol] | blocks[blockIndex]);

        for (int candidateMask = available; candidateMask != 0;
             candidateMask &= candidateMask - 1) {
            int bit = candidateMask & -candidateMask;
            int candidate = Integer.numberOfTrailingZeros(bit) + 1;

            board[targetRow][targetCol] = candidate;
            rows[targetRow] |= bit;
            columns[targetCol] |= bit;
            blocks[blockIndex] |= bit;

            if (solveSudoku()) {
                return true;
            }

            board[targetRow][targetCol] = 0;
            rows[targetRow] &= ~bit;
            columns[targetCol] &= ~bit;
            blocks[blockIndex] &= ~bit;
        }
        return false;
    }
}
