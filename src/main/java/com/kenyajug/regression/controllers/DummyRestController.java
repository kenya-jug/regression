package com.kenyajug.regression.controllers;

import com.kenyajug.regression.entities.AppLog;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dummy")
public class DummyRestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from DummyRestController!";

    }

}

