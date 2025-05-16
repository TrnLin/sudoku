# Sudoku Application

## Contribution Score

| ID       | Student Name     | Score |
|----------|------------------|-------|
| S4027060 | Tran Quoc Hung   | 5     |
| S4043097 | Tran Hoang Linh  | 5     |
| S4052257 | Nguyen Viet Son  | 5     |
| S4069761 | Le Tuan Hung     | 5     |

## Technical Requirements

- Java Development Kit (JDK) 17
- Maven 3.6 or higher (or use the included Maven Wrapper)
- Spring Boot 2.x
- (Optional) IDE such as IntelliJ IDEA, Eclipse, or VS Code

## Running the Application

### Option 1: Run via IDE

1. Open the project as a Maven project in your IDE.
2. Navigate to `src/main/java/org/example/SodokuApplication.java`.
3. Run the `main` method in the `SodokuApplication` class.

### Option 2: Run via Maven

From the project root:

```bash
mvn spring-boot:run
```

### Option 3: Run via Maven Wrapper

On Unix/Linux/Mac:
```bash
./mvnw spring-boot:run
```
On Windows PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
```

Once started, the application will be available at `http://localhost`.

IMPORTANT: Final Algorithm Implementation is in `src/main/java/org/example/services/RMIT_Sudoku_Solver.java`. Researched Algorithm is in `src/main/java/org/example/algorithms`
