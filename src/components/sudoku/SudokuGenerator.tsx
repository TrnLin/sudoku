import React, { useState, useCallback } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";

interface SudokuGeneratorProps {
  onGenerate: (n: number, manualBoard?: number[][]) => void;
  isGenerating: boolean;
  error: string | null;
  onManualModeChange: (isManual: boolean) => void;
  isManualMode: boolean;
  onManualBoardChange?: (board: number[][]) => void;
}

const MIN_GRID_SIZE = 2;
const DEFAULT_GRID_SIZE = 3;

const SudokuGenerator: React.FC<SudokuGeneratorProps> = ({
  onGenerate,
  isGenerating,
  error,
  onManualModeChange,
  isManualMode,
  onManualBoardChange,
}) => {
  // Store grid size as a string to allow clearing the input.
  const [n, setN] = useState<string>(DEFAULT_GRID_SIZE.toString());
  // Convert string to number if valid, otherwise use 0.
  const gridSize = parseInt(n, 10) || 0;

  const handleGenerateClick = useCallback(() => {
    if (isManualMode && onManualBoardChange) {
      const size = gridSize * gridSize;
      const newBoard = Array(size)
        .fill(0)
        .map(() => Array(size).fill(0));
      onManualBoardChange(newBoard);
    }
    onGenerate(gridSize);
  }, [gridSize, onGenerate, isManualMode, onManualBoardChange]);

  const handleNChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      const newValue = event.target.value;
      setN(newValue);
    },
    []
  );

  // Disable generate button if input is empty or less than minimum grid size.
  const isGenerateDisabled =
    isGenerating || gridSize < MIN_GRID_SIZE || isNaN(gridSize);

  const handleKeyDown = useCallback(
    (event: React.KeyboardEvent<HTMLInputElement>) => {
      if (event.key === "Enter" && !isGenerateDisabled) {
        handleGenerateClick();
      }
    },
    [handleGenerateClick, isGenerateDisabled]
  );

  const handleModeChange = useCallback(
    (checked: boolean) => {
      onManualModeChange(checked);
    },
    [onManualModeChange]
  );

  return (
    <div className='bg-white shadow-md rounded p-6 w-full h-min ring-2 ring-neutral-200'>
      <h2 className='text-xl font-semibold mb-4'>Sudoku Generator</h2>

      {/* Grid Size Input */}
      <div className='mb-4'>
        <label className='block text-gray-600 mb-2' htmlFor='gridSize'>
          Subgrid Size (n):
        </label>
        <Input
          id='gridSize'
          type='number'
          min={MIN_GRID_SIZE}
          value={n}
          onChange={handleNChange}
          onKeyDown={handleKeyDown}
          disabled={isGenerating}
          placeholder='Enter grid size'
        />
        <p className='text-sm text-gray-500 mt-1'>
          For example, input 3 for a 9×9 sudoku board.
        </p>
      </div>

      {/* Manual Mode Toggle */}
      <div className='mb-6'>
        <label className='flex items-center space-x-2 cursor-pointer'>
          <Checkbox
            checked={isManualMode}
            onCheckedChange={handleModeChange}
            disabled={isGenerating}
          />
          <span className='text-gray-600'>Manual Input Mode</span>
        </label>
      </div>

      {/* Generate Button */}
      <Button
        onClick={handleGenerateClick}
        className='w-full'
        disabled={isGenerateDisabled}
      >
        {isGenerating ? (
          <span className='flex items-center justify-center'>
            <svg
              className='animate-spin -ml-1 mr-3 h-5 w-5 text-white'
              xmlns='http://www.w3.org/2000/svg'
              fill='none'
              viewBox='0 0 24 24'
            >
              <circle
                className='opacity-25'
                cx='12'
                cy='12'
                r='10'
                stroke='currentColor'
                strokeWidth='4'
              ></circle>
              <path
                className='opacity-75'
                fill='currentColor'
                d='M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z'
              ></path>
            </svg>
            Generating...
          </span>
        ) : (
          "Generate Sudoku Puzzle"
        )}
      </Button>

      {/* Error Display */}
      {error && (
        <p className='text-red-500 mt-4 text-sm' role='alert'>
          {error}
        </p>
      )}
    </div>
  );
};

export default SudokuGenerator;
