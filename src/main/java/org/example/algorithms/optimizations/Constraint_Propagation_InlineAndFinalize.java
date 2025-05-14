package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntList;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

/**
 * Sudoku solver that uses constraint propagation and forward checking.
 * This implementation uses inline optimization techniques and final methods
 * for better performance.
 */
public class Constraint_Propagation_InlineAndFinalize {
    /** The size of the board (N×N). */
    private final int N;
    /** The size of each subgrid (√N). */
    private final int SUBGRID;
    /** Stores the possible values (domain) for each cell. */
    private IntSet[][] domains;

    /**
     * Creates a new constraint propagation solver for a board of size N×N.
     *
     * @param N The size of the board (must be a perfect square)
     * @throws IllegalArgumentException if N is not a perfect square
     */
    public Constraint_Propagation_InlineAndFinalize(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
    }

    /**
     * Creates empty domains for all cells on the board.
     *
     * @return A 2D array of empty IntSet objects
     */
    private final IntSet[][] createEmptyDomains() {
        IntSet[][] temp = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                temp[i][j] = new IntSet(N);
        return temp;
    }

    /**
     * Solves the given Sudoku board.
     *
     * @param board The board to solve (0 represents empty cells)
     * @return true if a solution was found, false otherwise
     */
    public final boolean solve(int[][] board) {
        initializeDomains(board);
        return forwardCheck(board);
    }

    /**
     * Initializes the domains for all cells based on the initial board state.
     *
     * @param board The initial board state
     */
    private final void initializeDomains(int[][] board) {
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
     * Performs forward checking with backtracking to solve the puzzle.
     *
     * @param board The current board state
     * @return true if a solution was found, false otherwise
     */
    private final boolean forwardCheck(int[][] board) {
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

            boolean safe = true;
            for (int k = 0; k < N; k++) {
                if (board[row][k] == value || board[k][col] == value) {
                    safe = false;
                    break;
                }
            }
            if (safe) {
                int boxRow = (row / SUBGRID) * SUBGRID;
                int boxCol = (col / SUBGRID) * SUBGRID;
                for (int ii = 0; ii < SUBGRID && safe; ii++) {
                    for (int jj = 0; jj < SUBGRID; jj++) {
                        if (board[boxRow + ii][boxCol + jj] == value) {
                            safe = false;
                            break;
                        }
                    }
                }
            }
            if (!safe) continue;

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

        board[row][col] = 0;
        return false;
    }

    /**
     * Propagates constraints through the board after a cell assignment.
     *
     * @param board The current board state
     * @return true if constraints are consistent, false if a contradiction is found
     */
    private final boolean propagateConstraints(int[][] board) {
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
     * Selects the next unassigned cell with the smallest domain.
     *
     * @param board The current board state
     * @return An array containing [row, col] of the selected cell, or null if all cells are assigned
     */
    private final int[] selectUnassignedCell(int[][] board) {
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
     * Creates a copy of the current board state.
     *
     * @param board The board to copy
     * @return A new board with the same values
     */
    private final int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[N][N];
        for (int i = 0; i < N; i++)
            System.arraycopy(board[i], 0, newBoard[i], 0, N);
        return newBoard;
    }

    /**
     * Restores a board to a previous state.
     *
     * @param board The board to restore
     * @param backup The backup board state
     */
    private final void restoreBoard(int[][] board, int[][] backup) {
        for (int i = 0; i < N; i++) {
            System.arraycopy(backup[i], 0, board[i], 0, N);
        }
    }

    /**
     * Creates a copy of the current domains.
     *
     * @return A new 2D array of IntSet objects with the same values
     */
    private final IntSet[][] copyDomains() {
        IntSet[][] copy = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                copy[i][j] = new IntSet(domains[i][j]);
        return copy;
    }

    /**
     * Gets all cells that share a constraint with the given cell (same row, column, or subgrid).
     *
     * @param row The row of the cell
     * @param col The column of the cell
     * @return An IntArrayList containing [row, col] pairs of all peer cells
     */
    private final IntArrayList getPeers(int row, int col) {
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
