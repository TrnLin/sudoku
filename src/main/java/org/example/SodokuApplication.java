package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Sudoku application.
 * This class serves as the entry point for the application.
 */
@SpringBootApplication
public class SodokuApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(SodokuApplication.class, args);
    }
}
