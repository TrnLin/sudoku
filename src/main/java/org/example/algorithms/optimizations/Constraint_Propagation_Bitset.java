/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.algorithms.optimizations;

import org.example.models.IntList;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

/**
 * Implementation of a Sudoku solver using constraint propagation with bitset representation.
 * This class efficiently represents and narrows down the possible values for each cell
 * using bitsets, where each bit corresponds to a possible value.
 */
public class Constraint_Propagation_Bitset {
    /** Size of the Sudoku board (N×N) */
    private final int N;
    /** Size of each subgrid (√N×√N) */
    private final int SUBGRID;
    /** Bitmask representing all possible values (all 1's for values 1 to N) */
    private final int FULL_MASK;
    /** Bitset domains representing possible values for each cell */
    private int[][] domains;

    /**
     * Creates a new constraint propagation solver for an N×N Sudoku board.
     * 
     * @param N The size of the Sudoku board (must be a perfect square)
     * @throws IllegalArgumentException if N is not a perfect square
     */
    public Constraint_Propagation_Bitset(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.FULL_MASK = (1 << N) - 1;
        this.domains = createEmptyDomains();
    }

    /**
     * Creates an initial domain matrix where each cell can have any value from 1 to N.
     * 
     * @return A matrix of bitsets where each bitset has all bits set to 1
     */
    private int[][] createEmptyDomains() {
        int[][] temp = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                temp[i][j] = FULL_MASK;
            }
        }
        return temp;
    }

    /**
     * Solves the given Sudoku board using constraint propagation and forward checking.
     * 
     * @param board The Sudoku board to solve (0 represents empty cells)
     * @return true if a solution was found, false otherwise
     */
    public boolean solve(int[][] board) {
        initializeDomains(board);
        return forwardCheck(board);
    }

    /**
     * Initializes domain values based on the initial board state.
     * For each filled cell, it sets the domain to only that value and
     * removes that value from the domains of all related cells.
     * 
     * @param board The Sudoku board to initialize domains from
     */
    private void initializeDomains(int[][] board) {
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                domains[row][col] = FULL_MASK;
            }
        }

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                int val = board[row][col];
                if (val != 0) {
                    domains[row][col] = bit(val);
                    for (int[] peer : getPeers(row, col).elements) {
                        if (peer != null) {
                            int r = peer[0], c = peer[1];
                            domains[r][c] &= ~bit(val);
                        }
                    }
                }
            }
        }
    }

    /**
     * Implements the forward checking algorithm to solve the Sudoku puzzle.
     * Selects an unassigned cell with the minimum domain size and tries
     * each possible value from its domain recursively.
     * 
     * @param board The current state of the Sudoku board
     * @return true if a solution was found, false otherwise
     */
    private boolean forwardCheck(int[][] board) {
        int[] cell = selectUnassignedCell(board);
        if (cell == null) return true;

        int row = cell[0], col = cell[1];
        IntList values = new IntList(N);

        for (int val = 1; val <= N; val++) {
            if ((domains[row][col] & bit(val)) != 0) {
                values.add(val);
            }
        }

        for (int i = 0; i < values.size(); i++) {
            int value = values.get(i);
            if (isSafe(board, row, col, value)) {
                int[][] boardCopy = copyBoard(board);
                int[][] domainCopy = copyDomains();

                board[row][col] = value;
                domains[row][col] = bit(value);

                if (propagateConstraints(board) && forwardCheck(board))
                    return true;

                restoreBoard(board, boardCopy);
                domains = domainCopy;
            }
        }

        board[row][col] = 0;
        return false;
    }

    /**
     * Propagates constraints by removing values from domains based on assigned cells.
     * Continues propagation until no further changes are made to any domain.
     * 
     * @param board The current state of the Sudoku board
     * @return false if any domain becomes empty (indicating an invalid board), true otherwise
     */
    private boolean propagateConstraints(int[][] board) {
        boolean changed;
        do {
            changed = false;
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    if (board[row][col] != 0) {
                        int val = board[row][col];
                        IntArrayList peers = getPeers(row, col);
                        for (int i = 0; i < peers.size(); i++) {
                            int[] peer = peers.get(i);
                            int r = peer[0], c = peer[1];
                            int mask = domains[r][c];
                            if ((mask & bit(val)) != 0) {
                                domains[r][c] = mask & ~bit(val);
                                changed = true;
                                if (domains[r][c] == 0) return false;
                            }
                        }
                    }
                }
            }
        } while (changed);
        return true;
    }

    /**
     * Selects an unassigned cell with the minimum number of possible values.
     * This heuristic (Minimum Remaining Values) helps reduce the branching factor.
     * 
     * @param board The current state of the Sudoku board
     * @return An array with [row, col] of the selected cell, or null if all cells are assigned
     */
    private int[] selectUnassignedCell(int[][] board) {
        int minSize = Integer.MAX_VALUE;
        int[] selected = null;

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (board[row][col] == 0) {
                    int size = Integer.bitCount(domains[row][col]);
                    if (size < minSize) {
                        minSize = size;
                        selected = new int[]{row, col};
                    }
                }
            }
        }

        return selected;
    }

    /**
     * Creates a deep copy of the board.
     * 
     * @param board The board to copy
     * @return A new board with the same values
     */
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, N);
        }
        return newBoard;
    }

    /**
     * Restores the board to a previous state.
     * 
     * @param board The board to restore
     * @param backup The backup board to restore from
     */
    private void restoreBoard(int[][] board, int[][] backup) {
        for (int i = 0; i < N; i++) {
            System.arraycopy(backup[i], 0, board[i], 0, N);
        }
    }

    /**
     * Creates a deep copy of the domains.
     * 
     * @return A new matrix with the same domain values
     */
    private int[][] copyDomains() {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(domains[i], 0, copy[i], 0, N);
        }
        return copy;
    }

    /**
     * Checks if placing a value at the specified position is valid according to Sudoku rules.
     * 
     * @param board The current board state
     * @param row The row to check
     * @param col The column to check
     * @param val The value to check
     * @return true if the value can be placed safely, false otherwise
     */
    private boolean isSafe(int[][] board, int row, int col, int val) {
        int bit = bit(val);
        for (int i = 0; i < N; i++) {
            if (board[row][i] == val || board[i][col] == val) return false;
        }
        int boxRow = (row / SUBGRID) * SUBGRID;
        int boxCol = (col / SUBGRID) * SUBGRID;
        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                if (board[boxRow + i][boxCol + j] == val) return false;
            }
        }
        return true;
    }

    /**
     * Converts a value to its corresponding bit representation.
     * 
     * @param val The value (1 to N)
     * @return A bitmask with only the bit at position (val-1) set to 1
     */
    private int bit(int val) {
        return 1 << (val - 1);
    }

    /**
     * Gets all cells that share a constraint with the given cell (same row, column, or subgrid).
     * 
     * @param row The row of the cell
     * @param col The column of the cell
     * @return A list of cell coordinates that are peers of the given cell
     */
    private IntArrayList getPeers(int row, int col) {
        IntArrayList peers = new IntArrayList(3 * N);
        for (int i = 0; i < N; i++) {
            if (i != col) peers.add(new int[]{row, i});
            if (i != row) peers.add(new int[]{i, col});
        }
        int boxRow = (row / SUBGRID) * SUBGRID;
        int boxCol = (col / SUBGRID) * SUBGRID;
        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                int r = boxRow + i, c = boxCol + j;
                if (r != row || c != col) peers.add(new int[]{r, c});
            }
        }
        return peers;
    }
}
