package com.matedevs.taskinmind.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "Üdv a TaskInMind Backend API-ban! Próbáld ki a /api/tasks végpontot.";
    }

    @GetMapping("/status")
    public String status(){
        return "TaskInMind Backend is running!";
    }
}
