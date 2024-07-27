package com.gettimhired.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("status", String.valueOf(ex.getBody().getStatus()));
        errors.put("detail", ex.getBody().getDetail());
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError f) {
                String fieldName = f.getField();
                String errorMessage = f.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
