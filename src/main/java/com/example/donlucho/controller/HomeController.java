package com.example.donlucho.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/info/{page}")
    public String infoPage(@PathVariable String page) {
        return "info/" + page;
    }

    @GetMapping("/game")
    public String game() {
        return "game/index";
    }
}
