package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;
public class Constraint_Propagation_BitMask {
    private final int N;
    private final int SUBGRID;
    private IntSet[][] domains;
    private short[] rowMask, colMask, boxMask;

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

    private IntSet[][] createEmptyDomains() {
        IntSet[][] temp = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                temp[i][j] = new IntSet(N);
        return temp;
    }

    public boolean solve(int[][] board) {
        initializeDomains(board);
        return forwardCheck(board);
    }

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

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[N][N];
        for (int i = 0; i < N; i++)
            System.arraycopy(board[i], 0, newBoard[i], 0, N);
        return newBoard;
    }

    private void restoreBoard(int[][] board, int[][] backup) {
        for (int i = 0; i < N; i++) {
            System.arraycopy(backup[i], 0, board[i], 0, N);
        }
    }

    private IntSet[][] copyDomains() {
        IntSet[][] copy = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                copy[i][j] = new IntSet(domains[i][j]);
        return copy;
    }

    private boolean isSafe(int row, int col, int val) {
        return (rowMask[row] & (1 << (val - 1))) == 0 &&
               (colMask[col] & (1 << (val - 1))) == 0 &&
               (boxMask[row / SUBGRID * SUBGRID + col / SUBGRID] & (1 << (val - 1))) == 0;
    }

    private void setMask(int row, int col, int val) {
        rowMask[row] |= (1 << (val - 1));
        colMask[col] |= (1 << (val - 1));
        boxMask[row / SUBGRID * SUBGRID + col / SUBGRID] |= (1 << (val - 1));
    }

    private void unsetMask(int row, int col, int val) {
        rowMask[row] &= ~(1 << (val - 1));
        colMask[col] &= ~(1 << (val - 1));
        boxMask[row / SUBGRID * SUBGRID + col / SUBGRID] &= ~(1 << (val - 1));
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
                int r = boxRow + i;
                int c = boxCol + j;
                if (r != row || c != col)
                    peers.add(new int[]{r, c});
            }
        }
        return peers;
    }
}
