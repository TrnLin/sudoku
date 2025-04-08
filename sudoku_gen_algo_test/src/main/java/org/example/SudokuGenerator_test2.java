package org.example;

import java.util.*;

public class SudokuGenerator_test2 {
    /**
     * @param n the square root of the board dimensions (e.g., 3 for a 9x9 board)
     * @return a 2D array representing the sudoku board
     */
    public static int[][] generateSudoku(int n) {
        int size = n * n;
        int[][] board = new int[size][size];
        if (fillBoard(board, n)) {
            return board;
        } else {
            throw new RuntimeException("Failed to generate a valid sudoku board.");
        }
    }

    /**
     * @param board the board to fill
     * @param n     the subgrid dimension (board is n*n x n*n)
     * @return true if the board was successfully filled, false otherwise
     */
    private static boolean fillBoard(int[][] board, int n) {
        int size = n * n;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == 0) {
                    List<Integer> numbers = new ArrayList<>();
                    for (int num = 1; num <= size; num++) {
                        numbers.add(num);
                    }
                    Collections.shuffle(numbers);
                    for (int number : numbers) {
                        if (isValid(board, row, col, number, n)) {
                            board[row][col] = number;
                            if (fillBoard(board, n)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param board  the current board
     * @param row    the row index
     * @param col    the column index
     * @param number the number to place
     * @param n      the subgrid dimension
     * @return true if valid, false otherwise
     */
    private static boolean isValid(int[][] board, int row, int col, int number, int n) {
        int size = n * n;
        // Check row and column
        for (int i = 0; i < size; i++) {
            if (board[row][i] == number || board[i][col] == number) {
                return false;
            }
        }
        // Check the subgrid
        int startRow = row - row % n;
        int startCol = col - col % n;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[startRow + i][startCol + j] == number) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param board the board to print
     */
    public static void printBoard(int[][] board) {
        if (board == null) {
            System.out.println("No sudoku board.");
            return;
        }
        int size = board.length;
        int blockSize = (int) Math.sqrt(size);

        for (int r = 0; r < size; r++) {
            if (r % blockSize == 0) {
                System.out.print("+");
                for (int i = 0; i < blockSize; i++) {
                    for (int j = 0; j < blockSize; j++) {
                        System.out.print("---");
                    }
                    System.out.print("+");
                }
                System.out.println();
            }

            for (int c = 0; c < size; c++) {
                if (c % blockSize == 0) {
                    System.out.print("|");
                }
                System.out.printf(" %2s", board[r][c] == 0 ? "." : board[r][c]);
            }
            System.out.println(" |");
        }
        System.out.print("+");
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                System.out.print("--");
            }
            System.out.print("+");
        }
        System.out.println();
    }
    // Generate a sudoku game by removing cells from the board
    public static int[][] generateSudokuGame(int[][] board, int n) {
        float removalPer = 0.6F;
        int[][] sudoku = board;
        removeCells(sudoku, removalPer, n);
        return sudoku;
    }

    // Remove cells from the board based on the specified percentage
    public static void removeCells(int[][] board, float removePer, int n) {
        int size = board.length;
        int totalCells = size * size;
        int cellsToRemove = (int) Math.floor(totalCells * removePer);
        List<int[]> positions = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                positions.add(new int[]{row, col});
            }
        }
        shuffle(positions);
        for (int i = 0; i < cellsToRemove; i++) {
            int[] pos = positions.get(i);
            int row = pos[0];
            int col = pos[1];
            board[row][col] = 0;
        }
    }

    // Shuffle the list of positions
    public static void shuffle(List<int[]> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            Collections.swap(list, i, j);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the square root of the grid dimension (e.g., 3 for 9x9 sudoku): ");
        int n = scanner.nextInt();
        long startTime = System.currentTimeMillis();
        int[][] sudokuBoard = generateSudoku(n);
        long endTime = System.currentTimeMillis();
        System.out.println("Generated Sudoku Board:");

        printBoard(sudokuBoard);

        System.out.println();
        System.out.println("Generating took " + (endTime - startTime) + "ms ");
        System.out.println();

        System.out.println("Remove cells:");
        int [][] sudokuGame = generateSudokuGame(sudokuBoard, n);
        long startTime2 = System.currentTimeMillis();
        printBoard(sudokuGame);
        long endTime2 = System.currentTimeMillis();
        System.out.print("Removing took " + (endTime2 - startTime2) + "ms ");
        scanner.close();

        System.out.println();

        System.out.println(Arrays.deepToString(sudokuGame));

    }
}
