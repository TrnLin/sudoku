package org.example.benchmark;

import org.example.algorithms.Constraint_Propagation;
import org.example.algorithms.optimizations.Constraint_Propagation_AC3;
import org.example.algorithms.optimizations.Constraint_Propagation_Bitset;
import org.example.algorithms.optimizations.Constraint_Propagation_Undostack;
import org.example.utils.BoardPrinter;

import java.util.Arrays;

public class PerformanceBenchmark {
    
    // Number of times to run each algorithm for better average measurements
    private static final int ITERATIONS = 5;
    
    // Test puzzles of different sizes
    private static final int[][] PUZZLE_16x16 = {
            {0, 13, 0, 3, 0, 16, 14, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 5, 3, 0, 11, 0, 0, 0, 0, 15, 10, 0, 4, 0},
            {15, 0, 7, 4, 10, 2, 9, 5, 13, 0, 3, 0, 14, 0, 1, 6},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 5, 0, 0, 3, 0, 0, 0},
            {0, 0, 0, 12, 0, 0, 0, 3, 0, 0, 1, 0, 7, 9, 14, 0},
            {0, 0, 0, 10, 0, 0, 13, 9, 6, 4, 8, 3, 0, 0, 12, 0},
            {0, 8, 0, 0, 6, 0, 12, 4, 16, 0, 11, 0, 0, 0, 0, 0},
            {4, 9, 11, 13, 14, 15, 0, 16, 12, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {12, 0, 16, 0, 0, 0, 7, 0, 0, 0, 0, 5, 4, 13, 8, 0},
            {3, 10, 0, 9, 0, 0, 5, 15, 7, 16, 6, 0, 11, 14, 0, 0},
            {5, 0, 0, 14, 0, 9, 2, 10, 0, 8, 0, 0, 1, 6, 16, 0},
            {0, 0, 0, 0, 0, 13, 6, 14, 11, 0, 16, 0, 15, 0, 0, 8},
            {0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 1, 13, 14},
            {0, 0, 0, 16, 0, 10, 0, 0, 2, 0, 14, 0, 0, 5, 11, 0},
            {0, 0, 14, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0}
    };
    
    private static final int[][] PUZZLE_9x9 = {
            {0, 0, 0, 8, 6, 4, 0, 0, 0},
            {0, 1, 0, 0, 3, 9, 0, 0, 8},
            {9, 0, 0, 0, 0, 0, 0, 0, 3},
            {2, 9, 0, 0, 0, 0, 0, 0, 0},
            {6, 8, 0, 4, 7, 0, 3, 0, 0},
            {0, 0, 0, 9, 0, 6, 0, 0, 2},
            {0, 6, 9, 0, 2, 8, 5, 0, 0},
            {0, 2, 0, 1, 0, 0, 6, 8, 0},
            {8, 0, 7, 6, 0, 3, 0, 2, 0}
    };
    

    private static final int[][] PUZZLE_25x25 = {
            {12, 14, 0, 0, 0, 0, 0, 0, 0, 9, 0, 25, 0, 0, 1, 0, 0, 5, 0, 0, 0, 6, 0, 0, 0},
            {9, 17, 15, 0, 0, 22, 6, 0, 3, 13, 0, 11, 8, 14, 7, 25, 0, 0, 12, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 6, 0, 21, 10, 0, 13, 4, 0, 11, 0, 18, 0, 25, 0},
            {5, 0, 0, 0, 23, 0, 0, 0, 14, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 12, 0, 0, 0},
            {25, 21, 11, 22, 0, 7, 0, 0, 0, 20, 12, 2, 5, 0, 15, 0, 17, 6, 23, 0, 9, 13, 0, 0, 0},
            {2, 12, 24, 10, 0, 0, 0, 16, 19, 7, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 3, 0, 9, 0},
            {16, 13, 0, 0, 15, 0, 0, 0, 24, 14, 9, 5, 19, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 0, 4},
            {0, 0, 0, 0, 0, 6, 0, 17, 0, 10, 0, 0, 0, 0, 18, 0, 0, 0, 9, 0, 0, 0, 2, 0, 0},
            {0, 0, 0, 19, 0, 0, 25, 0, 0, 15, 0, 0, 0, 0, 4, 17, 0, 14, 18, 0, 16, 8, 0, 12, 7},
            {0, 25, 0, 0, 18, 9, 0, 0, 12, 0, 0, 13, 21, 0, 0, 7, 15, 20, 0, 0, 22, 0, 0, 0, 17},
            {18, 2, 20, 0, 0, 0, 0, 12, 22, 0, 0, 0, 7, 13, 0, 0, 0, 0, 21, 0, 0, 0, 0, 1, 0},
            {24, 4, 25, 0, 0, 0, 10, 0, 0, 19, 0, 12, 0, 0, 11, 2, 14, 13, 3, 0, 0, 0, 0, 16, 22},
            {15, 0, 0, 0, 11, 0, 0, 0, 6, 23, 21, 0, 0, 0, 9, 19, 0, 22, 0, 0, 0, 0, 14, 0, 0},
            {7, 22, 0, 0, 10, 0, 24, 0, 0, 8, 0, 15, 0, 4, 3, 11, 20, 0, 0, 0, 12, 0, 0, 6, 0},
            {0, 0, 14, 21, 0, 0, 0, 9, 15, 11, 0, 0, 6, 22, 24, 10, 1, 12, 25, 0, 0, 0, 0, 0, 8},
            {0, 0, 22, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {20, 18, 0, 0, 4, 19, 0, 14, 0, 6, 8, 0, 0, 0, 2, 0, 0, 21, 0, 0, 13, 0, 0, 0, 0},
            {1, 0, 0, 6, 0, 0, 0, 0, 0, 17, 22, 0, 0, 0, 0, 0, 0, 11, 0, 0, 2, 21, 12, 19, 0},
            {0, 0, 9, 25, 14, 0, 0, 0, 11, 0, 0, 7, 0, 3, 12, 0, 19, 0, 0, 6, 0, 0, 17, 24, 20},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 6, 18, 0, 0, 0, 9, 0, 2, 24, 0, 0, 14, 7, 11, 0},
            {17, 0, 0, 0, 0, 0, 0, 11, 0, 0, 25, 0, 0, 8, 0, 0, 0, 0, 0, 12, 7, 2, 0, 0, 15},
            {13, 0, 7, 15, 0, 0, 21, 0, 0, 5, 0, 10, 17, 0, 0, 0, 3, 0, 0, 2, 24, 0, 9, 0, 12},
            {11, 0, 0, 24, 12, 0, 0, 8, 9, 16, 0, 0, 0, 0, 0, 22, 0, 25, 6, 14, 4, 0, 18, 20, 19},
            {10, 3, 0, 0, 25, 0, 7, 0, 0, 22, 0, 0, 0, 0, 0, 0, 0, 0, 17, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 12, 3, 0, 0, 0, 0, 21, 4, 15, 0, 16, 10, 24, 7, 0, 17, 0, 8, 5, 13}
    };
    
