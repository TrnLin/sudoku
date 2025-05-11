package org.example.benchmark;

import org.example.algorithms.Constraint_Propagation;
import org.example.algorithms.optimizations.Constraint_Propagation_AC3;
import org.example.algorithms.optimizations.Constraint_Propagation_Bitset;
import org.example.algorithms.optimizations.Constraint_Propagation_Undostack;
import org.example.algorithms.optimizations.Constraint_Propagation_InlineAndFinalize;
import org.example.algorithms.optimizations.Constraint_Propagation_BitMask;
import org.example.algorithms.optimizations.Constraint_Propagation_IterativeDeepening;
import org.example.algorithms.optimizations.Constraint_Propagation_PrecomputedPeers;
import org.example.algorithms.optimizations.Constraint_Propagation_MRV_Degree;
import org.example.algorithms.optimizations.Constraint_Propagation_LCV;
import org.example.utils.BoardPrinter;
import org.example.services.RMIT_Sudoku_Solver;

import java.util.Arrays;

public class PerformanceBenchmark {
    
    // Number of times to run each algorithm for better average measurements
    private static final int ITERATIONS = 5;
    
    // Test puzzles of different sizes
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
    
    public static void main(String[] args) {
        System.out.println("=== Sudoku Solver Performance Test ===\n");
        
        // Run benchmarks for 9x9 puzzle
        System.out.println("Testing with 9x9 puzzle:");
        runBenchmark(9, PUZZLE_9x9);
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
