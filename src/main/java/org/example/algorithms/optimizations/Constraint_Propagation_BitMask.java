/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

/**
 * A Sudoku solver implementation using constraint propagation with bitmasks for efficient domain checking.
 * This implementation uses forward checking and constraint propagation to solve Sudoku puzzles.
 * Bitmasks are used to efficiently track available values in rows, columns, and boxes.
 */
public class Constraint_Propagation_BitMask {
    /** The size of the Sudoku board (N x N). */
    private final int N;
    
    /** The size of a subgrid within the Sudoku board (√N x √N). */
    private final int SUBGRID;
    
    /** Domain sets for each cell, containing possible values for each position. */
    private IntSet[][] domains;
    
    /** Bitmasks tracking values used in each row, column, and box for efficient constraint checking. */
    private short[] rowMask, colMask, boxMask;

    /**
     * Constructs a new constraint propagation solver with bitmask optimization.
     *
     * @param N The size of the Sudoku board (must be a perfect square)
     * @throws IllegalArgumentException if N is not a perfect square
     */
    public Constraint_Propagation_BitMask(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
        this.rowMask = new short[N];
        this.colMask = new short[N];
        this.boxMask = new short[N];
    }

    /**
     * Creates empty domain sets for all cells on the board.
     *
     * @return A 2D array of IntSet objects representing the domains for each cell
     */
    private IntSet[][] createEmptyDomains() {
        IntSet[][] temp = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                temp[i][j] = new IntSet(N);
        return temp;
    }

    /**
     * Solves the given Sudoku board using constraint propagation with bitmasks.
     *
     * @param board The Sudoku board to solve (0 represents empty cells)
     * @return true if a solution is found, false otherwise
     */
    public boolean solve(int[][] board) {
        initializeDomains(board);
        return forwardCheck(board);
    }

    /**
     * Initializes the domain sets for all cells based on the initial board state.
     * Also sets up bitmasks for occupied cells.
     *
     * @param board The initial Sudoku board
     */
    private void initializeDomains(int[][] board) {
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                for (int val = 1; val <= N; val++) {
                    domains[row][col].add(val);
                }
            }
        }

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                int val = board[row][col];
                if (val != 0) {
                    setMask(row, col, val);
                }
            }
        }
    }

    /**
     * Performs forward checking with backtracking to solve the Sudoku board.
     * Selects unassigned cells based on minimum remaining values heuristic.
     *
     * @param board The Sudoku board to solve
     * @return true if a solution is found, false otherwise
     */
    private boolean forwardCheck(int[][] board) {
        int[] cell = selectUnassignedCell(board);
        if (cell == null) return true;

        int row = cell[0], col = cell[1];
        for (int value = 1; value <= N; value++) {
            if (domains[row][col].contains(value) && isSafe(row, col, value)) {
                int[][] boardCopy = copyBoard(board);
                IntSet[][] domainCopy = copyDomains();

                setMask(row, col, value);
                board[row][col] = value;
                domains[row][col].clear();
                domains[row][col].add(value);

                if (propagateConstraints(board) && forwardCheck(board))
                    return true;

                restoreBoard(board, boardCopy);
                domains = domainCopy;
                unsetMask(row, col, value);
            }
        }

        board[row][col] = 0;
        return false;
    }

    /**
     * Propagates constraints by removing values from domains of peers when a cell is assigned.
     * This implements arc consistency by ensuring all cell domains remain valid.
     *
     * @param board The current Sudoku board state
     * @return false if any domain becomes empty (indicating a contradiction), true otherwise
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
                            if (domains[r][c].contains(val)) {
                                domains[r][c].remove(val);
                                changed = true;
                                if (domains[r][c].isEmpty()) return false;
                            }
                        }
                    }
                }
            }
        } while (changed);
        return true;
    }

    /**
     * Selects an unassigned cell using the minimum remaining values (MRV) heuristic.
     * Chooses the cell with the smallest domain size.
     *
     * @param board The current Sudoku board state
     * @return An array containing [row, col] of the selected cell, or null if all cells are assigned
     */
    private int[] selectUnassignedCell(int[][] board) {
        int minSize = Integer.MAX_VALUE;
        int[] selected = null;

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (board[row][col] == 0) {
                    int size = domains[row][col].size();
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
     * Creates a deep copy of the current board state.
     *
     * @param board The board to copy
     * @return A new board that is a deep copy of the original
     */
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[N][N];
        for (int i = 0; i < N; i++)
            System.arraycopy(board[i], 0, newBoard[i], 0, N);
        return newBoard;
    }

    /**
     * Restores the board to a previous state from a backup.
     *
     * @param board The board to restore
     * @param backup The backup board state to restore from
     */
    private void restoreBoard(int[][] board, int[][] backup) {
        for (int i = 0; i < N; i++) {
            System.arraycopy(backup[i], 0, board[i], 0, N);
        }
    }

    /**
     * Creates a deep copy of the current domains.
     *
     * @return A new 2D array of IntSet objects that is a deep copy of the domains
     */
    private IntSet[][] copyDomains() {
        IntSet[][] copy = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                copy[i][j] = new IntSet(domains[i][j]);
        return copy;
    }

    /**
     * Checks if it's safe to place a value in a specific cell by examining bitmasks.
     * Uses bit operations for efficient constraint checking.
     *
     * @param row The row index
     * @param col The column index
     * @param val The value to check
     * @return true if the value can be safely placed, false otherwise
     */
    private boolean isSafe(int row, int col, int val) {
        return (rowMask[row] & (1 << (val - 1))) == 0 &&
               (colMask[col] & (1 << (val - 1))) == 0 &&
               (boxMask[row / SUBGRID * SUBGRID + col / SUBGRID] & (1 << (val - 1))) == 0;
    }

    /**
     * Sets bitmasks to indicate a value is used in a row, column, and box.
     *
     * @param row The row index
     * @param col The column index
     * @param val The value being set
     */
    private void setMask(int row, int col, int val) {
        rowMask[row] |= (1 << (val - 1));
        colMask[col] |= (1 << (val - 1));
        boxMask[row / SUBGRID * SUBGRID + col / SUBGRID] |= (1 << (val - 1));
    }

    /**
     * Unsets bitmasks to indicate a value is no longer used in a row, column, and box.
     * Used when backtracking.
     *
     * @param row The row index
     * @param col The column index
     * @param val The value being unset
     */
    private void unsetMask(int row, int col, int val) {
        rowMask[row] &= ~(1 << (val - 1));
        colMask[col] &= ~(1 << (val - 1));
        boxMask[row / SUBGRID * SUBGRID + col / SUBGRID] &= ~(1 << (val - 1));
    }

    /**
     * Gets all peer cells (cells in the same row, column, or box) for a given cell.
     *
     * @param row The row index
     * @param col The column index
     * @return An IntArrayList containing [row, col] coordinates for all peer cells
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
                int r = boxRow + i;
                int c = boxCol + j;
                if (r != row || c != col)
                    peers.add(new int[]{r, c});
            }
        }
        return peers;
    }
}
