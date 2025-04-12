package com.ducle.authservice.exception;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ExceptionResponse (String message, HttpStatus statusCode, Map<String, List<String>> errors ) {
    public ExceptionResponse(String message, HttpStatus statusCode) {
        this(message, statusCode, Map.of());  
    }
    
   
    public ExceptionResponse(String message, String statusCode, Map<String, List<String>> errors) {
        this(message, HttpStatus.valueOf(Integer.parseInt(statusCode)), errors);  
    }

    
}
