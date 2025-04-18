import java.util.*;

public class Constraint_Propagation {
    private final int N;
    private final int SUBGRID;
    private Set<Integer>[][] domains;

    public Constraint_Propagation(int N) {
        if (Math.sqrt(N) != (int) Math.sqrt(N)) {
            throw new IllegalArgumentException("N must be a perfect square (e.g., 4, 9, 16)");
        }
        this.N = N;
        this.SUBGRID = (int) Math.sqrt(N);
        this.domains = createEmptyDomains();
    }

    @SuppressWarnings("unchecked")
    private Set<Integer>[][] createEmptyDomains() {
        Set<Integer>[][] temp = (Set<Integer>[][]) new HashSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                temp[i][j] = new HashSet<>();
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
                    for (int[] peer : getPeers(row, col)) {
                        domains[peer[0]][peer[1]].remove(val);
                    }
                }
            }
        }
    }

    private boolean forwardCheck(int[][] board) {
        int[] cell = selectUnassignedCell(board);
        if (cell == null) return true;

        int row = cell[0], col = cell[1];
        List<Integer> values = new ArrayList<>(domains[row][col]);

        for (int value : values) {
            if (isSafe(board, row, col, value)) {
                int[][] boardCopy = copyBoard(board);
                Set<Integer>[][] domainCopy = copyDomains();

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
                        for (int[] peer : getPeers(row, col)) {
                            int r = peer[0], c = peer[1];
                            if (domains[r][c].remove(val)) {
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

    @SuppressWarnings("unchecked")
    private Set<Integer>[][] copyDomains() {
        Set<Integer>[][] copy = (Set<Integer>[][]) new HashSet[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                copy[i][j] = new HashSet<>(domains[i][j]);
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

    private List<int[]> getPeers(int row, int col) {
        List<int[]> peers = new ArrayList<>();

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

    public void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int val : row) {
                System.out.printf("%2d ", val);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int[][] puzzle = {
                {0, 0, 0, 0,  2, 0, 0, 0,  0, 0, 3, 0,  0, 0, 0, 0},
                {0, 0, 0, 0,  0, 7, 0, 4,  0, 0, 0, 0,  0, 0, 0, 0},
                {5, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 6, 0},
                {0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  7, 0, 0, 0},

                {0, 0, 0, 0,  0, 0, 0, 0,  9, 0, 0, 0,  0, 0, 0, 0},
                {0, 0, 0, 0,  0, 0, 0, 0,  0, 6, 0, 0,  0, 0, 0, 0},
                {0, 1, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0},
                {0, 0, 0, 2,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0},

                {0, 0, 0, 0,  0, 0, 0, 0,  8, 0, 0, 0,  0, 0, 0, 0},
                {0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 3,  0, 0, 0, 0},
                {0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 5, 0, 0},
                {0, 0, 0, 9,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 1},

                {0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0},
                {0, 3, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0,  0, 0, 0, 0},
                {0, 0, 0, 0,  0, 0, 5, 0,  0, 0, 0, 0,  0, 0, 0, 0},
                {0, 0, 0, 0,  0, 0, 0, 6,  0, 0, 0, 0,  0, 0, 0, 0}
        };


        Constraint_Propagation solver = new Constraint_Propagation(16);
        if (solver.solve(puzzle)) {
            solver.printBoard(puzzle);
        } else {
            System.out.println("No solution found.");
        }
    }
}
