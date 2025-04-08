import React, { useState } from "react";
import { generatePuzzle } from "./generateSudoku";

const App: React.FC = () => {
  // n is the current input, currentN is used for the generated board.
  const [n, setN] = useState<number>(3);
  const [currentN, setCurrentN] = useState<number>(3);
  const [board, setBoard] = useState<number[][] | null>(null);
  // fixedCells tracks which positions were prefilled in the puzzle.
  const [fixedCells, setFixedCells] = useState<boolean[][] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleGenerate = () => {
    const selectedN = n;
    setCurrentN(selectedN);
    try {
      const puzzle = generatePuzzle(selectedN, 0.6);
      setBoard(puzzle);
      // Determine which cells are fixed (nonzero after cell removal).
      const fixed = puzzle.map((row) => row.map((cell) => cell !== 0));
      setFixedCells(fixed);
      setError(null);
      console.log("Generated Sudoku Puzzle:", puzzle);
    } catch (err) {
      console.error(err);
      setError("Error generating Sudoku puzzle.");
    }
  };

  // SIZE is now computed from currentN so it only updates on click
  const SIZE = currentN * currentN;

  const handleCellChange = (
    rowIdx: number,
    colIdx: number,
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const newValue = event.target.value;
    const value = newValue === "" ? 0 : parseInt(newValue, 10);
    // Allow only numbers between 0 and SIZE.
    if (value < 0 || value > SIZE) return;
    if (board) {
      setBoard((prevBoard) =>
        prevBoard!.map((row, i) => {
          if (i === rowIdx) {
            return row.map((cell, j) => (j === colIdx ? value : cell));
          }
          return row;
        })
      );
    }
  };

  return (
    <section className='min-h-lvh '>
      <div className='grid grid-cols-[1fr_4fr] gap-4 p-4 w-full min-h-lvh justify-center'>
        <div className='bg-white shadow-md rounded p-6 w-full max-w-md h-min ring-2 ring-neutral-200'>
          <h2 className='text-xl font-semibold'>Sudoku Generator</h2>
          <div className='mb-4'>
            <label className='block text-gray-600 mb-2'>
              Subgrid Size (n):
            </label>
            <input
              type='number'
              min='2'
              value={n}
              onChange={(e) => setN(Number(e.target.value))}
              className='w-full p-2 border rounded'
            />
            <p className='text-sm text-gray-500 mt-1'>
              For example, input 3 for a 9×9 sudoku board.
            </p>
          </div>
          <button
            onClick={handleGenerate}
            className='w-full bg-neutral-950 hover:bg-neutral-600 text-white font-semibold py-2 rounded'
          >
            Generate Sudoku Puzzle
          </button>
          {error && <p className='text-red-500 mt-4'>{error}</p>}
        </div>
        <div className='bg-white shadow-md grid place-items-center rounded p-6 w-full h-full ring-2 ring-neutral-200'>
          {board && fixedCells && (
            <div className=' overflow-auto w-fit'>
              <div
                className='grid gap-1 border-2 border-gray-500 p-4 rounded'
                style={{ gridTemplateColumns: `repeat(${SIZE}, 2.5rem)` }}
              >
                {board.map((row, i) =>
                  row.map((cell, j) =>
                    fixedCells[i][j] ? (
                      <div
                        key={`${i}-${j}`}
                        className='flex items-center justify-center border border-gray-300 w-10 h-10 text-sm font-medium bg-gray-200'
                        style={{
                          borderRightWidth:
                            (j + 1) % currentN === 0 && j !== SIZE - 1
                              ? "2px"
                              : "1px",
                          borderBottomWidth:
                            (i + 1) % currentN === 0 && i !== SIZE - 1
                              ? "2px"
                              : "1px",
                        }}
                      >
                        {cell}
                      </div>
                    ) : (
                      <input
                        key={`${i}-${j}`}
                        type='number'
                        min='1'
                        max={SIZE}
                        value={cell === 0 ? "" : cell}
                        onChange={(e) => handleCellChange(i, j, e)}
                        className='text-center border border-gray-300 w-10 h-10 text-sm font-medium'
                        style={{
                          borderRightWidth:
                            (j + 1) % currentN === 0 && j !== SIZE - 1
                              ? "2px"
                              : "1px",
                          borderBottomWidth:
                            (i + 1) % currentN === 0 && i !== SIZE - 1
                              ? "2px"
                              : "1px",
                        }}
                      />
                    )
                  )
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </section>
  );
};

export default App;
