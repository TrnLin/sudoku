package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntList;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

/**
 * Implementation of a Sudoku solver using constraint propagation with iterative deepening.
 * This algorithm combines forward checking with constraint propagation to efficiently solve Sudoku puzzles.
 * It uses a conflict-limited backtracking approach to gradually increase search depth.
 */
public class Constraint_Propagation_IterativeDeepening {
    /** Size of the Sudoku board (N×N) */
    private final int N;
    /** Size of each subgrid (√N×√N) */
    private final int SUBGRID;
    /** Stores possible values (domains) for each cell */
    private IntSet[][] domains;
    /** Maximum number of conflicts allowed before backtracking */
    private int conflictLimit = 100;

    /**
     * Creates a new constraint propagation solver for the given board size.
     *
     * @param N Size of the Sudoku board (must be a perfect square)
     * @throws IllegalArgumentException if N is not a perfect square
     */
    public Constraint_Propagation_IterativeDeepening(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
    }

    /**
     * Creates the initial domains for all cells.
     * 
     * @return A 2D array of empty domains for each cell
     */
    private IntSet[][] createEmptyDomains() {
        IntSet[][] temp = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                temp[i][j] = new IntSet(N);
        return temp;
    }

    /**
     * Solves the given Sudoku board using constraint propagation and iterative deepening.
     * 
     * @param board The Sudoku board to solve (0 represents empty cells)
     * @return true if a solution was found, false otherwise
     */
    public boolean solve(int[][] board) {
        initializeDomains(board);
        for (int conflicts = 0; conflicts < Integer.MAX_VALUE; conflicts += conflictLimit) {
            if (forwardCheck(board, conflicts)) return true;
        }
        return false;
    }

    /**
     * Initializes the domains for all cells based on the initial board state.
     * For cells with values already assigned, restricts the domain to that value.
     * For empty cells, removes values that are already used by peers.
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
     * Performs forward checking with backtracking to solve the Sudoku board.
     * 
     * @param board The current state of the Sudoku board
     * @param conflictThreshold Maximum number of conflicts allowed before backtracking
     * @return true if a solution was found, false if backtracking is needed
     */
    private boolean forwardCheck(int[][] board, int conflictThreshold) {
        int[] cell = selectUnassignedCell(board);
        if (cell == null) return true;

        int row = cell[0], col = cell[1];
        IntList values = new IntList(N);
        int conflicts = 0;
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

                if (propagateConstraints(board) && forwardCheck(board, conflictThreshold))
        return true;

                conflicts++;
                if (conflicts >= conflictThreshold) {
                    restoreBoard(board, boardCopy);
                    domains = domainCopy;
                    return false;
    }

                restoreBoard(board, boardCopy);
                domains = domainCopy;
            }
        }

        board[row][col] = 0;
        return false;
    }

    /**
     * Propagates constraints by removing values from domains that are no longer valid.
     * 
     * @param board The current state of the Sudoku board
     * @return true if all cells still have at least one possible value, false if any cell has an empty domain
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
     * Selects the next unassigned cell with the smallest domain size (MRV heuristic).
     * 
     * @param board The current state of the Sudoku board
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
     * Creates a deep copy of the current board.
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
     * Creates a deep copy of all cell domains.
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
     * Checks if placing a value at the specified position is valid according to Sudoku rules.
     * 
     * @param board The current state of the Sudoku board
     * @param row Row index
     * @param col Column index
     * @param val Value to check
     * @return true if the value can be placed, false otherwise
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
     * Gets all peer cells for a given cell (cells in the same row, column, or subgrid).
     * 
     * @param row Row index
     * @param col Column index
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
                int r = boxRow + i;
                int c = boxCol + j;
                if (r != row || c != col)
                    peers.add(new int[]{r, c});
            }
        }
        return peers;
    }
}