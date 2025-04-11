import React, { useState } from "react";
import { Button } from "@/components/ui/button";

interface SudokuGeneratorProps {
  onGenerate: (n: number) => void; // Callback to generate the puzzle
  isGenerating: boolean; // Optional: To disable button during generation
  error: string | null;
}

const SudokuGenerator: React.FC<SudokuGeneratorProps> = ({
  onGenerate,
  isGenerating,
  error,
}) => {
  const [n, setN] = useState<number>(3); // Local state for the input value

  const handleGenerateClick = () => {
    onGenerate(n);
  };

  return (
    <div className='bg-white shadow-md rounded p-6 w-full h-min ring-2 ring-neutral-200'>
      <h2 className='text-xl font-semibold'>Sudoku Generator</h2>
      <div className='mb-4'>
        <label className='block text-gray-600 mb-2'>Subgrid Size (n):</label>
        <input
          type='number'
          min='2'
          value={n}
          onChange={(e) => setN(Number(e.target.value))}
          className='w-full p-2 border rounded'
          disabled={isGenerating}
        />
        <p className='text-sm text-gray-500 mt-1'>
          For example, input 3 for a 9×9 sudoku board.
        </p>
      </div>
      <Button
        onClick={handleGenerateClick}
        className='w-full'
        disabled={isGenerating}
      >
        {isGenerating ? "Generating..." : "Generate Sudoku Puzzle"}
      </Button>
      {error && <p className='text-red-500 mt-4'>{error}</p>}
    </div>
  );
};

export default SudokuGenerator;
