package com.kenyajug.regression.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dummy")
public class DummyRestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from DummyRestController!";

    }
}

