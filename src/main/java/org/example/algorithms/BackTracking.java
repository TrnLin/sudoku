package org.example.algorithms;

import org.example.utils.BoardPrinter;

//Implemented by Linh 
public class BackTracking {
    //Declare the variables
    private final int n;
    private final int blockSize;
    private final int[][] board;
    private final int[] rows;
    private final int[] columns;
    private final int[] blocks;

    // Constructor to initialize the board and bit masks
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

    // Initializes the bit masks for rows, columns, and blocks
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

    // Returns the block index for a given cell
    private int getBlockIndex(int row, int col) {
        return (row / blockSize) * blockSize + (col / blockSize);
    }

    // Returns the block index for a given cell
    public boolean solve() {
        return solveSudoku();
    }

   // Solves the Sudoku puzzle using backtracking
    private boolean solveSudoku() {
        int minOptions = Integer.MAX_VALUE;
        int targetRow = -1;
        int targetCol = -1;

        // Find the empty cell with the minimum number of candidate digits.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 0) {
                    int blockIndex = getBlockIndex(i, j);
                    // Compute available candidates.
                    int available = ((1 << n) - 1) & ~(rows[i] | columns[j] | blocks[blockIndex]);
                    int count = Integer.bitCount(available);
                    if (count == 0) {
                        return false; // Dead end: no possible digit here.
                    }
                    if (count < minOptions) {
                        minOptions = count;
                        targetRow = i;
                        targetCol = j;
                        // Early break if only one candidate is possible.
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

        // No empty cell left, puzzle solved!
        if (targetRow == -1) {
            return true;
        }

        // No candidates available for this cell.
        int blockIndex = getBlockIndex(targetRow, targetCol);
        int available = ((1 << n) - 1) & ~(rows[targetRow]
                | columns[targetCol] | blocks[blockIndex]);

        // Try each candidate digit.
        for (int candidateMask = available; candidateMask != 0;
             candidateMask &= candidateMask - 1) {
            int bit = candidateMask & -candidateMask; // lowest set bit
            int candidate = Integer.numberOfTrailingZeros(bit) + 1; // candidate digit

            // Place candidate.
            board[targetRow][targetCol] = candidate;
            rows[targetRow] |= bit;
            columns[targetCol] |= bit;
            blocks[blockIndex] |= bit;

            // Proceed recursively.
            if (solveSudoku()) {
                return true;
            }

            // Backtrack.
            board[targetRow][targetCol] = 0;
            rows[targetRow] &= ~bit;
            columns[targetCol] &= ~bit;
            blocks[blockIndex] &= ~bit;
        }
        return false;
    }

    //Main method
    public static void main(String[] args) {
        int[][] sampleBoard16x16 = {
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

        long startTime = System.nanoTime();

        BackTracking solver = new BackTracking(sampleBoard16x16);

        if (solver.solve()) {
            long endTime = System.nanoTime();
            long durationNs = (long) ((endTime - startTime) / 1000000.0); // Convert to milliseconds
            // Convert to milliseconds
            System.out.println("Sudoku solved in " + durationNs + " ms.");
            long memoryUsageBytes = (long) sampleBoard16x16.length * sampleBoard16x16[0].length * Integer.BYTES;
            System.out.println("Approximate memory usage: " + memoryUsageBytes + " bytes.");
            BoardPrinter.printBoardFormatted(sampleBoard16x16, "16x16 Solution:");
        } else {
            System.out.println("No solution found.");
        }
    }
}
