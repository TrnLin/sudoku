import React, { useState, useCallback } from "react";
import { generatePuzzle } from "./generateSudoku";
import SudokuGenerator from "./components/sudoku/SudokuGenerator";
import SudokuSolver from "./components/sudoku/SudokuSolver";
import SudokuGrid from "./components/sudoku/SudokuBoard";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import axios, { AxiosResponse } from "axios";

interface FormResponse {
  solveBoard: number[][];
  time: number;
}

const FormSchema = z.object({
  board: z.array(z.array(z.number())),
});

const App: React.FC = () => {
  // Board state
  const [currentN, setCurrentN] = useState<number>(3);
  const [board, setBoard] = useState<number[][]>([]);
  const [fixedCells, setFixedCells] = useState<boolean[][]>([]);

  // Mode state
  const [isManualMode, setIsManualMode] = useState<boolean>(false);

  // Status state
  const [isGenerating, setIsGenerating] = useState<boolean>(false);
  const [isSolving, setIsSolving] = useState<boolean>(false);

  // Error and timing state
  const [errorGenerator, setErrorGenerator] = useState<string | null>(null);
  const [errorSolver, setErrorSolver] = useState<string | null>(null);
  const [time, setTime] = useState<number | null>(null);

  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: { board: [] },
  });

  const initializeEmptyBoard = useCallback((n: number) => {
    const size = n * n;
    return Array(size)
      .fill(0)
      .map(() => Array(size).fill(0));
  }, []);

  const handleManualModeChange = useCallback(
    (isManual: boolean) => {
      setIsManualMode(isManual);
      if (isManual) {
        const newBoard = initializeEmptyBoard(currentN);
        setBoard(newBoard);
        setFixedCells(newBoard.map((row) => row.map(() => false)));
      }
    },
    [currentN, initializeEmptyBoard]
  );

  const handleGenerate = useCallback(
    (n: number) => {
      setIsGenerating(true);
      setErrorGenerator(null);
      setErrorSolver(null);
      setBoard([]);
      setFixedCells([]);
      setTime(null);
      setCurrentN(n);

      if (isManualMode) {
        const newBoard = initializeEmptyBoard(n);
        setBoard(newBoard);
        setFixedCells(newBoard.map((row) => row.map(() => false)));
        setIsGenerating(false);
        return;
      }

      setTimeout(() => {
        try {
          const puzzle = generatePuzzle(n, 0.6);
          setBoard(puzzle);
          setFixedCells(puzzle.map((row) => row.map((cell) => cell !== 0)));
        } catch (err) {
          console.error(err);
          setErrorGenerator("Error generating Sudoku puzzle.");
          setBoard([]);
          setFixedCells([]);
        } finally {
          setIsGenerating(false);
        }
      }, 0);
    },
    [isManualMode, initializeEmptyBoard]
  );

  const handleCellChange = useCallback(
    (
      rowIdx: number,
      colIdx: number,
      event: React.ChangeEvent<HTMLInputElement>
    ) => {
      const newValue = event.target.value;
      const value = newValue === "" ? 0 : parseInt(newValue, 10);
      const SIZE = currentN * currentN;

      if (isNaN(value) || value < 0 || value > SIZE) return;

      setBoard((prevBoard) =>
        prevBoard.map((row, i) =>
          i === rowIdx
            ? row.map((cell, j) => (j === colIdx ? value : cell))
            : row
        )
      );
    },
    [currentN]
  );

  const onSubmit = useCallback(async () => {
    if (!board?.length) {
      setErrorSolver("No board available to solve.");
      return;
    }

    form.setValue("board", board);
    const isValid = await form.trigger();
    if (!isValid) {
      setErrorSolver("Board data is invalid.");
      return;
    }

    setIsSolving(true);
    setErrorSolver(null);
    setTime(null);

    try {
      const response: AxiosResponse<FormResponse> = await axios.post(
        "/api/solve",
        { board }
      );
      const { solveBoard, time: solveTime } = response.data;
      if (!solveBoard?.length) {
        throw new Error("Solver returned an empty or invalid board.");
      }
      setBoard(solveBoard);
      setTime(solveTime);
      setFixedCells(solveBoard.map((row) => row.map(() => true)));

      console.log("Solving board:", board);
    } catch (error) {
      console.error("Error solving puzzle:", error);
      const message = axios.isAxiosError(error)
        ? error.response?.data?.message ||
          error.message ||
          "Failed to solve the puzzle."
        : error instanceof Error
        ? error.message
        : "Failed to solve the puzzle.";
      setErrorSolver(message);
    } finally {
      setIsSolving(false);
    }
  }, [board, form]);

  return (
    <section className='min-h-lvh'>
      <div className='grid grid-cols-[minmax(250px,_1fr)_3fr] gap-4 p-4 w-full min-h-lvh'>
        <div>
          <SudokuGenerator
            onGenerate={handleGenerate}
            isGenerating={isGenerating}
            error={errorGenerator}
            onManualModeChange={handleManualModeChange}
            isManualMode={isManualMode}
            onManualBoardChange={(board) => setBoard(board)} // Add this prop
          />
          <SudokuSolver
            time={time}
            form={form}
            onSubmit={onSubmit}
            isSolving={isSolving}
            hasBoard={board?.length > 0}
            error={errorSolver}
          />
        </div>

        <div className='bg-white shadow-md rounded p-6 w-full ring-2 ring-neutral-200 h-[calc(100lvh-2rem)] grid place-items-center overflow-auto'>
          {board?.length > 0 ? (
            <div className='overflow-auto'>
              <SudokuGrid
                board={board}
                fixedCells={fixedCells}
                currentN={currentN}
                onCellChange={handleCellChange}
                isManualMode={isManualMode}
              />
            </div>
          ) : (
            <div className='flex items-center justify-center h-full rounded-lg'>
              <p className='text-gray-500 text-lg'>
                Generate a puzzle to get started
              </p>
            </div>
          )}
        </div>
      </div>
    </section>
  );
};

export default App;
