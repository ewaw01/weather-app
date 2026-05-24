package com.example.weather_application.user;

public record UserSearchFilter(
        Long id,
        String userId,
        Integer pageNum,
        Integer pageSize
) {
}
