package com.example.StarterPack.utils;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.StarterPack.utils.Exception.ApiException;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        List<String> generalErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError fieldErr) {
                String fieldName = fieldErr.getField();
                String errorMessage = fieldErr.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                generalErrors.add(error.getDefaultMessage());
            }
        });

        HttpErrorResponse response = HttpErrorResponse.of("Unprocessable entity", 422, errors, generalErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = ApiException.class)
    ResponseEntity<HttpErrorResponse> handleApiException(ApiException ex) {
        System.out.println("--------------------GlobalExceptionHandler.handleApiException-------------------------");
        var response = HttpErrorResponse.of(ex.getMessage(), ex.getStatus(),ex.getErrors(),null);  

        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus()));
    }
}
