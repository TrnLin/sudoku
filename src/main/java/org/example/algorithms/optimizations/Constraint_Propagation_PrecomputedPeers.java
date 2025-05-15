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
 * Sudoku solver implementing constraint propagation with precomputed peers.
 * This optimization caches the peers of each cell to avoid recalculating them
 * during constraint propagation operations.
 */
public class Constraint_Propagation_PrecomputedPeers {
    private final int N;
    private final int SUBGRID;
    private IntSet[][] domains;
    private IntArrayList[][] peers;

    /**
     * Constructs a Sudoku constraint propagation solver with the specified grid size.
     *
     * @param N The size of the grid (must be a perfect square, e.g., 4, 9, 16)
     * @throws IllegalArgumentException if N is not a perfect square
     */
    public Constraint_Propagation_PrecomputedPeers(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
        this.peers = new IntArrayList[N][N];
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                peers[row][col] = computePeers(row, col);
            }
        }
    }

    /**
     * Creates empty domain sets for all cells.
     *
     * @return A 2D array of empty IntSet objects
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
     * Initializes the domains for all cells based on the initial board state.
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
                    IntArrayList peerList = peers[row][col];
                    for (int i = 0; i < peerList.size(); i++) {
                        int[] peer = peerList.get(i);
                        domains[peer[0]][peer[1]].remove(val);
                    }
                }
            }
        }
    }

    /**
     * Performs forward checking with backtracking to solve the Sudoku puzzle.
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
     * Propagates constraints throughout the board after a value assignment.
     *
     * @param board The current state of the Sudoku board
     * @return true if the board is still valid after constraint propagation, false otherwise
     */
    private boolean propagateConstraints(int[][] board) {
        boolean changed;
        do {
            changed = false;
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    if (board[row][col] != 0) {
                        int val = board[row][col];
                        IntArrayList peerList = peers[row][col];
                        for (int i = 0; i < peerList.size(); i++) {
                            int[] peer = peerList.get(i);
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
     * Selects the unassigned cell with the smallest domain size (most constrained).
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
     * Creates a deep copy of the Sudoku board.
     *
     * @param board The board to copy
     * @return A new array with the same contents as the input board
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
     * @param backup The backup board to restore from
     */
    private void restoreBoard(int[][] board, int[][] backup) {
        for (int i = 0; i < N; i++)
            System.arraycopy(backup[i], 0, board[i], 0, N);
    }

    /**
     * Creates a deep copy of the current domains.
     *
     * @return A new 2D array with copies of all domains
     */
    private IntSet[][] copyDomains() {
        IntSet[][] copy = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                copy[i][j] = new IntSet(domains[i][j]);
        return copy;
    }

    /**
     * Checks if it is safe to place a value in the given cell.
     *
     * @param board The current state of the Sudoku board
     * @param row The row index
     * @param col The column index
     * @param val The value to check
     * @return true if the value can be placed in the cell, false otherwise
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
     * Computes all peers (cells in the same row, column, or box) for a given cell.
     *
     * @param row The row index
     * @param col The column index
     * @return A list containing the coordinates of all peer cells
     */
    private IntArrayList computePeers(int row, int col) {
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

    /**
     * Gets the precomputed list of peers for a specific cell.
     *
     * @param row The row index
     * @param col The column index
     * @return A list containing the coordinates of all peer cells
     */
    private IntArrayList getPeers(int row, int col) {
        return peers[row][col];
    }
} 