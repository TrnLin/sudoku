package org.example.services;

import org.example.models.IntList;

/**
 * Service implementing an optimized constraint propagation Sudoku solver.
 *
 * Optimizations included:
 *  - Bitmask domain representation (int bitsets).
 *  - Precomputed peer lists for constant-time neighbor lookup.
 *  - MRV (Minimum Remaining Values) + Degree heuristic for variable ordering.
 *  - LCV (Least Constraining Value) for value ordering.
 *
 * AC3 and undo-stack optimizations are omitted for simplicity; forward-checking with fixpoint propagation provides good performance.
 */
public final class ConstraintPropagationService {

    private static final int N = 9;
    private static final int SUBGRID = 3;
    private static final int FULL_MASK = (1 << N) - 1;
    private static final int[][] PEERS = buildPeers();

    /**
     * Solves the given Sudoku puzzle using optimized constraint propagation.
     *
     * @param puzzle input 9x9 Sudoku grid, with 0 for empty cells.
     * @return solved 9x9 grid, or throws exception if unsolvable/invalid.
     */
    public int[][] solve(int[][] puzzle) {
        // Flatten board to 1D
        int[] board = new int[N * N];
        for (int r = 0, idx = 0; r < N; r++) {
            for (int c = 0; c < N; c++, idx++) {
                board[idx] = puzzle[r][c];
            }
        }
        // Initialize domains
        int[] domains = new int[N * N];
        for (int i = 0; i < N * N; i++) {
            domains[i] = FULL_MASK;
        }
        // Assign initial values and propagate constraints
        for (int i = 0; i < N * N; i++) {
            int v = board[i];
            if (v != 0) {
                if ((domains[i] & bit(v)) == 0) {
                    // Value already eliminated
                    throw new IllegalArgumentException("Invalid puzzle: inconsistent value at row=" + (i/N) + ", col=" + (i%N));
                }
                domains[i] = bit(v);
                // Eliminate this value from peers
                int b = bit(v);
                for (int peer : PEERS[i]) {
                    domains[peer] &= ~b;
                    if (domains[peer] == 0) {
                        throw new IllegalArgumentException("Invalid puzzle: no legal value for cell at row=" + (peer/N) + ", col=" + (peer%N));
                    }
                }
            }
        }
        
        // Search and solve
        if (!search(board, domains)) {
            throw new IllegalStateException("No solution found.");
        }
        // Unflatten to 2D result
        int[][] result = new int[N][N];
        for (int i = 0; i < N * N; i++) {
            result[i / N][i % N] = board[i];
        }
        return result;
    }

    // Recursive backtracking with MRV+Degree and LCV
    private static boolean search(int[] board, int[] domains) {
        int idx = selectUnassignedCell(board, domains);
        if (idx == -1) {
            return true; // solved
        }
        int mask = domains[idx];
        int valueCount = Integer.bitCount(mask);
        int[] values = new int[valueCount];
        // Collect possible values
        for (int v = 1, pos = 0; v <= N; v++) {
            int b = bit(v);
            if ((mask & b) != 0) {
                values[pos++] = v;
            }
        }
        // Compute elimination counts for LCV
        int[] elim = new int[valueCount];
        for (int i = 0; i < valueCount; i++) {
            int b = bit(values[i]);
            int count = 0;
            for (int peer : PEERS[idx]) {
                if ((domains[peer] & b) != 0) {
                    count++;
                }
            }
            elim[i] = count;
        }
        // Sort values by increasing elimination (LCV)
        for (int i = 0; i < valueCount - 1; i++) {
            int min = i;
            for (int j = i + 1; j < valueCount; j++) {
                if (elim[j] < elim[min]) {
                    min = j;
                }
            }
            if (min != i) {
                int tmpV = values[i];
                values[i] = values[min];
                values[min] = tmpV;
                int tmpE = elim[i];
                elim[i] = elim[min];
                elim[min] = tmpE;
            }
        }
        // Backup domains state
        int[] domainsBackup = domains.clone();
        for (int v : values) {
            board[idx] = v;
            domains[idx] = bit(v);
            if (propagate(board, domains) && search(board, domains)) {
                return true;
            }
            // Restore for next iteration
            board[idx] = 0;
            System.arraycopy(domainsBackup, 0, domains, 0, domains.length);
        }
        return false;
    }

    // Forward-checking propagation to fixpoint
    private static boolean propagate(int[] board, int[] domains) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < board.length; i++) {
                int v = board[i];
                if (v != 0) {
                    int b = bit(v);
                    for (int peer : PEERS[i]) {
                        if ((domains[peer] & b) != 0) {
                            domains[peer] &= ~b;
                            changed = true;
                            if (domains[peer] == 0) {
                                return false;
                            }
                        }
                    }
                }
            }
        } while (changed);
        return true;
    }

    // MRV with Degree heuristic
    private static int selectUnassignedCell(int[] board, int[] domains) {
        int minSize = Integer.MAX_VALUE;
        int bestIdx = -1;
        int bestDegree = -1;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                int size = Integer.bitCount(domains[i]);
                if (size < minSize) {
                    minSize = size;
                    bestIdx = i;
                    bestDegree = computeDegree(board, i);
                } else if (size == minSize) {
                    int degree = computeDegree(board, i);
                    if (degree > bestDegree) {
                        bestIdx = i;
                        bestDegree = degree;
                    }
                }
            }
        }
        return bestIdx;
    }

    private static int computeDegree(int[] board, int idx) {
        int deg = 0;
        for (int peer : PEERS[idx]) {
            if (board[peer] == 0) {
                deg++;
            }
        }
        return deg;
    }

    private static int bit(int v) {
        return 1 << (v - 1);
    }

    // Precompute peers for each cell: 20 unique neighbors (row, col, block)
    private static int[][] buildPeers() {
        int[][] peers = new int[N * N][];
        for (int idx = 0; idx < N * N; idx++) {
            int row = idx / N;
            int col = idx % N;
            
            // Use IntList for dynamic collection
            IntList peerList = new IntList(24); // Max possible peers
            
            // Track which cells we've already added (to avoid duplicates)
            boolean[] seen = new boolean[N * N];
            
            // Row peers
            for (int c = 0; c < N; c++) {
                if (c != col) {
                    int peerIdx = row * N + c;
                    peerList.add(peerIdx);
                    seen[peerIdx] = true;
                }
            }
            
            // Column peers
            for (int r = 0; r < N; r++) {
                if (r != row) {
                    int peerIdx = r * N + col;
                    peerList.add(peerIdx);
                    seen[peerIdx] = true;
                }
            }
            
            // Block peers
            int boxRow = (row / SUBGRID) * SUBGRID;
            int boxCol = (col / SUBGRID) * SUBGRID;
            for (int r = boxRow; r < boxRow + SUBGRID; r++) {
                for (int c = boxCol; c < boxCol + SUBGRID; c++) {
                    int peerIdx = r * N + c;
                    if (peerIdx != idx && !seen[peerIdx]) {
                        peerList.add(peerIdx);
                        seen[peerIdx] = true;
                    }
                }
            }
            
            // Convert dynamic list to fixed array
            peers[idx] = new int[peerList.size];
            for (int i = 0; i < peerList.size; i++) {
                peers[idx][i] = peerList.elements[i];
            }
        }
        return peers;
    }
}