package com.example.weather_application.errors;

public class LocationIsAlreadyIncludedException extends RuntimeException {
    public LocationIsAlreadyIncludedException(String message) {
        super(message);
    }
}
