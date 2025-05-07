package org.example.algorithms.optimizations;

import org.example.models.IntList;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

public class Constraint_Propagation_Bitset {
    private final int N;
    private final int SUBGRID;
    private final int FULL_MASK;
    private int[][] domains;

    public Constraint_Propagation_Bitset(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.FULL_MASK = (1 << N) - 1;
        this.domains = createEmptyDomains();
    }

    private int[][] createEmptyDomains() {
        int[][] temp = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                temp[i][j] = FULL_MASK;
            }
        }
        return temp;
    }

    public boolean solve(int[][] board) {
        initializeDomains(board);
        return forwardCheck(board);
    }

    private void initializeDomains(int[][] board) {
        // Start with all values allowed
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                domains[row][col] = FULL_MASK;
            }
        }

        // Apply initial assignments
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

    private boolean forwardCheck(int[][] board) {
        int[] cell = selectUnassignedCell(board);
        if (cell == null) return true;

        int row = cell[0], col = cell[1];
        IntList values = new IntList(N);

        // Collect allowed values in this domain
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

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, N);
        }
        return newBoard;
    }

    private void restoreBoard(int[][] board, int[][] backup) {
        for (int i = 0; i < N; i++) {
            System.arraycopy(backup[i], 0, board[i], 0, N);
        }
    }

    private int[][] copyDomains() {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(domains[i], 0, copy[i], 0, N);
        }
        return copy;
    }

    private boolean isSafe(int[][] board, int row, int col, int val) {
        int bit = bit(val);
        // Row & column
        for (int i = 0; i < N; i++) {
            if (board[row][i] == val || board[i][col] == val) return false;
        }
        // Subgrid
        int boxRow = (row / SUBGRID) * SUBGRID;
        int boxCol = (col / SUBGRID) * SUBGRID;
        for (int i = 0; i < SUBGRID; i++) {
            for (int j = 0; j < SUBGRID; j++) {
                if (board[boxRow + i][boxCol + j] == val) return false;
            }
        }
        return true;
    }

    private int bit(int val) {
        return 1 << (val - 1);
    }

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
