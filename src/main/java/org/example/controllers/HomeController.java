/*
 * Authors:
 * Tran Quoc Hung - S4027060 
 * Tran Hoang Linh - S4043097 
 * Le Tuan Hung - S4069761 
 * Nguyen Viet Son - S4052257
 */

package org.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller handling the home page requests.
 */
@Controller
public class HomeController {

    /**
     * Handles the root URL and forwards to the index page.
     * 
     * @return The view name to forward to
     */
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}
