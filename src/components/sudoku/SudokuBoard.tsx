import React from "react";

interface SudokuGridProps {
  board: number[][];
  fixedCells: boolean[][];
  currentN: number;
  onCellChange: (
    row: number,
    col: number,
    event: React.ChangeEvent<HTMLInputElement>
  ) => void;
  isManualMode: boolean;
}

interface CellStyleProps {
  i: number;
  j: number;
  currentN: number;
  SIZE: number;
}

const getCellStyle = ({ i, j, currentN, SIZE }: CellStyleProps) => ({
  borderRightWidth: (j + 1) % currentN === 0 && j !== SIZE - 1 ? "2px" : "1px",
  borderRightColor:
    (j + 1) % currentN === 0 && j !== SIZE - 1 ? "#595959" : "gray",
  borderBottomWidth: (i + 1) % currentN === 0 && i !== SIZE - 1 ? "2px" : "1px",
  borderBottomColor:
    (i + 1) % currentN === 0 && i !== SIZE - 1 ? "#595959" : "gray",
});

const SudokuCell: React.FC<{
  cell: number;
  i: number;
  j: number;
  currentN: number;
  SIZE: number;
  onCellChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
}> = ({ cell, i, j, currentN, SIZE, onCellChange }) => {
  const cellStyle = getCellStyle({ i, j, currentN, SIZE });
  const baseClassName = "border border-gray-300 w-10 h-10 text-sm font-medium";

  return onCellChange ? (
    <input
      type='number'
      min='1'
      max={SIZE}
      value={cell === 0 ? "" : cell}
      onChange={onCellChange}
      className={`text-center ${baseClassName}`}
      style={cellStyle}
    />
  ) : (
    <div
      className={`flex items-center justify-center ${baseClassName}`}
      style={cellStyle}
    >
      {cell !== 0 ? cell : ""}
    </div>
  );
};

const SudokuGrid: React.FC<SudokuGridProps> = ({
  board,
  fixedCells,
  currentN,
  onCellChange,
  isManualMode,
}) => {
  const SIZE = currentN * currentN;

  if (!board || !fixedCells) {
    return null;
  }

  return (
    <div className='bg-white shadow-md grid place-items-center rounded p-6 w-full h-full ring-2 ring-neutral-200'>
      <div className='overflow-auto w-fit'>
        <div
          className='grid gap-1 border-2 border-gray-300 p-4 rounded'
          style={{ gridTemplateColumns: `repeat(${SIZE}, 2.5rem)` }}
        >
          {board.map((row, i) =>
            row.map((cell, j) => (
              <SudokuCell
                key={`${i}-${j}`}
                cell={cell}
                i={i}
                j={j}
                currentN={currentN}
                SIZE={SIZE}
                onCellChange={
                  isManualMode || !fixedCells[i][j]
                    ? (e) => onCellChange(i, j, e)
                    : undefined
                }
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default SudokuGrid;
