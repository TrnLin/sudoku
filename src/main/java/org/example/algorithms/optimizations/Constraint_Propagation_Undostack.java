package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntList;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

import java.util.Deque;
import java.util.ArrayDeque;

public class Constraint_Propagation_Undostack {
    private final int N;
    private final int SUBGRID;
    private IntSet[][] domains;
    private Deque<Change> trail;

    public Constraint_Propagation_Undostack(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
    }

    private IntSet[][] createEmptyDomains() {
        IntSet[][] temp = new IntSet[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                temp[i][j] = new IntSet(N);
            }
        }
        return temp;
    }

    public boolean solve(int[][] board) {
        trail = new ArrayDeque<>();
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

    private void undoTo(int targetSize) {
        while (trail.size() > targetSize) {
            trail.pop().undo();
        }
    }

    private interface Change {
        void undo();
    }

    private static class BoardChange implements Change {
        private final int[][] boardRef;
        private final int row, col, oldVal;

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

    private class DomainChange implements Change {
        private final int row, col;
        private final IntSet oldDomain;

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
