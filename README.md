# Sudoku Generator

A Java-based Sudoku puzzle generator that can create valid Sudoku puzzles of various sizes.

## Team Members

| ID       | Student Name |
| -------- | ------------ |
| s4027060 | Hung Tran    |
| s4045097 | Linh Tran    |
| s4052257 | Son Nguyen   |
| s4069761 | Hung Tran    |

## Requirements

- Java JDK 24 or higher
- Maven 3.6 or higher

## Installation

1. Clone the repository:

   ```powershell
   git clone https://github.com/your-username/sudoku-1.git
   cd sudoku-1\sudoku_gen_algo_test
   ```

2. Build the project using Maven:
   ```powershell
   mvn clean install
   ```

## Running the Application

### Generator 1 (Using Bit Masking)

```powershell
mvn exec:java -Dexec.mainClass="org.example.SudokuGenerator_test1"
```

### Generator 2 (Using Backtracking)

```powershell
mvn exec:java -Dexec.mainClass="org.example.SudokuGenerator_test2"
```

## Usage

1. When prompted, enter the base size 'n' (e.g., 3 for a 9x9 Sudoku puzzle)
2. The program will generate a valid Sudoku puzzle and display it
3. For Generator 2, it will also show a partially filled puzzle suitable for solving

## Project Structure

```
sudoku-1/
├── sudoku_gen_algo_test/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── org/
│   │               └── example/
│   │                   ├── SudokuGenerator_test1.java
│   │                   └── SudokuGenerator_test2.java
│   └── pom.xml
└── README.md
```
