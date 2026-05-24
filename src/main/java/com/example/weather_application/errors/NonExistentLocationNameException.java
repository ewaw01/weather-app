package com.example.weather_application.errors;

public class NonExistentLocationNameException extends RuntimeException {
    public NonExistentLocationNameException(String message) {
        super(message);
    }
}
