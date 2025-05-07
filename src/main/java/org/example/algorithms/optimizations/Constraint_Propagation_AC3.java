package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntList;
import org.example.models.IntArrayList;
import org.example.utils.BoardPrinter;

public class Constraint_Propagation_AC3 {
    private final int N;
    private final int SUBGRID;
    private IntSet[][] domains;

    public Constraint_Propagation_AC3(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
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
        // start with all values permitted
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                domains[row][col].clear();
                for (int val = 1; val <= N; val++) {
                    domains[row][col].add(val);
                }
            }
        }
        // eliminate based on the initial clues
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
            // no unassigned cells ⇒ solved
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
            if (isSafe(board, row, col, value)) {
                // snapshot
                int[][] boardCopy   = copyBoard(board);
                IntSet[][] domCopy  = copyDomains();

                // assign
                board[row][col] = value;
                domains[row][col].clear();
                domains[row][col].add(value);

                // enforce arc‐consistency globally
                if (ac3() && forwardCheck(board)) {
                    return true;
                }

                // backtrack
                restoreBoard(board, boardCopy);
                domains = domCopy;
            }
        }

        board[row][col] = 0;
        return false;
    }

    /**
     * AC-3 algorithm: enforce arc-consistency on all variable pairs (i,j).
     */
    private boolean ac3() {
        // we'll store each arc as [xiRow, xiCol, xjRow, xjCol]
        IntArrayList queue = new IntArrayList(N * N * (3 * N));
        // initialize with every peer‐arc
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                IntArrayList peers = getPeers(r, c);
                for (int k = 0; k < peers.size(); k++) {
                    int[] p = peers.get(k);
                    queue.add(new int[]{r, c, p[0], p[1]});
                }
            }
        }

        int idx = 0;
        while (idx < queue.size()) {
            int[] arc = queue.get(idx++);
            int xiR = arc[0], xiC = arc[1], xjR = arc[2], xjC = arc[3];
            if (revise(xiR, xiC, xjR, xjC)) {
                if (domains[xiR][xiC].isEmpty()) {
                    return false;
                }
                // add all arcs (xk → xi) for k ∈ peers(xi) \ {xj}
                IntArrayList backPeers = getPeers(xiR, xiC);
                for (int m = 0; m < backPeers.size(); m++) {
                    int[] p2 = backPeers.get(m);
                    if (p2[0] == xjR && p2[1] == xjC) continue;
                    queue.add(new int[]{p2[0], p2[1], xiR, xiC});
                }
            }
        }

        return true;
    }

    /**
     * Revise domains[xi] with respect to domains[xj] under the constraint xi != xj.
     * Returns true if we removed at least one value from xi's domain.
     */
    private boolean revise(int xiR, int xiC, int xjR, int xjC) {
        boolean revised = false;
        // only singleton in xj can force a removal
        if (domains[xjR][xjC].size() == 1) {
            // fetch the sole value in xj
            int only = 0;
            for (int v = 1; v <= N; v++) {
                if (domains[xjR][xjC].contains(v)) {
                    only = v;
                    break;
                }
            }
            // remove it from xi if present
            if (domains[xiR][xiC].contains(only)) {
                domains[xiR][xiC].remove(only);
                revised = true;
            }
        }
        return revised;
    }

    private int[] selectUnassignedCell(int[][] board) {
        int minSize = Integer.MAX_VALUE;
        int[] best   = null;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (board[r][c] == 0) {
                    int sz = domains[r][c].size();
                    if (sz < minSize) {
                        minSize = sz;
                        best    = new int[]{r, c};
                    }
                }
            }
        }
        return best;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] nb = new int[N][N];
        for (int i = 0; i < N; i++) {
            System.arraycopy(board[i], 0, nb[i], 0, N);
        }
        return nb;
    }

    private void restoreBoard(int[][] board, int[][] backup) {
        for (int i = 0; i < N; i++) {
            System.arraycopy(backup[i], 0, board[i], 0, N);
        }
    }

    private IntSet[][] copyDomains() {
        IntSet[][] cp = new IntSet[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                cp[i][j] = new IntSet(domains[i][j]);
            }
        }
        return cp;
    }

    private boolean isSafe(int[][] board, int row, int col, int val) {
        for (int i = 0; i < N; i++) {
            if (board[row][i] == val || board[i][col] == val) {
                return false;
            }
        }
        int boxR = (row / SUBGRID) * SUBGRID;
        int boxC = (col / SUBGRID) * SUBGRID;
        for (int dr = 0; dr < SUBGRID; dr++) {
            for (int dc = 0; dc < SUBGRID; dc++) {
                if (board[boxR + dr][boxC + dc] == val) {
                    return false;
                }
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
        int boxR = (row / SUBGRID) * SUBGRID;
        int boxC = (col / SUBGRID) * SUBGRID;
        for (int dr = 0; dr < SUBGRID; dr++) {
            for (int dc = 0; dc < SUBGRID; dc++) {
                int r = boxR + dr, c = boxC + dc;
                if (r != row || c != col) {
                    peers.add(new int[]{r, c});
                }
            }
        }
        return peers;
    }
}
