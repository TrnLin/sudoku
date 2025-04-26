package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Sudoku App");
        model.addAttribute("message", "Welcome to the Sudoku Web Application!");
        return "index";
    }
}
