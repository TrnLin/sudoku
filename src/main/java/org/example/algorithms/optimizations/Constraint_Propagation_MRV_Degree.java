/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntList;
import org.example.models.IntArrayList;

/**
 * Sudoku solver implementation using constraint propagation with Minimum Remaining Values (MRV)
 * and Degree heuristics for enhanced performance.
 * <p>
 * This class combines multiple optimization techniques:
 * <ul>
 *   <li>Constraint Propagation: Reduces domains of variables based on constraints</li>
 *   <li>MRV (Minimum Remaining Values): Selects variables with fewest legal values</li>
 *   <li>Degree Heuristic: Prefers variables that are involved in more constraints</li>
 * </ul>
 */
public class Constraint_Propagation_MRV_Degree {
    private final int N;
    private final int SUBGRID;
    private IntSet[][] domains;

    /**
     * Constructs a Sudoku solver with the specified board size.
     *
     * @param N The dimension of the Sudoku board (e.g., 9 for a 9x9 board)
     * @throws IllegalArgumentException if N is not a perfect square
     */
    public Constraint_Propagation_MRV_Degree(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
    }

    /**
     * Creates empty domains for all cells in the Sudoku board.
     *
     * @return A 2D array of IntSet objects representing the domains
     */
    private IntSet[][] createEmptyDomains() {
        IntSet[][] temp = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                temp[i][j] = new IntSet(N);
        return temp;
    }

    /**
     * Solves the given Sudoku board using constraint propagation with MRV and degree heuristics.
     *
     * @param board The Sudoku board to solve, where 0 represents empty cells
     * @return true if a solution was found, false otherwise
     */
    public boolean solve(int[][] board) {
        initializeDomains(board);
        return forwardCheck(board);
    }

    /**
     * Initializes the domains for all cells based on the initial state of the board.
     * For each filled cell, it updates the domains of related cells.
     *
     * @param board The Sudoku board to analyze
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
     * Implements the forward checking algorithm with backtracking for solving the Sudoku board.
     * Uses MRV and degree heuristics to select cells.
     *
     * @param board The Sudoku board to solve
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
     * Propagates constraints across the board after a value has been assigned to a cell.
     * Removes the assigned value from the domains of all related cells.
     *
     * @param board The current state of the Sudoku board
     * @return false if any domain becomes empty (indicating an inconsistent state), true otherwise
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
                            int[] p = peers.get(i);
                            int r = p[0], c = p[1];
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
     * Selects an unassigned cell to work on next using the MRV and degree heuristics.
     * MRV chooses the cell with fewest legal values, and degree breaks ties by selecting
     * the cell with the highest degree (most constraints on other variables).
     *
     * @param board The current state of the Sudoku board
     * @return An array with [row, col] of the selected cell, or null if all cells are assigned
     */
    private int[] selectUnassignedCell(int[][] board) {
        int minSize = Integer.MAX_VALUE;
        int[] selected = null;
        int maxDegree = -1;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (board[row][col] == 0) {
                    int size = domains[row][col].size();
                    if (size < minSize) {
                        minSize = size;
                        selected = new int[]{row, col};
                        maxDegree = computeDegree(row, col, board);
                    } else if (size == minSize) {
                        int degree = computeDegree(row, col, board);
                        if (degree > maxDegree) {
                            selected = new int[]{row, col};
                            maxDegree = degree;
                        }
                    }
                }
            }
        }
        return selected;
    }

    /**
     * Computes the degree of a cell, which is the number of unassigned peers.
     * Used by the degree heuristic to break ties when multiple cells have the same MRV.
     *
     * @param row The row of the cell
     * @param col The column of the cell
     * @param board The current state of the Sudoku board
     * @return The degree (number of unassigned peers)
     */
    private int computeDegree(int row, int col, int[][] board) {
        IntArrayList peers = getPeers(row, col);
        int degree = 0;
        for (int i = 0; i < peers.size(); i++) {
            int[] peer = peers.get(i);
            if (board[peer[0]][peer[1]] == 0) {
                degree++;
            }
        }
        return degree;
    }

    /**
     * Creates a deep copy of the Sudoku board.
     *
     * @param board The board to copy
     * @return A new instance with the same values
     */
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[N][N];
        for (int i = 0; i < N; i++)
            System.arraycopy(board[i], 0, newBoard[i], 0, N);
        return newBoard;
    }

    /**
     * Restores the board to a previous state during backtracking.
     *
     * @param board The board to restore
     * @param backup The backup to restore from
     */
    private void restoreBoard(int[][] board, int[][] backup) {
        for (int i = 0; i < N; i++)
            System.arraycopy(backup[i], 0, board[i], 0, N);
    }

    /**
     * Creates a deep copy of the current domains.
     *
     * @return A new instance of domains with the same values
     */
    private IntSet[][] copyDomains() {
        IntSet[][] copy = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                copy[i][j] = new IntSet(domains[i][j]);
        return copy;
    }

    /**
     * Checks if placing a value at a specific position is safe according to Sudoku rules.
     * Verifies row, column, and subgrid constraints.
     *
     * @param board The current state of the Sudoku board
     * @param row The row to check
     * @param col The column to check
     * @param val The value to place
     * @return true if the placement is valid, false otherwise
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
     * Gets all peers (related cells) for a given cell.
     * Peers are cells in the same row, column, or subgrid.
     *
     * @param row The row of the cell
     * @param col The column of the cell
     * @return A list of peer cells as [row, col] pairs
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
                if (r != row || c != col)
                    peers.add(new int[]{r, c});
            }
        }
        return peers;
    }
} 