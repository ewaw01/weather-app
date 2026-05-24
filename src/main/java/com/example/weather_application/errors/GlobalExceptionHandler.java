package com.example.weather_application.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception e
    ) {
        log.error("(handleException) Handled exception " + e);

        return ResponseEntity.internalServerError().body(
                new ErrorResponseDto(
                        "Something went wrong",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponseDto> handleNoSuchElementException(
            NoSuchElementException e
    ) {
        log.error("(handleNoSuchElementException) Handled exception " + e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponseDto(
                        "Not found",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(exception = {
            HttpClientErrorException.class,
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponseDto> handleHttpClientErrorException(
            Exception e
    ) {
        log.error("(handleHttpClientErrorException) Handled exception " + e.getMessage());

        return ResponseEntity.badRequest().body(
                new ErrorResponseDto(
                        "Incorrect data, bad request",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistException(
            UserAlreadyExistException e
    ) {
        log.error("(handleUserAlreadyExistException) Handled exception " + e.getMessage());

        return ResponseEntity.badRequest().body(
                new ErrorResponseDto(
                    "Bad request",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(exception = {
            NonExistentLocationNameException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponseDto> handleErrorsRelatedToIncorrectDataInHTTP(
            Exception e
    ) {
        log.error("(handleErrorsRelatedToIncorrectDataInHTTP) Handled exception " + e.getMessage());

        return ResponseEntity.badRequest().body(
                new ErrorResponseDto(
                        "Bad request",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

}
