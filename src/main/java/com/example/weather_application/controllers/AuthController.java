package com.example.weather_application.controllers;

import com.example.weather_application.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<String> register(
        @RequestBody @Valid RegisterRequest request
    ) {
        log.info("Registration request: {}", request);
        authService.register(request);
        return ResponseEntity.ok().body("Successfully registration!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
           @RequestBody @Valid LoginRequest request
    ) {
        log.info("Login request: {}", request);
        return ResponseEntity.ok().body("Successfully login!\nYour token: " + authService.login(request));
    }
}
