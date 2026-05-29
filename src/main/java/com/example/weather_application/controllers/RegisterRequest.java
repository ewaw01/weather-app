package com.example.weather_application.controllers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Email is required")@Email String email,
        @NotBlank(message = "Password is required") String password,
        @NotBlank(message = "Username is required") String username
) {
}
