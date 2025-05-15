/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.algorithms;

import org.example.models.IntSet;
import org.example.models.IntList;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

/**
 * Implements the constraint propagation algorithm for solving Sudoku puzzles.
 * This algorithm uses forward checking and backtracking with domain reduction
 * to efficiently find solutions by maintaining and propagating constraints.
 */
public class Constraint_Propagation {
    /** The size of the board (N×N) */
    private final int N;
    /** The size of each subgrid (√N×√N) */
    private final int SUBGRID;
    /** Stores the possible values (domain) for each cell */
    private IntSet[][] domains;

    /**
     * Creates a new Constraint_Propagation solver for an N×N Sudoku board.
     *
     * @param N The size of the board (must be a perfect square like 4, 9, 16)
     * @throws IllegalArgumentException if N is not a perfect square
     */
    public Constraint_Propagation(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
    }

    /**
     * Creates and initializes empty domains for all cells.
     *
     * @return A 2D array of IntSet representing empty domains
     */
    private IntSet[][] createEmptyDomains() {
        IntSet[][] temp = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                temp[i][j] = new IntSet(N);
        return temp;
    }

    /**
     * Solves the given Sudoku board using constraint propagation.
     *
     * @param board The Sudoku board to solve (0 represents empty cells)
     * @return true if a solution was found, false otherwise
     */
    public boolean solve(int[][] board) {
        initializeDomains(board);
        return forwardCheck(board);
    }

    /**
     * Initializes the domains of all cells based on the initial board state.
     * For filled cells, restricts domain to the single value.
     * For empty cells, removes values that conflict with filled cells.
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
                    domains[row][col].clear();
                    domains[row][col].add(val);
                    for (int[] peer : getPeers(row, col).elements) {
                        if (peer != null) {
                            domains[peer[0]][peer[1]].remove(val);
                        }
                    }
                }
            }
        }
    }

    /**
     * Implements the forward checking algorithm with backtracking.
     * Selects unassigned cells using MRV (Minimum Remaining Values) heuristic.
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
            if (domains[row][col].contains(val)) {
                values.add(val);
            }
        }

        for (int i = 0; i < values.size(); i++) {
            int value = values.get(i);
            if (isSafe(board, row, col, value)) {
                int[][] boardCopy = copyBoard(board);
                IntSet[][] domainCopy = copyDomains();

                board[row][col] = value;
                domains[row][col].clear();
                domains[row][col].add(value);

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
     * Propagates constraints by removing inconsistent values from domains.
     * Implements arc consistency by ensuring all peers of filled cells
     * don't have the same value in their domains.
     *
     * @param board The current state of the Sudoku board
     * @return true if all domains remain non-empty, false otherwise
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
     * Selects an unassigned cell with the minimum domain size (MRV heuristic).
     *
     * @param board The current state of the Sudoku board
     * @return An array [row, col] of the selected cell, or null if all cells are assigned
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
     * Creates a deep copy of the board.
     *
     * @param board The board to copy
     * @return A new board with the same values
     */
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[N][N];
        for (int i = 0; i < N; i++)
            System.arraycopy(board[i], 0, newBoard[i], 0, N);
        return newBoard;
    }

    /**
     * Restores the board state from a backup.
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
     * Creates a deep copy of all domains.
     *
     * @return A new 2D array of domains with the same values
     */
    private IntSet[][] copyDomains() {
        IntSet[][] copy = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                copy[i][j] = new IntSet(domains[i][j]);
        return copy;
    }

    /**
     * Checks if a value can be safely placed in a cell without violating
     * Sudoku constraints (row, column, and subgrid uniqueness).
     *
     * @param board The current state of the Sudoku board
     * @param row The row of the cell
     * @param col The column of the cell
     * @param val The value to check
     * @return true if the value can be safely placed, false otherwise
     */
    private boolean isSafe(int[][] board, int row, int col, int val) {
        for (int i = 0; i < N; i++)
            if (board[row][i] == val || board[i][col] == val)
                return false;

        int boxRow = (row / SUBGRID) * SUBGRID;
        int boxCol = (col / SUBGRID) * SUBGRID;

        for (int i = 0; i < SUBGRID; i++)
            for (int j = 0; j < SUBGRID; j++)
                if (board[boxRow + i][boxCol + j] == val)
                    return false;

        return true;
    }

    /**
     * Gets all cells that are constrained by the given cell (same row, column, or subgrid).
     *
     * @param row The row of the cell
     * @param col The column of the cell
     * @return A list of peer cell coordinates as [row, col] pairs
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
