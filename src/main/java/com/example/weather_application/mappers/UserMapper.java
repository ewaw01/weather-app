package com.example.weather_application.mappers;

import com.example.weather_application.user.User;
import com.example.weather_application.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getUserId()
        );
    }

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.userId()
        );
    }
}