    public static void main(String[] args) {
        System.out.println("=== Sudoku Solver Performance Test ===\n");
        
        // Run benchmarks for 9x9 puzzle
        System.out.println("Testing with 9x9 puzzle:");
        runBenchmark(9, PUZZLE_9x9);
        System.out.println();
        
        // Run benchmarks for 16x16 puzzle
        System.out.println("Testing with 16x16 puzzle:");
        runBenchmark(16, PUZZLE_16x16);
        System.out.println();

        // Run benchmarks for 25x25 puzzle
        System.out.println("Testing with 25x25 puzzle:");
        runBenchmark(25, PUZZLE_25x25);
    }
    
    private static void runBenchmark(int size, int[][] puzzle) {
        System.out.println("Original Constraint Propagation:");
        testConstraintPropagation(size, copyPuzzle(puzzle));
        
        System.out.println("Bitset optimization:");
        testConstraintPropagationBitset(size, copyPuzzle(puzzle));
        
        System.out.println("Undo stack optimization:");
        testConstraintPropagationUndostack(size, copyPuzzle(puzzle));
        
        System.out.println("AC3 optimization:");
        testConstraintPropagationAC3(size, copyPuzzle(puzzle));
    }
    
    private static void testConstraintPropagation(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation solver = new Constraint_Propagation(size);
            
            long startTime = System.nanoTime();
            boolean solved = solver.solve(puzzleCopy);
            long endTime = System.nanoTime();
            
            times[i] = endTime - startTime;
            
            if (i == 0) {
                if (!solved) {
                    System.out.println("  Failed to solve the puzzle!");
                    return;
                }
                if (solved && size <= 9) {
                    System.out.println("  Solution:");
                    BoardPrinter.printBoardFormatted(puzzleCopy, null);
                }
            }
        }
        
        printStatistics(times);
    }
    
    private static void testConstraintPropagationBitset(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_Bitset solver = new Constraint_Propagation_Bitset(size);
            
            long startTime = System.nanoTime();
            boolean solved = solver.solve(puzzleCopy);
            long endTime = System.nanoTime();
            
            times[i] = endTime - startTime;
            
            if (i == 0 && !solved) {
                System.out.println("  Failed to solve the puzzle!");
                return;
            }
        }
        
        printStatistics(times);
    }
    
    private static void testConstraintPropagationUndostack(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_Undostack solver = new Constraint_Propagation_Undostack(size);
            
            long startTime = System.nanoTime();
            boolean solved = solver.solve(puzzleCopy);
            long endTime = System.nanoTime();
            
            times[i] = endTime - startTime;
            
            if (i == 0 && !solved) {
                System.out.println("  Failed to solve the puzzle!");
                return;
            }
        }
        
        printStatistics(times);
    }
    
    private static void testConstraintPropagationAC3(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_AC3 solver = new Constraint_Propagation_AC3(size);
            
            long startTime = System.nanoTime();
            boolean solved = solver.solve(puzzleCopy);
            long endTime = System.nanoTime();
            
            times[i] = endTime - startTime;
            
            if (i == 0 && !solved) {
                System.out.println("  Failed to solve the puzzle!");
                return;
            }
        }
        
        printStatistics(times);
    }
    
    private static void printStatistics(long[] times) {
        // Calculate statistics
        long minTime = Arrays.stream(times).min().orElse(0);
        long maxTime = Arrays.stream(times).max().orElse(0);
        long avgTime = Arrays.stream(times).sum() / ITERATIONS;
        
        // Print results
        System.out.printf("  Min: %.2f ms\n", minTime / 1_000_000.0);
        System.out.printf("  Max: %.2f ms\n", maxTime / 1_000_000.0);
        System.out.printf("  Avg: %.2f ms\n", avgTime / 1_000_000.0);
    }
    
    private static int[][] copyPuzzle(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }
}
