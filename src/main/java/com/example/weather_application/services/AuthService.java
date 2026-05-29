package com.example.weather_application.services;

import com.example.weather_application.controllers.LoginRequest;
import com.example.weather_application.controllers.RegisterRequest;
import com.example.weather_application.entities.UserEntity;
import com.example.weather_application.errors.UserAlreadyExistException;
import com.example.weather_application.repos.UserRepository;
import com.example.weather_application.security.JwtUtil;
import com.example.weather_application.security.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    public void register(
            @Valid RegisterRequest request
    ) {
        log.info("Register request: {}", request);

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistException("User with email " + request.email() + " already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER);
        user.setUserId(request.username());
        user.setIsBlocked(false);

        userRepository.save(user);
    }

    public String login(
            @Valid LoginRequest request
    ) {
        log.info("Login request: {}", request);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserEntity user = userRepository.findByEmail(request.email()).orElseThrow(
                () -> new NoSuchElementException("User with email " + request.email() + " not found")
        );

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return token;
    }
}
