/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.benchmark;

import org.example.algorithms.Constraint_Propagation;
import org.example.algorithms.optimizations.Constraint_Propagation_AC3;
import org.example.algorithms.optimizations.Constraint_Propagation_Undostack;
import org.example.algorithms.optimizations.Constraint_Propagation_InlineAndFinalize;
import org.example.algorithms.optimizations.Constraint_Propagation_BitMask;
import org.example.algorithms.optimizations.Constraint_Propagation_IterativeDeepening;
import org.example.algorithms.optimizations.Constraint_Propagation_PrecomputedPeers;
import org.example.algorithms.optimizations.Constraint_Propagation_MRV_Degree;
import org.example.algorithms.optimizations.Constraint_Propagation_LCV;
import org.example.utils.BoardPrinter;
import org.example.services.RMIT_Sudoku_Solver;

/**
 * A performance benchmarking class for comparing various Sudoku solver algorithms.
 * This class measures execution time and provides statistics for different optimization strategies.
 */
public class PerformanceBenchmark {
    
    /**
     * Number of times to run each algorithm for better average measurements.
     */
    private static final int ITERATIONS = 20;
    
    /**
     * Test puzzles of different sizes. Currently contains a 9x9 Sudoku puzzle.
     */
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
    
    /**
     * Main method to run all Sudoku solver benchmarks.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== Sudoku Solver Performance Test ===\n");
        
        // Run benchmarks for 9x9 puzzle
        System.out.println("Testing with 9x9 puzzle:");
        runBenchmark(9, PUZZLE_9x9);
    }
    
    /**
     * Runs performance benchmarks for all implemented Sudoku solver algorithms.
     * 
     * @param size Size of the puzzle (e.g., 9 for a 9x9 puzzle)
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
    private static void runBenchmark(int size, int[][] puzzle) {
        System.out.println("Original Constraint Propagation:");
        testConstraintPropagation(size, copyPuzzle(puzzle));
        
        System.out.println("Undo stack optimization:");
        testConstraintPropagationUndostack(size, copyPuzzle(puzzle));
        
        System.out.println("AC3 optimization:");
        testConstraintPropagationAC3(size, copyPuzzle(puzzle));

        System.out.println("BitMask optimization:");
        testConstraintPropagationBitMask(size, copyPuzzle(puzzle));

        System.out.println("Inline and Finalize optimization:");
        testConstraintPropagationInlineAndFinalize(size, copyPuzzle(puzzle));

        System.out.println("Iterative Deepening optimization:");
        testConstraintPropagationIterativeDeepening(size, copyPuzzle(puzzle));

        System.out.println("Precomputed Peers optimization:");
        testConstraintPropagationPrecomputedPeers(size, copyPuzzle(puzzle));

        System.out.println("MRV with Degree optimization:");
        testConstraintPropagationMRVDegree(size, copyPuzzle(puzzle));

        System.out.println("Least Constraining Value optimization:");
        testConstraintPropagationLCV(size, copyPuzzle(puzzle));

        System.out.println("RMIT_Sudoku_Solver optimized solver:");
        testConstraintPropagationService(size, copyPuzzle(puzzle));
    }
    
    /**
     * Tests the base Constraint Propagation algorithm.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
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
    
    /**
     * Tests the Constraint Propagation algorithm with Undo Stack optimization.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
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
    
    /**
     * Tests the Constraint Propagation algorithm with AC3 optimization.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
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
    
    /**
     * Tests the Constraint Propagation algorithm with BitMask optimization.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
    private static void testConstraintPropagationBitMask(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_BitMask solver = new Constraint_Propagation_BitMask(size);
            
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
    
    /**
     * Tests the Constraint Propagation algorithm with Inline and Finalize optimizations.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
    private static void testConstraintPropagationInlineAndFinalize(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_InlineAndFinalize solver = new Constraint_Propagation_InlineAndFinalize(size);
            
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
    
    /**
     * Tests the Constraint Propagation algorithm with Iterative Deepening optimization.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
    private static void testConstraintPropagationIterativeDeepening(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_IterativeDeepening solver = new Constraint_Propagation_IterativeDeepening(size);
            
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
    
    /**
     * Tests the Constraint Propagation algorithm with Precomputed Peers optimization.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
    private static void testConstraintPropagationPrecomputedPeers(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_PrecomputedPeers solver = new Constraint_Propagation_PrecomputedPeers(size);
            
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
    
    /**
     * Tests the Constraint Propagation algorithm with Minimum Remaining Values and Degree heuristics.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
    private static void testConstraintPropagationMRVDegree(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_MRV_Degree solver = new Constraint_Propagation_MRV_Degree(size);
            
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
    
    /**
     * Tests the Constraint Propagation algorithm with Least Constraining Value heuristic.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
    private static void testConstraintPropagationLCV(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            
            Constraint_Propagation_LCV solver = new Constraint_Propagation_LCV(size);
            
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
    
    /**
     * Tests the RMIT_Sudoku_Solver service implementation.
     * 
     * @param size Size of the puzzle
     * @param puzzle 2D array representing the Sudoku puzzle to solve
     */
    private static void testConstraintPropagationService(int size, int[][] puzzle) {
        long[] times = new long[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            int[][] puzzleCopy = copyPuzzle(puzzle);
            RMIT_Sudoku_Solver solver = new RMIT_Sudoku_Solver();
            long startTime = System.nanoTime();
            int[][] solved = solver.solve(puzzleCopy);
            long endTime = System.nanoTime();
            times[i] = endTime - startTime;
            if (i == 0 && solved == null) {
                System.out.println("  Failed to solve the puzzle!");
                return;
            }
        }
        printStatistics(times);
    }
    
    /**
     * Prints statistical data about the execution times.
     * 
     * @param times Array of execution times in nanoseconds
     */
    private static void printStatistics(long[] times) {
        // Calculate statistics using custom IntList instead of Arrays.stream
        long minTime = findMin(times);
        long maxTime = findMax(times);
        long avgTime = calculateAverage(times);
        
        // Print results
        System.out.printf("  Min: %.2f ms\n", minTime / 1_000_000.0);
        System.out.printf("  Max: %.2f ms\n", maxTime / 1_000_000.0);
        System.out.printf("  Avg: %.2f ms\n", avgTime / 1_000_000.0);
    }
    
    /**
     * Finds the minimum value in an array of longs.
     * 
     * @param array The array to search
     * @return The minimum value
     */
    private static long findMin(long[] array) {
        if (array.length == 0) {
            return 0;
        }
        
        long min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }
    
    /**
     * Finds the maximum value in an array of longs.
     * 
     * @param array The array to search
     * @return The maximum value
     */
    private static long findMax(long[] array) {
        if (array.length == 0) {
            return 0;
        }
        
        long max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
    
    /**
     * Calculates the average of values in an array of longs.
     * 
     * @param array The array of values
     * @return The average value
     */
    private static long calculateAverage(long[] array) {
        if (array.length == 0) {
            return 0;
        }
        
        long sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum / array.length;
    }
    
    /**
     * Creates a deep copy of a 2D integer array.
     * 
     * @param original The original puzzle array to copy
     * @return A new array with the same contents as the original
     */
    private static int[][] copyPuzzle(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new int[original[i].length];
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = original[i][j];
            }
        }
        return copy;
    }
}
