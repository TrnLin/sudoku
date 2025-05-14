package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntList;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

import java.util.Deque;
import java.util.ArrayDeque;

/**
 * Sudoku solver using constraint propagation with an undo stack.
 * This implementation maintains domains for each cell and uses
 * forward checking with constraint propagation to efficiently solve
 * sudoku puzzles.
 */
public class Constraint_Propagation_Undostack {
    private final int N;
    private final int SUBGRID;
    private IntSet[][] domains;
    private Deque<Change> trail;

    /**
     * Constructs a constraint propagation solver for an NxN sudoku board.
     *
     * @param N The size of the board, must be a perfect square (e.g., 4, 9, 16)
     * @throws IllegalArgumentException if N is not a perfect square
     */
    public Constraint_Propagation_Undostack(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
    }

    /**
     * Creates an empty domain set for each cell in the board.
     *
     * @return A 2D array of empty IntSet objects
     */
    private IntSet[][] createEmptyDomains() {
        IntSet[][] temp = new IntSet[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                temp[i][j] = new IntSet(N);
            }
        }
        return temp;
    }

    /**
     * Solves the given sudoku board using constraint propagation.
     *
     * @param board The sudoku board to solve (0 represents empty cells)
     * @return true if a solution was found, false otherwise
     */
    public boolean solve(int[][] board) {
        trail = new ArrayDeque<>();
        initializeDomains(board);
        return forwardCheck(board);
    }

    /**
     * Initializes the domains for all cells based on the initial board state.
     * Empty cells start with all possible values, filled cells have a domain of size 1,
     * and constraints are propagated to peers.
     *
     * @param board The initial sudoku board
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
     * Performs forward checking with constraint propagation to solve the board.
     * Selects unassigned cells with the minimum remaining values heuristic.
     *
     * @param board The current state of the board
     * @return true if a solution was found, false otherwise
     */
    private boolean forwardCheck(int[][] board) {
        int[] cell = selectUnassignedCell(board);
        if (cell == null) {
            return true;
        }
        int row = cell[0], col = cell[1];

        IntList values = new IntList(N);
        for (int v = 1; v <= N; v++) {
            if (domains[row][col].contains(v)) {
                values.add(v);
            }
        }

        for (int i = 0; i < values.size(); i++) {
            int value = values.get(i);
            if (!isSafe(board, row, col, value)) continue;

            int checkpoint = trail.size();

            trail.push(new BoardChange(board, row, col, board[row][col]));
            board[row][col] = value;

            trail.push(new DomainChange(row, col, new IntSet(domains[row][col])));
            domains[row][col].clear();
            domains[row][col].add(value);

            if (propagateConstraints(board) && forwardCheck(board)) {
                return true;
            }

            undoTo(checkpoint);
        }

        return false;
    }

    /**
     * Propagates constraints after a value is assigned to a cell.
     * Removes the assigned value from the domains of all peer cells.
     *
     * @param board The current state of the board
     * @return false if any domain becomes empty (contradiction), true otherwise
     */
    private boolean propagateConstraints(int[][] board) {
        boolean changed;
        do {
            changed = false;
            for (int row = 0; row < N; row++) {
                for (int col = 0; col < N; col++) {
                    int val = board[row][col];
                    if (val != 0) {
                        IntArrayList peers = getPeers(row, col);
                        for (int i = 0; i < peers.size(); i++) {
                            int[] p = peers.get(i);
                            int r = p[0], c = p[1];
                            if (domains[r][c].contains(val)) {
                                trail.push(new DomainChange(r, c, new IntSet(domains[r][c])));
                                domains[r][c].remove(val);
                                changed = true;
                                if (domains[r][c].isEmpty()) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        } while (changed);
        return true;
    }

    /**
     * Restores the board and domains to a previous state by undoing changes.
     *
     * @param targetSize The target size of the trail stack
     */
    private void undoTo(int targetSize) {
        while (trail.size() > targetSize) {
            trail.pop().undo();
        }
    }

    /**
     * Interface for changes that can be undone.
     */
    private interface Change {
        /**
         * Reverts this change.
         */
        void undo();
    }

    /**
     * Represents a change to a board cell value that can be undone.
     */
    private static class BoardChange implements Change {
        private final int[][] boardRef;
        private final int row, col, oldVal;

        /**
         * Creates a new board change.
         *
         * @param boardRef Reference to the board
         * @param row Row of the changed cell
         * @param col Column of the changed cell
         * @param oldVal Previous value of the cell
         */
        BoardChange(int[][] boardRef, int row, int col, int oldVal) {
            this.boardRef = boardRef;
            this.row = row;
            this.col = col;
            this.oldVal = oldVal;
        }

        @Override
        public void undo() {
            boardRef[row][col] = oldVal;
        }
    }

    /**
     * Represents a change to a cell's domain that can be undone.
     */
    private class DomainChange implements Change {
        private final int row, col;
        private final IntSet oldDomain;

        /**
         * Creates a new domain change.
         *
         * @param row Row of the cell whose domain changed
         * @param col Column of the cell whose domain changed
         * @param oldDomain Previous domain of the cell
         */
        DomainChange(int row, int col, IntSet oldDomain) {
            this.row = row;
            this.col = col;
            this.oldDomain = oldDomain;
        }

        @Override
        public void undo() {
            domains[row][col] = oldDomain;
        }
    }

    /**
     * Selects an unassigned cell with the minimum remaining values heuristic.
     *
     * @param board The current state of the board
     * @return A 2-element array with the [row, col] of the selected cell, or null if all cells are assigned
     */
    private int[] selectUnassignedCell(int[][] board) {
        int minSize = Integer.MAX_VALUE;
        int[] best = null;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (board[r][c] == 0) {
                    int sz = domains[r][c].size();
                    if (sz < minSize) {
                        minSize = sz;
                        best = new int[]{r, c};
                    }
                }
            }
        }
        return best;
    }

    /**
     * Checks if placing a value in a cell is valid according to sudoku rules.
     *
     * @param board The current state of the board
     * @param row Row of the cell
     * @param col Column of the cell
     * @param val Value to check
     * @return true if the value can be placed at the position, false otherwise
     */
    private boolean isSafe(int[][] board, int row, int col, int val) {
        for (int i = 0; i < N; i++) {
            if (board[row][i] == val || board[i][col] == val) return false;
        }
        int br = (row / SUBGRID) * SUBGRID;
        int bc = (col / SUBGRID) * SUBGRID;
        for (int dr = 0; dr < SUBGRID; dr++) {
            for (int dc = 0; dc < SUBGRID; dc++) {
                if (board[br + dr][bc + dc] == val) return false;
            }
        }
        return true;
    }

    /**
     * Gets all the cells that are in the same row, column, or subgrid as the given cell.
     *
     * @param row Row of the cell
     * @param col Column of the cell
     * @return A list of [row, col] pairs representing the peer cells
     */
    private IntArrayList getPeers(int row, int col) {
        IntArrayList peers = new IntArrayList(3 * N);
        for (int i = 0; i < N; i++) {
            if (i != col) peers.add(new int[]{row, i});
            if (i != row) peers.add(new int[]{i, col});
        }
        int br = (row / SUBGRID) * SUBGRID;
        int bc = (col / SUBGRID) * SUBGRID;
        for (int dr = 0; dr < SUBGRID; dr++) {
            for (int dc = 0; dc < SUBGRID; dc++) {
                int r = br + dr, c = bc + dc;
                if (r != row || c != col) {
                    peers.add(new int[]{r, c});
                }
            }
        }
        return peers;
    }
}
