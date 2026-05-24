package com.example.weather_application.errors;

import java.time.LocalDateTime;

public record ErrorResponseDto (
    String message,
    String detailMessage,
    LocalDateTime time
) {
}
