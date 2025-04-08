package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SudokuGenerator_test1 {

    private int n; // Base size (e.g., 3 for a 9x9 Sudoku)
    private int N; // Grid size (N = n*n)
    private int[][] board;
    private Random random = new Random();
    private int solutionCount; // For uniqueness check

    // Bit masks for rows, columns, and subgrids during fillBoardRecursive()
    private int[] rowMask;
    private int[] colMask;
    private int[] blockMask;

    /**
     * @param n The base size (e.g., 3 for a 9x9 grid). Must be >= 1.
     */
    public int[][] generateSudoku(int n) {
        if (n < 1) {
            System.err.println("Error: Input 'n' must be 1 or greater.");
            return null;
        }
        this.n = n;
        this.N = n * n;
        this.board = new int[N][N];

        // Initialize bit mask arrays (all zeros initially)
        rowMask = new int[N];
        colMask = new int[N];
        blockMask = new int[N];

        if (!fillBoardRecursive()) {
            System.err.println("Error: Could not generate a full solution.");
            return null;
        }

        createPuzzle();

        return board;
    }

    private boolean fillBoardRecursive() {
        int[] cell = findEmptyCellWithLeastCandidates();
        if (cell == null) {
            return true; // Board is full
        }
        int r = cell[0];
        int c = cell[1];
        int block = (r / n) * n + (c / n);
        int mask = rowMask[r] | colMask[c] | blockMask[block];

        List<Integer> candidates = new ArrayList<>();
        for (int num = 1; num <= N; num++) {
            int bit = 1 << (num - 1);
            if ((mask & bit) == 0) {
                candidates.add(num);
            }
        }
        Collections.shuffle(candidates, random);

        for (int num : candidates) {
            int bit = 1 << (num - 1);
            board[r][c] = num;
            rowMask[r] |= bit;
            colMask[c] |= bit;
            blockMask[block] |= bit;

            if (fillBoardRecursive()) {
                return true;
            }

            // Backtrack
            board[r][c] = 0;
            rowMask[r] &= ~bit;
            colMask[c] &= ~bit;
            blockMask[block] &= ~bit;
        }
        return false;
    }

    /**
     * @return an array {row, col} for the best candidate cell; null if none found.
     */
    private int[] findEmptyCellWithLeastCandidates() {
        int minCount = Integer.MAX_VALUE;
        int[] best = null;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (board[r][c] == 0) {
                    int block = (r / n) * n + (c / n);
                    int mask = rowMask[r] | colMask[c] | blockMask[block];
                    int count = 0;
                    for (int num = 1; num <= N; num++) {
                        int bit = 1 << (num - 1);
                        if ((mask & bit) == 0) {
                            count++;
                        }
                    }
                    if (count < minCount) {
                        minCount = count;
                        best = new int[] { r, c };
                        if (minCount == 1) return best; // best possible candidate
                    }
                }
            }
        }
        return best;
    }


    /**
     * Removes cells from the full solution to form a puzzle while checking that
     * the puzzle retains exactly one unique solution.
     */
    private void createPuzzle() {
        List<int[]> cells = new ArrayList<>();
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                cells.add(new int[]{r, c});
            }
        }
        Collections.shuffle(cells, random);

        int cellsRemoved = 0;
        for (int[] cell : cells) {
            int r = cell[0];
            int c = cell[1];

            if (board[r][c] == 0)
                continue;

            int temp = board[r][c];
            board[r][c] = 0;

            if (!hasUniqueSolution()) {
                // Revert removal if uniqueness is lost
                board[r][c] = temp;
            } else {
                cellsRemoved++;
            }
        }
        System.out.println("Removed " + cellsRemoved + " cells.");
    }

    /**
     * @return true if there is exactly one solution; false otherwise.
     */
    private boolean hasUniqueSolution() {
        solutionCount = 0;
        // Set up masks (for rows, columns, and blocks) from the current puzzle.
        int[] rMask = new int[N];
        int[] cMask = new int[N];
        int[] bMask = new int[N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (board[r][c] != 0) {
                    int bit = 1 << (board[r][c] - 1);
                    rMask[r] |= bit;
                    cMask[c] |= bit;
                    int block = (r / n) * n + (c / n);
                    bMask[block] |= bit;
                }
            }
        }
        uniquenessSolver(board, rMask, cMask, bMask);
        return solutionCount == 1;
    }

    /**
     * @param puzzle The current state of the puzzle.
     * @param rMask  Bit masks for rows.
     * @param cMask  Bit masks for columns.
     * @param bMask  Bit masks for blocks.
     */
    private void uniquenessSolver(
            int[][] puzzle, int[] rMask, int[] cMask, int[] bMask) {
        int[] cell = findEmptyCellWithLeastCandidates(puzzle, rMask, cMask, bMask);
        if (cell == null) {
            solutionCount++;
            return;
        }
        int r = cell[0];
        int c = cell[1];
        int block = (r / n) * n + (c / n);
        int mask = rMask[r] | cMask[c] | bMask[block];

        for (int num = 1; num <= N; num++) {
            int bit = 1 << (num - 1);
            if ((mask & bit) == 0) {
                puzzle[r][c] = num;
                rMask[r] |= bit;
                cMask[c] |= bit;
                bMask[block] |= bit;

                uniquenessSolver(puzzle, rMask, cMask, bMask);

                // Backtrack
                puzzle[r][c] = 0;
                rMask[r] &= ~bit;
                cMask[c] &= ~bit;
                bMask[block] &= ~bit;

                if (solutionCount > 1)
                    return; // Stop early if more than one solution found
            }
        }
    }

    /**
     * @param puzzle The current puzzle state.
     * @param rMask  Row bit masks.
     * @param cMask  Column bit masks.
     * @param bMask  Block bit masks.
     * @return an int array {row, col} for the chosen cell; null if no empty cell remains.
     */
    private int[] findEmptyCellWithLeastCandidates(
            int[][] puzzle, int[] rMask, int[] cMask, int[] bMask) {
        int minCount = Integer.MAX_VALUE;
        int[] best = null;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (puzzle[r][c] == 0) {
                    int block = (r / n) * n + (c / n);
                    int mask = rMask[r] | cMask[c] | bMask[block];
                    int count = 0;
                    for (int num = 1; num <= N; num++) {
                        int bit = 1 << (num - 1);
                        if ((mask & bit) == 0) {
                            count++;
                        }
                    }
                    if (count < minCount) {
                        minCount = count;
                        best = new int[] { r, c };
                        if (minCount == 1)
                            return best;
                    }
                }
            }
        }
        return best;
    }

    /**
     * @param boardToPrint The board to print.
     */
    public void printBoard(int[][] boardToPrint) {
        if (boardToPrint == null) {
            System.out.println("No board to print.");
            return;
        }
        int currentN = boardToPrint.length;
        int currentn = (int) Math.sqrt(currentN);

        for (int r = 0; r < currentN; r++) {
            if (r > 0 && r % currentn == 0) {
                for (int i = 0; i < currentN + currentn - 1; i++) {
                    System.out.print("-");
                    if ((i + 1) % (currentn + 1) == 0 && i < currentN + currentn - 2)
                        System.out.print("+");
                }
                System.out.println();
            }
            for (int c = 0; c < currentN; c++) {
                if (c > 0 && c % currentn == 0) {
                    System.out.print("|");
                }
                System.out.print(boardToPrint[r][c] == 0 ? "." : boardToPrint[r][c]);
                if (currentN > 9 && boardToPrint[r][c] < 10)
                    System.out.print(" ");
                else
                    System.out.print(" ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the base size 'n' (e.g., 3 for a 9x9 Sudoku): ");

        try {
            int n = Integer.parseInt(scanner.nextLine());
            if (n <= 0) {
                System.out.println("Please enter a positive integer for n.");
                return;
            }

            SudokuGenerator_test1 generator = new SudokuGenerator_test1();
            System.out.println("\nGenerating a " + (n * n) + "x" + (n * n)
                    + " Sudoku puzzle...");

            long startTime = System.currentTimeMillis();
            int[][] puzzle = generator.generateSudoku(n);
            long endTime = System.currentTimeMillis();

            if (puzzle != null) {
                System.out.println("\nGenerated Puzzle (0 = empty cell):");
                generator.printBoard(puzzle);
                System.out.println("\nGeneration took: " + (endTime - startTime) + " ms");
            } else {
                System.out.println("Failed to generate the puzzle.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter an integer.");
        } catch (OutOfMemoryError e) {
            System.err.println("\nError: Out of Memory. The requested Sudoku size is too large for the available memory.");
            System.err.println("Try a smaller value for 'n' (e.g., 3 or 4).");
        } finally {
            scanner.close();
        }
    }
}
