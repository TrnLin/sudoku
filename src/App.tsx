import React, { useState, useCallback } from "react";
import { generatePuzzle } from "./generateSudoku"; // Assuming this path is correct
import SudokuGenerator from "./components/sudoku/SudokuGenerator"; // Adjust path if needed
import SudokuSolver from "./components/sudoku/SudokuSolver"; // Adjust path if needed
import SudokuGrid from "./components/sudoku/SudokuBoard"; // Adjust path if needed

import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import axios, { AxiosResponse } from "axios";

// Keep response interface and schema here as they relate to App's state/logic
interface FormResponse {
  solveBoard: number[][];
  time: number;
}

const FormSchema = z.object({
  board: z.array(z.array(z.number())),
});

const App: React.FC = () => {
  const [currentN, setCurrentN] = useState<number>(3);
  const [board, setBoard] = useState<number[][]>([]);
  const [fixedCells, setFixedCells] = useState<boolean[][]>([]);
  const [errorGenerator, setErrorGenerator] = useState<string | null>(null);
  const [errorSolver, setErrorSolver] = useState<string | null>(null);
  const [time, setTime] = useState<number | null>(null);
  const [isGenerating, setIsGenerating] = useState<boolean>(false);
  const [isSolving, setIsSolving] = useState<boolean>(false);

  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: {
      board: [], // Initialize empty, will be set before submit
    },
  });

  const handleGenerate = useCallback((n: number) => {
    setIsGenerating(true);
    setErrorGenerator(null);
    setErrorSolver(null); // Clear solver error on new generation
    setBoard([]); // Clear board immediately
    setFixedCells([]);
    setTime(null);
    setCurrentN(n); // Set currentN here based on input from generator

    // Use setTimeout to allow UI update before potentially blocking generation
    setTimeout(() => {
      try {
        // Adjust difficulty (e.g., 0.6 for ~60% filled cells)
        const puzzle = generatePuzzle(n, 0.6);
        setBoard(puzzle);
        const fixed = puzzle.map((row) => row.map((cell) => cell !== 0));
        setFixedCells(fixed);
        console.log("Generated Sudoku Puzzle:", puzzle);
      } catch (err) {
        console.error(err);
        setErrorGenerator("Error generating Sudoku puzzle.");
        setBoard([]); // Ensure board is cleared on error
        setFixedCells([]);
      } finally {
        setIsGenerating(false);
      }
    }, 0);
  }, []); // No dependencies needed if generatePuzzle is pure

  const handleCellChange = useCallback(
    (
      rowIdx: number,
      colIdx: number,
      event: React.ChangeEvent<HTMLInputElement>
    ) => {
      const newValue = event.target.value;
      // Allow empty string to clear cell (becomes 0)
      const value = newValue === "" ? 0 : parseInt(newValue, 10);
      const SIZE = currentN * currentN;

      // Basic validation: ensure it's a number and within range 0-SIZE
      if (isNaN(value) || value < 0 || value > SIZE) {
        return; // Ignore invalid input
      }

      setBoard((prevBoard) =>
        prevBoard.map((row, i) =>
          i === rowIdx
            ? row.map((cell, j) => (j === colIdx ? value : cell))
            : row
        )
      );
    },
    [currentN]
  ); // Dependency on currentN for SIZE calculation

  const onSubmit = useCallback(async () => {
    if (!board || board.length === 0) {
      setErrorSolver("No board available to solve.");
      return;
    }

    // Set the current board state into the form for validation/submission
    form.setValue("board", board);
    // Trigger validation manually if needed, or rely on handleSubmit
    const isValid = await form.trigger();
    if (!isValid) {
      console.error("Form validation failed:", form.formState.errors);
      setErrorSolver("Board data is invalid.");
      return;
    }

    console.log("Submitting board:", board);
    setIsSolving(true);
    setErrorSolver(null);
    setTime(null);

    try {
      const response: AxiosResponse<FormResponse> = await axios.post(
        "/api/solve", // Ensure this endpoint exists and is correct
        { board: board } // Send the board data
      );
      const { solveBoard, time: solveTime } = response.data; // Rename time to avoid conflict

      if (!solveBoard || solveBoard.length === 0) {
        throw new Error("Solver returned an empty or invalid board.");
      }

      setBoard(solveBoard);
      setTime(solveTime);
      // Make all cells appear "fixed" after solving
      setFixedCells(solveBoard.map((row) => row.map(() => true)));
      console.log("Solved Sudoku Puzzle:", solveBoard);
      console.log("Solve Time:", solveTime);
    } catch (error) {
      console.error("Error solving puzzle:", error);
      let message = "Failed to solve the puzzle.";
      if (axios.isAxiosError(error)) {
        // Provide more specific feedback if possible
        message = error.response?.data?.message || error.message || message;
      } else if (error instanceof Error) {
        message = error.message;
      }
      setErrorSolver(message);
      // Optionally revert fixedCells or leave them as they were before solve attempt
    } finally {
      setIsSolving(false);
    }
  }, [board, form]); // Dependencies: board and form instance

  return (
    <section className='min-h-lvh'>
      <div className='grid grid-cols-[minmax(250px,_1fr)_3fr] gap-4 p-4 w-full min-h-lvh'>
        {/* Left Column: Controls */}
        <div>
          <SudokuGenerator
            onGenerate={handleGenerate}
            isGenerating={isGenerating}
            error={errorGenerator}
          />
          <SudokuSolver
            time={time}
            form={form}
            onSubmit={onSubmit}
            isSolving={isSolving}
            hasBoard={board && board.length > 0}
            error={errorSolver}
          />
        </div>

        {/* Right Column: Grid */}
        <SudokuGrid
          board={board}
          fixedCells={fixedCells}
          currentN={currentN}
          onCellChange={handleCellChange}
        />
      </div>
    </section>
  );
};

export default App;
