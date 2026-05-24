package com.example.weather_application.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record User (
        @Nullable
        Long id,
        @NotBlank(message = "user id is not null")
        String userId
) {
}
