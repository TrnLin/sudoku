package org.example.services;

import org.example.models.IntList;
import org.example.models.SudokuException;
import org.springframework.stereotype.Service;

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
@Service
public final class ConstraintPropagationService {

    /**
     * Solves the given Sudoku puzzle using optimized constraint propagation.
     *
     * @param puzzle input Sudoku grid of any valid size, with 0 for empty cells.
     * @return solved Sudoku grid, or throws exception if unsolvable/invalid.
     */
    public int[][] solve(int[][] puzzle) {
        if (puzzle == null || puzzle.length == 0) {
            throw new SudokuException("Puzzle cannot be null or empty", 0);
        }
        
        int n = puzzle.length;
        
        double sqrtN = Math.sqrt(n);
        if (sqrtN != Math.floor(sqrtN)) {
            throw new SudokuException("Board size must be a perfect square (e.g., 4x4, 9x9, 16x16). Got: " + n + "x" + n, n);
        }
        
        int subgrid = (int) sqrtN;
        
        for (int i = 0; i < n; i++) {
            if (puzzle[i] == null || puzzle[i].length != n) {
                throw new SudokuException("Puzzle must be a square grid: " + n + "x" + n, n);
            }
        }
        
        if (n > 30) {
            throw new SudokuException("Sudoku size too large. Maximum supported size is 30x30", n);
        }
        
        int fullMask = (1 << n) - 1;
        int[][] peers = buildPeers(n, subgrid);

        int[] board = new int[n * n];
        for (int r = 0, idx = 0; r < n; r++) {
            for (int c = 0; c < n; c++, idx++) {
                board[idx] = puzzle[r][c];
            }
        }
        
        int[] domains = new int[n * n];
        for (int i = 0; i < n * n; i++) {
            domains[i] = fullMask;
        }
        
        for (int i = 0; i < n * n; i++) {
            int v = board[i];
            if (v != 0) {
                if (v < 1 || v > n) {
                    throw new SudokuException("Invalid value " + v + " at position [" + (i/n) + "," + (i%n) + 
                            "]. Values must be 0 or between 1 and " + n, n);
                }
                
                if ((domains[i] & bit(v, n)) == 0) {
                    throw new SudokuException("Invalid puzzle: inconsistent value at row=" + (i/n) + ", col=" + (i%n), n);
                }
                domains[i] = bit(v, n);
                
                int b = bit(v, n);
                for (int peer : peers[i]) {
                    domains[peer] &= ~b;
                    if (domains[peer] == 0) {
                        throw new SudokuException("Invalid puzzle: no legal value for cell at row=" + (peer/n) + ", col=" + (peer%n), n);
                    }
                }
            }
        }
        
        if (!search(board, domains, n, peers)) {
            throw new SudokuException("No solution found", n);
        }
        
        int[][] result = new int[n][n];
        for (int i = 0; i < n * n; i++) {
            result[i / n][i % n] = board[i];
        }
        return result;
    }

    private static boolean search(int[] board, int[] domains, int n, int[][] peers) {
        int idx = selectUnassignedCell(board, domains, peers);
        if (idx == -1) {
            return true;
        }
        int mask = domains[idx];
        int valueCount = Integer.bitCount(mask);
        int[] values = new int[valueCount];
        
        for (int v = 1, pos = 0; v <= n; v++) {
            int b = bit(v, n);
            if ((mask & b) != 0) {
                values[pos++] = v;
            }
        }
        
        int[] elim = new int[valueCount];
        for (int i = 0; i < valueCount; i++) {
            int b = bit(values[i], n);
            int count = 0;
            for (int peer : peers[idx]) {
                if ((domains[peer] & b) != 0) {
                    count++;
                }
            }
            elim[i] = count;
        }
        
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
        
        int[] domainsBackup = domains.clone();
        for (int v : values) {
            board[idx] = v;
            domains[idx] = bit(v, n);
            if (propagate(board, domains, n, peers) && search(board, domains, n, peers)) {
                return true;
            }
            
            board[idx] = 0;
            System.arraycopy(domainsBackup, 0, domains, 0, domains.length);
        }
        return false;
    }

    private static boolean propagate(int[] board, int[] domains, int n, int[][] peers) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < board.length; i++) {
                int v = board[i];
                if (v != 0) {
                    int b = bit(v, n);
                    for (int peer : peers[i]) {
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

    private static int selectUnassignedCell(int[] board, int[] domains, int[][] peers) {
        int minSize = Integer.MAX_VALUE;
        int bestIdx = -1;
        int bestDegree = -1;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                int size = Integer.bitCount(domains[i]);
                if (size < minSize) {
                    minSize = size;
                    bestIdx = i;
                    bestDegree = computeDegree(board, i, peers);
                } else if (size == minSize) {
                    int degree = computeDegree(board, i, peers);
                    if (degree > bestDegree) {
                        bestIdx = i;
                        bestDegree = degree;
                    }
                }
            }
        }
        return bestIdx;
    }

    private static int computeDegree(int[] board, int idx, int[][] peers) {
        int deg = 0;
        for (int peer : peers[idx]) {
            if (board[peer] == 0) {
                deg++;
            }
        }
        return deg;
    }

    private static int bit(int v, int n) {
        if (v < 1 || v > n) {
            throw new SudokuException("Value " + v + " out of range for grid size " + n, n);
        }
        return 1 << (v - 1);
    }

    private static int[][] buildPeers(int n, int subgrid) {
        int[][] peers = new int[n * n][];
        for (int idx = 0; idx < n * n; idx++) {
            int row = idx / n;
            int col = idx % n;
            
            IntList peerList = new IntList(n * 3);
            
            boolean[] seen = new boolean[n * n];
            
            for (int c = 0; c < n; c++) {
                if (c != col) {
                    int peerIdx = row * n + c;
                    peerList.add(peerIdx);
                    seen[peerIdx] = true;
                }
            }
            
            for (int r = 0; r < n; r++) {
                if (r != row) {
                    int peerIdx = r * n + col;
                    if (!seen[peerIdx]) {
                        peerList.add(peerIdx);
                        seen[peerIdx] = true;
                    }
                }
            }
            
            int boxRow = (row / subgrid) * subgrid;
            int boxCol = (col / subgrid) * subgrid;
            for (int r = boxRow; r < boxRow + subgrid; r++) {
                for (int c = boxCol; c < boxCol + subgrid; c++) {
                    int peerIdx = r * n + c;
                    if (peerIdx != idx && !seen[peerIdx]) {
                        peerList.add(peerIdx);
                        seen[peerIdx] = true;
                    }
                }
            }
            
            peers[idx] = new int[peerList.size];
            for (int i = 0; i < peerList.size; i++) {
                peers[idx][i] = peerList.elements[i];
            }
        }
        return peers;
    }
}