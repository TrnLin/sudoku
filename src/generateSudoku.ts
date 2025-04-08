export function generateSudoku(n: number): number[][] {
  const size = n * n;
  const board: number[][] = Array.from({ length: size }, () =>
    Array.from({ length: size }, () => 0)
  );
  if (fillBoard(board, n)) {
    return board;
  } else {
    throw new Error("Failed to generate Sudoku board.");
  }
}

function fillBoard(board: number[][], n: number): boolean {
  const size = n * n;
  for (let row = 0; row < size; row++) {
    for (let col = 0; col < size; col++) {
      if (board[row][col] === 0) {
        // Create a shuffled array of numbers 1 … size.
        const numbers = Array.from({ length: size }, (_, i) => i + 1);
        shuffle(numbers);
        for (const number of numbers) {
          if (isValid(board, row, col, number, n)) {
            board[row][col] = number;
            if (fillBoard(board, n)) {
              return true;
            }
            board[row][col] = 0;
          }
        }
        return false;
      }
    }
  }
  return true;
}

function isValid(
  board: number[][],
  row: number,
  col: number,
  number: number,
  n: number
): boolean {
  const size = n * n;
  // Check row and column.
  for (let i = 0; i < size; i++) {
    if (board[row][i] === number || board[i][col] === number) {
      return false;
    }
  }
  // Check the subgrid.
  const startRow = row - (row % n);
  const startCol = col - (col % n);
  for (let i = 0; i < n; i++) {
    for (let j = 0; j < n; j++) {
      if (board[startRow + i][startCol + j] === number) {
        return false;
      }
    }
  }
  return true;
}

export function generatePuzzle(
  n: number,
  removalPercentage: number = 0.6
): number[][] {
  const board = generateSudoku(n);
  removeCells(board, removalPercentage);
  return board;
}

function removeCells(board: number[][], removalPercentage: number): void {
  const size = board.length;
  const totalCells = size * size;
  const cellsToRemove = Math.floor(totalCells * removalPercentage);
  const positions: { row: number; col: number }[] = [];
  for (let row = 0; row < size; row++) {
    for (let col = 0; col < size; col++) {
      positions.push({ row, col });
    }
  }
  shuffle(positions);
  for (let i = 0; i < cellsToRemove; i++) {
    const { row, col } = positions[i];
    board[row][col] = 0;
  }
}

function shuffle<T>(array: T[]): void {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
}
