import React from "react";

interface SudokuGridProps {
  board: number[][];
  fixedCells: boolean[][];
  currentN: number;
  onCellChange: (
    rowIdx: number,
    colIdx: number,
    event: React.ChangeEvent<HTMLInputElement>
  ) => void;
}

const SudokuGrid: React.FC<SudokuGridProps> = ({
  board,
  fixedCells,
  currentN,
  onCellChange,
}) => {
  const SIZE = currentN * currentN;

  return (
    <div className='bg-white shadow-md grid place-items-center rounded p-6 w-full h-full ring-2 ring-neutral-200'>
      {board && fixedCells && (
        <div className='overflow-auto w-fit'>
          <div
            className='grid gap-1 border-2 border-gray-300 p-4 rounded'
            style={{ gridTemplateColumns: `repeat(${SIZE}, 2.5rem)` }}
          >
            {board.map((row, i) =>
              row.map((cell, j) =>
                fixedCells[i][j] ? (
                  <div
                    key={`${i}-${j}`}
                    className='flex items-center justify-center border border-gray-300 w-10 h-10 text-sm font-medium'
                    style={{
                      borderRightWidth:
                        (j + 1) % currentN === 0 && j !== SIZE - 1
                          ? "2px"
                          : "1px",
                      borderRightColor:
                        (j + 1) % currentN === 0 && j !== SIZE - 1
                          ? "#595959"
                          : "gray",
                      borderBottomWidth:
                        (i + 1) % currentN === 0 && i !== SIZE - 1
                          ? "2px"
                          : "1px",
                      borderBottomColor:
                        (i + 1) % currentN === 0 && i !== SIZE - 1
                          ? "#595959"
                          : "gray",
                    }}
                  >
                    {cell !== 0 ? cell : ""}
                  </div>
                ) : (
                  <input
                    key={`${i}-${j}`}
                    type='number'
                    min='1'
                    max={SIZE}
                    value={cell === 0 ? "" : cell}
                    onChange={(e) => onCellChange(i, j, e)}
                    className='text-center border border-gray-300 w-10 h-10 text-sm font-medium'
                    style={{
                      borderRightWidth:
                        (j + 1) % currentN === 0 && j !== SIZE - 1
                          ? "2px"
                          : "1px",
                      borderRightColor:
                        (j + 1) % currentN === 0 && j !== SIZE - 1
                          ? "#595959"
                          : "gray",
                      borderBottomWidth:
                        (i + 1) % currentN === 0 && i !== SIZE - 1
                          ? "2px"
                          : "1px",
                      borderBottomColor:
                        (i + 1) % currentN === 0 && i !== SIZE - 1
                          ? "#595959"
                          : "gray",
                    }}
                  />
                )
              )
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default SudokuGrid;
