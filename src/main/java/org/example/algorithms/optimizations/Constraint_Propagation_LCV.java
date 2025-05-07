package org.example.algorithms.optimizations;

import org.example.models.IntSet;
import org.example.models.IntList;
import org.example.models.IntArrayList;

public class Constraint_Propagation_LCV {
    private final int N;
    private final int SUBGRID;
    private IntSet[][] domains;

    public Constraint_Propagation_LCV(int N) {
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
        if (cell == null) return true;
        int row = cell[0], col = cell[1];

        IntList values = new IntList(N);
        for (int val = 1; val <= N; val++) {
            if (domains[row][col].contains(val)) {
                values.add(val);
            }
        }

        int m = values.size();
        int[] eliminateCount = new int[m];
        for (int i = 0; i < m; i++) {
            int value = values.get(i);
            int count = 0;
            IntArrayList peers = getPeers(row, col);
            for (int j = 0; j < peers.size(); j++) {
                int[] peer = peers.get(j);
                int r = peer[0], c = peer[1];
                if (board[r][c] == 0 && domains[r][c].contains(value)) {
                    count++;
                }
            }
            eliminateCount[i] = count;
        }
        
        for (int i = 0; i < m - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < m; j++) {
                if (eliminateCount[j] < eliminateCount[minIdx]) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                int tmpVal = values.elements[i];
                values.elements[i] = values.elements[minIdx];
                values.elements[minIdx] = tmpVal;
                
                int tmpCnt = eliminateCount[i];
                eliminateCount[i] = eliminateCount[minIdx];
                eliminateCount[minIdx] = tmpCnt;
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
        for (int i = 0; i < N; i++)
            System.arraycopy(backup[i], 0, board[i], 0, N);
    }

    private IntSet[][] copyDomains() {
        IntSet[][] copy = new IntSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                copy[i][j] = new IntSet(domains[i][j]);
        return copy;
    }

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
                if (r != row || c != col)
                    peers.add(new int[]{r, c});
            }
        }
        return peers;
    }
} 